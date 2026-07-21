package dev.modelarium.examples.sirbasic.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

public final class SettingsLoader {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private SettingsLoader() {}

    public static SIRSettings loadSIRConfig(String resourcePath) {
        try (InputStream is = SettingsLoader.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null)
                throw new IllegalArgumentException("Config resource not found: " + resourcePath);
            return MAPPER.readValue(is, SIRSettings.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read config: " + resourcePath, e);
        }
    }
}
