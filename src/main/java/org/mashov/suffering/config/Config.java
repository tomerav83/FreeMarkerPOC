package org.mashov.suffering.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.stream.Collectors;

public class Config {
    private static final ObjectMapper OM = new ObjectMapper();
    private final Map<String, String> config;

    public Config(Map<String, String> config) {
        this.config = config;
    }

    public static Config create(String resourceFileName) {
        return new Config(readJsonToMap(resourceFileName));
    }

    private static Map<String, String> readJsonToMap(String resourceFileName) {
        try (InputStream inputStream = Config.class.getClassLoader().getResourceAsStream(resourceFileName)) {
            if (inputStream == null) {
                System.err.println("Resource file not found: " + resourceFileName);
                return null;
            }

            // Parse JSON to Map
            return OM.readValue(inputStream, new TypeReference<Map<String, Object>>() {})
                    .entrySet()
                    .stream()
                    .map(entry -> Map.entry(entry.getKey(), entry.getValue().toString()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        } catch (IOException e) {
            return null;
        }
    }

    public Map<String, String> get() {
        return config;
    }

    public <T> T get(String property, TypeReference<T> typeReference) throws JsonProcessingException {
        return OM.readValue(config.get(property), typeReference);
    }
}
