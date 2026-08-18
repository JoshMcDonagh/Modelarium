package modelarium.entities.logging.databases;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import modelarium.utils.Cloners;
import modelarium.utils.RandomStringGenerator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Concrete implementation of {@link AttributeSetLogDatabase} that stores results
 * in an SQLite database file.
 *
 * <p>This implementation uses a stable row-based schema internally instead of
 * dynamically altering table columns.
 *
 * <p>Each logical attribute "column" is stored as a named ordered series:
 * <pre>
 *     (series_name, position_index, value_json)
 * </pre>
 */
public class DiskBasedAttributeSetLogDatabase extends AttributeSetLogDatabase {

    /** Thread-safe list of currently active databases to auto-disconnect on JVM shutdown. */
    private static final List<DiskBasedAttributeSetLogDatabase> activeDatabases =
            Collections.synchronizedList(new ArrayList<>());

    /** Whether the JVM shutdown hook that disconnects active databases has been registered */
    private static volatile boolean shutdownHookRegistered = false;

    /** Shared mapper; ObjectMapper is thread-safe after configuration. */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /** The name of the table attribute series are stored in */
    private static final String ATTRIBUTES_TABLE_NAME = "attributes_table";

    /**
     * Per-series class map for deserialisation.
     * Concurrent map is used because callers may hit a single database instance from
     * multiple threads, even though DB access itself is additionally guarded by dbLock.
     */
    private final Map<String, Class<?>> attributeClassesMap = new ConcurrentHashMap<>();

    /** Guards connection lifecycle and all database operations for this instance. */
    private final Object dbLock = new Object();

    /** The SQLite connection this database uses, or null while disconnected */
    private Connection connection;

    /**
     * Returns the class of the first non-null value in a list.
     *
     * @param values the values to inspect
     * @return the class of the first non-null value, or null if there is none
     */
    private static Class<?> firstNonNullClass(List<?> values) {
        if (values == null)
            return null;

        for (Object value : values) {
            if (value != null)
                return value.getClass();
        }

        return null;
    }

    /**
     * Creates a unique path for the database's backing file inside the system's temporary directory.
     *
     * @return a new database file path within a uniquely named temporary folder
     */
    private static String createTempDatabasePath() {
        String folderName = "temp_" + RandomStringGenerator.generateUniqueRandomString(20);
        Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"), folderName);
        return tempDir.resolve(RandomStringGenerator.generateUniqueRandomString(20) + ".db").toString();
    }

    /**
     * Constructs a new disk-based attribute set log database backed by a file in the system's temporary directory.
     *
     * <p>The instance is registered for automatic disconnect on JVM shutdown, with the shutdown hook created the
     * first time any instance is constructed.
     */
    public DiskBasedAttributeSetLogDatabase() {
        super(createTempDatabasePath());

        synchronized (activeDatabases) {
            activeDatabases.add(this);

            if (!shutdownHookRegistered) {
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    List<DiskBasedAttributeSetLogDatabase> snapshot;
                    synchronized (activeDatabases) {
                        snapshot = new ArrayList<>(activeDatabases);
                    }

                    for (DiskBasedAttributeSetLogDatabase db : snapshot) {
                        try {
                            db.disconnect();
                        } catch (Exception e) {
                            System.err.println(
                                    "Error while disconnecting database during shutdown: " + e.getMessage()
                            );
                        }
                    }
                }));

