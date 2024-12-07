package org.mashov.suffering.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public class Config {
    private static final ObjectMapper OM = new ObjectMapper();
    private final Map<String, String> config;
    private final String json;

    public static Config get(String resourceFileName) {
        String json = readFile(resourceFileName);

        return new Config(readJsonToMap(json), json);
    }

    private static String readFile(String resourceFileName) {
        try (InputStream inputStream = Config.class.getClassLoader().getResourceAsStream(
                "configs/" + resourceFileName + ".config.json")) {
            if (inputStream == null) {
                System.err.println("Resource file not found: " + resourceFileName);
                return null;
            }

            return new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining());
        } catch (IOException e) {
            return null;
        }
    }

    private static Map<String, String> readJsonToMap(String content) {
        return new Gson().fromJson(content, JsonObject.class)
                .getAsJsonObject("placeholders")
                .asMap()
                .entrySet()
                .stream()
                .map(entry -> Map.entry(entry.getKey(), entry.getValue().getAsString()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public String getPackage() {
        return config.get("packageName");
    }

    public String getClassName() {
        return config.get("className");
    }
}
