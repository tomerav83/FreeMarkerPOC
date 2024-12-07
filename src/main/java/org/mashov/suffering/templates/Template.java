package org.mashov.suffering.templates;

import freemarker.core.Macro;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import org.mashov.suffering.config.Config;
import org.mashov.suffering.macros.validation.MacroValidator;

import java.io.File;
import java.io.IOException;

public class Template {
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

    public static Template get(String name) throws Exception {
        MacroValidator.validate(name);

        freemarker.template.Template template = CONFIGURATION.getTemplate(name);

        return new Template();
    }

    private static boolean validate(Config config, freemarker.template.Template template) throws Exception {
        Macro macro = (Macro) template.getMacros().get(template.getName().replace(".ftl", ""));
        return macro.getArgumentNames().length == config.get().size();
    }
}