                shutdownHookRegistered = true;
            }
        }
    }

    /**
     * Establishes an SQLite connection and creates the required table.
     */
    @Override
    public void connect() {
        synchronized (dbLock) {
            if (connection != null)
                return;

            try {
                Path dbPath = Paths.get(getDatabasePath());
                Files.createDirectories(dbPath.getParent());

                connection = DriverManager.getConnection("jdbc:sqlite:" + getDatabasePath());
                configureConnection(connection);
                createAttributeTable();
            } catch (SQLException e) {
                connection = null;
                throw new RuntimeException("Failed to establish SQLite connection: " + e.getMessage(), e);
            } catch (IOException e) {
                throw new RuntimeException("Failed to create database directory: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Closes the SQLite connection and deletes the database file.
     */
    @Override
    public void disconnect() {
        synchronized (dbLock) {
            synchronized (activeDatabases) {
                try {
                    if (connection != null) {
                        try (Statement stmt = connection.createStatement()) {
                            stmt.execute("PRAGMA wal_checkpoint(TRUNCATE);");
                            stmt.execute("PRAGMA journal_mode = DELETE;");
                        } catch (SQLException e) {
                            System.err.println("Error finalising WAL before close: " + e.getMessage());
                        }

                        try {
                            connection.close();
                        } catch (SQLException e) {
                            System.err.println("Error closing SQLite connection: " + e.getMessage());
                        }
                    }

                    deleteDatabaseFileAndMaybeParentDirectory();
                } finally {
                    activeDatabases.remove(this);
                    connection = null;
                }
            }
        }
    }

    // === Attribute Value Recording (Per-Tick) ===

    /**
     * Appends a value to the named attribute's stored series, remembering the value's class for later
     * deserialisation.
     *
     * @param attributeName the name of the attribute the value belongs to
     * @param attributeValue the value to append
     * @param <T> the type of the value being appended
     */
    @Override
    public <T> void addAttributeValue(String attributeName, T attributeValue) {
        Objects.requireNonNull(attributeName, "attributeName must not be null");
        rememberType(attributeClassesMap, attributeName, attributeValue);
        addSeriesValue(ATTRIBUTES_TABLE_NAME, attributeName, Cloners.standard().deepClone(attributeValue));
    }

    // === Bulk Column Replacement ===

    /**
     * Replaces the named attribute's stored series with the given values, inferring the attribute's value type from
     * the first non-null value.
     *
     * @param attributeName the name of the attribute the values belong to
     * @param attributeValues the values to store as the attribute's series
     */
    @Override
    public void setAttributeColumn(String attributeName, List<Object> attributeValues) {
        Objects.requireNonNull(attributeName, "attributeName must not be null");

        Class<?> inferred = firstNonNullClass(attributeValues);
        if (inferred != null) {
            attributeClassesMap.put(attributeName, inferred);
        }

        replaceSeries(ATTRIBUTES_TABLE_NAME, attributeName,
                attributeValues == null ? Collections.emptyList() : Cloners.standard().deepClone(attributeValues));
    }

    // === Column Retrieval ===

    /**
     * Retrieves the named attribute's stored series, deserialising each value to the attribute's remembered class.
     *
     * @param attributeName the name of the attribute whose series to retrieve
     * @return the attribute's stored values in insertion order
     */
    @Override
    public List<Object> getAttributeColumnAsList(String attributeName) {
        Objects.requireNonNull(attributeName, "attributeName must not be null");
        return retrieveSeries(ATTRIBUTES_TABLE_NAME, attributeName, attributeClassesMap.get(attributeName));
    }

    // === Database/Table Management ===

    /**
     * Applies the SQLite pragmas this database uses for safe and performant access.
     *
     * @param connection the newly opened connection to configure
     */
    private void configureConnection(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
            stmt.execute("PRAGMA journal_mode = WAL;");
            stmt.execute("PRAGMA synchronous = NORMAL;");
            stmt.execute("PRAGMA temp_store = MEMORY;");
            stmt.execute("PRAGMA busy_timeout = 5000;");
        }
    }

    /**
     * Creates the table attribute series are stored in, if it does not already exist.
     */
    private void createAttributeTable() {
        createSeriesTable(ATTRIBUTES_TABLE_NAME);
    }

    /**
     * Creates a series table with the database's stable schema, if it does not already exist.
     *
     * <p>The schema stores each logical attribute "column" as a named ordered series of rows: series_name (the
     * attribute's name), position_index (preserving order within that series) and value_json (the serialised
     * value). The composite primary key ensures one value per position in a given series.
     *
     * @param tableName the name of the table to create
     */
    private void createSeriesTable(String tableName) {
        String createTableSql =
                "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                        "series_name TEXT NOT NULL, " +
                        "position_index INTEGER NOT NULL, " +
                        "value_json TEXT, " +
                        "PRIMARY KEY (series_name, position_index)" +
                        ");";

        String createIndexSql =
                "CREATE INDEX IF NOT EXISTS idx_" + tableName + "_series_position " +
                        "ON " + tableName + " (series_name, position_index);";

        synchronized (dbLock) {
            ensureConnected();

            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate(createTableSql);
                stmt.executeUpdate(createIndexSql);
            } catch (SQLException e) {
                throw new RuntimeException("Error creating table '" + tableName + "': " + e.getMessage(), e);
            }
        }
    }

    // === Internal Write Operations ===

    /**
     * Appends a serialised value to the named series at the next position index.
     *
     * @param tableName the name of the table the series is stored in
     * @param seriesName the name of the series the value belongs to
     * @param value the value to serialise and append
     * @param <T> the type of the value being appended
     */
    private <T> void addSeriesValue(String tableName, String seriesName, T value) {
        synchronized (dbLock) {
            ensureConnected();

            String sql = "INSERT INTO " + tableName + " (series_name, position_index, value_json) VALUES (?, ?, ?);";

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                int nextIndex = getNextPositionIndex(tableName, seriesName);
                stmt.setString(1, seriesName);
                stmt.setInt(2, nextIndex);
                stmt.setString(3, serialiseValue(value));
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(
                        "Error inserting value into '" + tableName + "' for series '" + seriesName + "': " + e.getMessage(),
                        e
                );
            }
        }
    }

    /**
     * Replaces the named series with the given values inside a single transaction, rolling back if any insert
     * fails.
     *
     * @param tableName the name of the table the series is stored in
     * @param seriesName the name of the series to replace
     * @param values the values to serialise and store as the series
     */
    private void replaceSeries(String tableName, String seriesName, List<Object> values) {
        synchronized (dbLock) {
            ensureConnected();

            String deleteSql = "DELETE FROM " + tableName + " WHERE series_name = ?;";
            String insertSql = "INSERT INTO " + tableName + " (series_name, position_index, value_json) VALUES (?, ?, ?);";

            boolean originalAutoCommit;
            try {
                originalAutoCommit = connection.getAutoCommit();
                connection.setAutoCommit(false);

                try (PreparedStatement deleteStmt = connection.prepareStatement(deleteSql);
                     PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {

                    deleteStmt.setString(1, seriesName);
                    deleteStmt.executeUpdate();

                    for (int i = 0; i < values.size(); i++) {
                        insertStmt.setString(1, seriesName);
                        insertStmt.setInt(2, i);
                        insertStmt.setString(3, serialiseValue(values.get(i)));
                        insertStmt.addBatch();
                    }

                    insertStmt.executeBatch();
                    connection.commit();
                } catch (SQLException e) {
                    connection.rollback();
                    throw e;
                } finally {
                    connection.setAutoCommit(originalAutoCommit);
                }
            } catch (SQLException e) {
                throw new RuntimeException(
                        "Error replacing series '" + seriesName + "' in '" + tableName + "': " + e.getMessage(),
                        e
                );
            }
        }
    }

    /**
     * Returns the next free position index for the named series.
     *
     * @param tableName the name of the table the series is stored in
     * @param seriesName the name of the series to find the next position for
     * @return one greater than the series' highest stored position index, or 0 for a new series
     */
    private int getNextPositionIndex(String tableName, String seriesName) throws SQLException {
        String sql = "SELECT COALESCE(MAX(position_index), -1) + 1 FROM " + tableName + " WHERE series_name = ?;";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, seriesName);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return 0;
    }

    // === Internal Read Operations ===

    /**
     * Retrieves the named series in position order, deserialising each value to the given class.
     *
     * @param tableName the name of the table the series is stored in
     * @param seriesName the name of the series to retrieve
     * @param type the class to deserialise each value to, or null to return the raw serialised strings
     * @return the series' values in position order
     */
    private List<Object> retrieveSeries(String tableName, String seriesName, Class<?> type) {
        synchronized (dbLock) {
            ensureConnected();

            String sql =
                    "SELECT value_json " +
                            "FROM " + tableName + " " +
                            "WHERE series_name = ? " +
                            "ORDER BY position_index ASC;";

            List<Object> results = new ArrayList<>();

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, seriesName);

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String value = rs.getString("value_json");
                        if (value != null) {
                            results.add(type != null ? deserialiseValue(value, type) : value);
                        }
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(
                        "Error retrieving series '" + seriesName + "' from '" + tableName + "': " + e.getMessage(),
                        e
                );
            }

            return results;
        }
    }

    // === Utility ===

    /**
     * Records the class of a value against its series name for later deserialisation, if the value is non-null.
     *
     * @param typeMap the map of series names to value classes to record into
     * @param name the name of the series the value belongs to
     * @param value the value whose class to record
     */
    private void rememberType(Map<String, Class<?>> typeMap, String name, Object value) {
        if (value != null)
            typeMap.put(name, value.getClass());
    }

    /**
     * Checks that the database connection has been established, failing fast if it has not.
     */
    private void ensureConnected() {
        if (connection == null)
            throw new IllegalStateException("Database connection has not been established. Call connect() first.");
    }

    /**
     * Deletes the database's backing file, and deletes its parent directory too if that directory lives inside the
     * system's temporary directory.
     */
    private void deleteDatabaseFileAndMaybeParentDirectory() {
        String databasePathString = getDatabasePath();
        if (databasePathString == null || databasePathString.isBlank())
            return;

        Path databasePath = Paths.get(databasePathString);

        try {
            Files.deleteIfExists(databasePath);
        } catch (IOException e) {
            System.err.println("Failed to delete database file: " + databasePath + " (" + e.getMessage() + ")");
        }

        Path parent = databasePath.getParent();
        if (parent == null)
            return;

        try {
            Path systemTempDir = Paths.get(System.getProperty("java.io.tmpdir")).toAbsolutePath().normalize();
            Path normalisedParent = parent.toAbsolutePath().normalize();

            if (normalisedParent.startsWith(systemTempDir)) {
                try (var entries = Files.list(normalisedParent)) {
                    entries.forEach(file -> {
                        try {
                            Files.deleteIfExists(file);
                        } catch (IOException ex) {
                            System.err.println("Failed to delete file: " + file + " (" + ex.getMessage() + ")");
                        }
                    });
                }

                IOException lastError = null;
                for (int attempt = 0; attempt < 5; attempt++) {
                    try {
                        Files.deleteIfExists(normalisedParent);
                        lastError = null;
                        break;
                    } catch (IOException e) {
                        lastError = e;
                        System.gc();
                        try {
                            Thread.sleep(30);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }
                if (lastError != null)
                    throw lastError;
            }
        } catch (Exception e) {
            System.err.println("Failed to clean up parent temp directory: " + parent + " (" + e.getMessage() + ")");
        }
    }

    // === JSON (De)serialisation Utilities ===

    /**
     * Serialises a value to its JSON representation.
     *
     * @param value the value to serialise
     * @return the value's JSON string, or null if the value is null
     */
    private static String serialiseValue(Object value) {
        if (value == null)
            return null;

        try {
            return OBJECT_MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serialising value: " + e.getMessage(), e);
        }
    }

    /**
     * Deserialises a JSON string back to a value of the given class.
     *
     * @param value the JSON string to deserialise
     * @param type the class to deserialise the value to
     * @return the deserialised value, or null if the string is null
     */
    private Object deserialiseValue(String value, Class<?> type) {
        if (value == null)
            return null;

        if (type == null)
            throw new IllegalArgumentException("Cannot deserialise: type is null");

        try {
            return OBJECT_MAPPER.readValue(value, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(
                    "Error deserialising value: " + value + " with type: " + type.getName(),
                    e
            );
        }
    }
}