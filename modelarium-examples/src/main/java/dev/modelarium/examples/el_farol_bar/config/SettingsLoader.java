package dev.modelarium.examples.el_farol_bar.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

/** Loads the configuration bundled with the El Farol Bar example. */
public final class SettingsLoader {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private SettingsLoader() {}

    public static ElFarolBarSettings loadElFarolBarConfig(String resourcePath) {
        try (InputStream inputStream = SettingsLoader.class.getResourceAsStream("/" + resourcePath)) {
            if (inputStream == null)
                throw new IllegalArgumentException("Config resource not found: " + resourcePath);

            return MAPPER.readValue(inputStream, ElFarolBarSettings.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read config: " + resourcePath, e);
        }
    }
}
