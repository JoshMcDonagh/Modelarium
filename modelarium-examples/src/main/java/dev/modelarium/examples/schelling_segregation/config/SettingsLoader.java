package dev.modelarium.examples.schelling_segregation.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

/** Loads the configuration bundled with the Schelling segregation example. */
public final class SettingsLoader {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private SettingsLoader() {}

    public static SchellingSegregationSettings loadSchellingSegregationConfig(String resourcePath) {
        try (InputStream inputStream = SettingsLoader.class.getResourceAsStream("/" + resourcePath)) {
            if (inputStream == null)
                throw new IllegalArgumentException("Config resource not found: " + resourcePath);

            return MAPPER.readValue(inputStream, SchellingSegregationSettings.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read config: " + resourcePath, e);
        }
    }
}
