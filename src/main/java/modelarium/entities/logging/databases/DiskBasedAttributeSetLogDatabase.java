package modelarium.entities.logging.databases;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private static volatile boolean shutdownHookRegistered = false;

    /** Shared mapper; ObjectMapper is thread-safe after configuration. */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final String ATTRIBUTES_TABLE_NAME = "attributes_table";

    /**
     * Per-series class map for deserialisation.
     * Concurrent map is used because callers may hit a single database instance from
     * multiple threads, even though DB access itself is additionally guarded by dbLock.
     */
    private final Map<String, Class<?>> attributeClassesMap = new ConcurrentHashMap<>();

    /** Guards connection lifecycle and all database operations for this instance. */
    private final Object dbLock = new Object();

    private Connection connection;

    private static Class<?> firstNonNullClass(List<?> values) {
        if (values == null)
            return null;

        for (Object value : values) {
            if (value != null)
                return value.getClass();
        }

        return null;
    }

    private static String createTempDatabasePath() {
        String folderName = "temp_" + RandomStringGenerator.generateUniqueRandomString(20);
        Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"), folderName);
        return tempDir.resolve(RandomStringGenerator.generateUniqueRandomString(20) + ".db").toString();
    }

    /** Registers this instance for automatic disconnect on JVM shutdown. */
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

    @Override
    public <T> void addAttributeValue(String attributeName, T attributeValue) {
        Objects.requireNonNull(attributeName, "attributeName must not be null");
        rememberType(attributeClassesMap, attributeName, attributeValue);
        addSeriesValue(ATTRIBUTES_TABLE_NAME, attributeName, attributeValue);
    }

    // === Bulk Column Replacement ===

    @Override
    public void setAttributeColumn(String attributeName, List<Object> attributeValues) {
        Objects.requireNonNull(attributeName, "attributeName must not be null");

        Class<?> inferred = firstNonNullClass(attributeValues);
        if (inferred != null) {
            attributeClassesMap.put(attributeName, inferred);
        }

        replaceSeries(ATTRIBUTES_TABLE_NAME, attributeName,
                attributeValues == null ? Collections.emptyList() : attributeValues);
    }

    // === Column Retrieval ===

    @Override
    public List<Object> getAttributeColumnAsList(String attributeName) {
        Objects.requireNonNull(attributeName, "attributeName must not be null");
        return retrieveSeries(ATTRIBUTES_TABLE_NAME, attributeName, attributeClassesMap.get(attributeName));
    }

    // === Database/Table Management ===

    private void configureConnection(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
            stmt.execute("PRAGMA journal_mode = WAL;");
            stmt.execute("PRAGMA synchronous = NORMAL;");
            stmt.execute("PRAGMA temp_store = MEMORY;");
            stmt.execute("PRAGMA busy_timeout = 5000;");
        }
    }

    private void createAttributeTable() {
        createSeriesTable(ATTRIBUTES_TABLE_NAME);
    }

    /**
     * Stable schema:
     * - series_name: attribute name
     * - position_index: preserves order within that series
     * - value_json: serialised value
     *
     * Composite primary key ensures one value per position in a given series.
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

    private void rememberType(Map<String, Class<?>> typeMap, String name, Object value) {
        if (value != null)
            typeMap.put(name, value.getClass());
    }

    private void ensureConnected() {
        if (connection == null)
            throw new IllegalStateException("Database connection has not been established. Call connect() first.");
    }

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

                Files.deleteIfExists(normalisedParent);
            }
        } catch (Exception e) {
            System.err.println("Failed to clean up parent temp directory: " + parent + " (" + e.getMessage() + ")");
        }
    }

    private boolean isDirectoryEmpty(Path directory) throws IOException {
        try (var entries = Files.list(directory)) {
            return entries.findAny().isEmpty();
        }
    }

    // === JSON (De)serialisation Utilities ===

    private static String serialiseValue(Object value) {
        if (value == null)
            return null;

        try {
            return OBJECT_MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serialising value: " + e.getMessage(), e);
        }
    }

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