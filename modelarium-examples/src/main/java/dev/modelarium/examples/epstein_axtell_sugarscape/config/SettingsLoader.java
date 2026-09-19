package dev.modelarium.examples.epstein_axtell_sugarscape.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

/** Loads the Sugarscape experiment-suite settings from a classpath JSON resource. */
public final class SettingsLoader {
    private SettingsLoader() {}

    public static SugarscapeSettings load(String resource) {
        try (InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource)) {
            if (stream == null)
                throw new IllegalArgumentException("No resource found: " + resource);
            return new ObjectMapper().readValue(stream, SugarscapeSettings.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read Sugarscape config: " + resource, e);
        }
    }
}
