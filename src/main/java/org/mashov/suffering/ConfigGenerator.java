package org.mashov.suffering;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ConfigGenerator {
    private static final String NAME = "basic.ftl";
    private static final String FTL = "src/main/resources/templates/%s".formatted(NAME);
    private static final String OUTPUT = "src/main/resources/configs/%s.config.json".formatted(NAME);

    public static void main(String[] args) {
        try {
            String templateContent = Files.readString(Paths.get(FTL));
            Map<String, Map<String, String>> allMacros = new LinkedHashMap<>();
            Set<String> allPlaceholders = new LinkedHashSet<>();
            Set<String> loopVariables = new HashSet<>();

            // Process the main template
            processTemplate(templateContent, FTL, allMacros, allPlaceholders, loopVariables);

            // Remove loop variables from placeholders
            allPlaceholders.removeAll(loopVariables);

            // Generate the configuration file
            generateConfigFile(allPlaceholders, OUTPUT);
        } catch (IOException e) {
            System.out.println("Error reading template file: " + e.getMessage());
        }
    }

    private static void processTemplate(String templateContent, String baseFilePath,
                                        Map<String, Map<String, String>> allMacros, Set<String> allPlaceholders, Set<String> loopVariables) throws IOException {
        // Extract macros and placeholders from the current template
        allMacros.putAll(extractMacros(templateContent));
        allPlaceholders.addAll(extractPlaceholders(templateContent));

        // Extract loop variables
        loopVariables.addAll(extractLoopVariables(templateContent));

        // Process <#import> directives
        processImports(templateContent, baseFilePath, allMacros, allPlaceholders, loopVariables);
    }

    private static void processImports(String templateContent, String baseFilePath,
                                       Map<String, Map<String, String>> allMacros, Set<String> allPlaceholders, Set<String> loopVariables) throws IOException {
        // Pattern to match <#import "file.ftl" as alias> directives
        Pattern importPattern = Pattern.compile("<#import\\s+\"([^\"]+)\"\\s+as\\s+(\\w+)>");
        Matcher importMatcher = importPattern.matcher(templateContent);

        while (importMatcher.find()) {
            String importPath = importMatcher.group(1);
            String alias = importMatcher.group(2);

            // Resolve the import path relative to the current template file
            Path resolvedPath = Paths.get(baseFilePath).getParent().resolve(importPath).normalize();
            if (!Files.exists(resolvedPath)) {
                System.out.println("Warning: Imported file not found - " + resolvedPath);
                continue;
            }

            String importedContent = Files.readString(resolvedPath);
            Map<String, Map<String, String>> importedMacros = extractMacros(importedContent);

            // Prefix macros with the alias
            for (Map.Entry<String, Map<String, String>> entry : importedMacros.entrySet()) {
                allMacros.put(alias + "." + entry.getKey(), entry.getValue());
            }

            // Extract placeholders and loop variables from the imported template
            allPlaceholders.addAll(extractPlaceholders(importedContent));
            loopVariables.addAll(extractLoopVariables(importedContent));
        }
    }

    private static Map<String, Map<String, String>> extractMacros(String templateContent) {
        Map<String, Map<String, String>> macros = new LinkedHashMap<>();

        // Pattern to match macros and their default parameters
        Pattern macroPattern = Pattern.compile("<#macro\\s+(\\w+)\\s*(.*?)>");
        Pattern paramPattern = Pattern.compile("(\\w+)=(\"[^\"]*\"|\\S+)");

        Matcher macroMatcher = macroPattern.matcher(templateContent);
        while (macroMatcher.find()) {
            String macroName = macroMatcher.group(1);
            String paramString = macroMatcher.group(2);

            Map<String, String> params = new LinkedHashMap<>();
            Matcher paramMatcher = paramPattern.matcher(paramString);
            while (paramMatcher.find()) {
                String paramName = paramMatcher.group(1);
                String paramValue = paramMatcher.group(2).replace("\"", ""); // Remove quotes
                params.put(paramName, paramValue);
            }
            macros.put(macroName, params);
        }
        return macros;
    }

    private static Set<String> extractPlaceholders(String templateContent) {
        Set<String> placeholders = new LinkedHashSet<>();
        // Pattern to match placeholders like ${variable}, ${variable?something}, or ${variable["key"]}
        Pattern placeholderPattern = Pattern.compile("\\$\\{(\\w+)(?:\\?\\w+|\\[.*?\\])?}");

        Matcher placeholderMatcher = placeholderPattern.matcher(templateContent);
        while (placeholderMatcher.find()) {
            String placeholder = placeholderMatcher.group(1); // Extract the variable name
            placeholders.add(placeholder);
        }
        return placeholders;
    }

    private static Set<String> extractLoopVariables(String templateContent) {
        Set<String> loopVariables = new HashSet<>();
        // Pattern to match <#list items as item>
        Pattern listPattern = Pattern.compile("<#list\\s+\\w+\\s+as\\s+(\\w+)>");
        Matcher listMatcher = listPattern.matcher(templateContent);

        while (listMatcher.find()) {
            String loopVariable = listMatcher.group(1); // Extract the loop variable name
            loopVariables.add(loopVariable);
        }
        return loopVariables;
    }

    private static void generateConfigFile(Set<String> placeholders, String outputFilePath) {
        Map<String, Object> config = new LinkedHashMap<>();

        Map<String, String> placeholderDefaults = new LinkedHashMap<>();
        for (String placeholder : placeholders) {
            placeholderDefaults.put(placeholder, "default_value");
        }
        config.put("placeholders", placeholderDefaults);

        // Write config to JSON file
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (Writer writer = new FileWriter(outputFilePath)) {
            gson.toJson(config, writer);
            System.out.println("Config file generated: " + outputFilePath);
        } catch (IOException e) {
            System.out.println("Error writing config file: " + e.getMessage());
        }
    }
}

