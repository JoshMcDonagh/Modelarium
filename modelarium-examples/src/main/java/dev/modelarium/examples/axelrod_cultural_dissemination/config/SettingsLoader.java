package dev.modelarium.examples.axelrod_cultural_dissemination.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

/** Loads the JSON configuration bundled with the Axelrod cultural dissemination example. */
public final class SettingsLoader {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private SettingsLoader() {}

    public static AxelrodCulturalDisseminationSettings loadAxelrodCulturalDisseminationConfig(String resourcePath) {
        try (InputStream inputStream = SettingsLoader.class.getResourceAsStream("/" + resourcePath)) {
            if (inputStream == null)
                throw new IllegalArgumentException("Config resource not found: " + resourcePath);

            return MAPPER.readValue(inputStream, AxelrodCulturalDisseminationSettings.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read config: " + resourcePath, e);
        }
    }
}
