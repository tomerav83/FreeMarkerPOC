package org.mashov.suffering.templates;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import org.mashov.suffering.config.Config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FTL {
    private static final Configuration CONFIGURATION;

    static {
        try {
            CONFIGURATION = new Configuration(Configuration.VERSION_2_3_33);
            CONFIGURATION.setDirectoryForTemplateLoading(new File("src/main/resources/templates"));
            CONFIGURATION.setWhitespaceStripping(true);
            CONFIGURATION.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void generate(String name) throws Exception {
        Config config = Config.get(name);
        Template template = CONFIGURATION.getTemplate(name);

        File outputDir = new File("generated/" + config.getPackage().replace('.', '/'));
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        // Generate the output file
        File outputFile = new File(outputDir, config.getClassName() + ".java");
        try (FileWriter writer = new FileWriter(outputFile)) {
            template.process(config.getConfig(), writer);
            System.out.println("Generated: " + outputFile.getAbsolutePath());
        }
    }
}
