package org.mashov.suffering.macros;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.mashov.suffering.config.Config;

import java.io.File;
import java.io.IOException;

public class Macro {
    private static final Configuration CONFIGURATION;

    static {
        try {
            CONFIGURATION = new Configuration(Configuration.VERSION_2_3_33);
            CONFIGURATION.setDirectoryForTemplateLoading(new File("src/main/resources/templates/macros"));
            CONFIGURATION.setWhitespaceStripping(true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean validateMacroUsedByTemplate(String name) throws Exception {
        Config config = Config.create("configs/macros/%s.macros.config.json".formatted(name));
        Template template = CONFIGURATION.getTemplate("%s.ftl".formatted(name));

        return validate(config, template);
    }

    private static boolean validate(Config config, Template template) throws Exception {
        freemarker.core.Macro macro = (freemarker.core.Macro) template.getMacros().get(template.getName().replace(".ftl", ""));

        if (macro.getArgumentNames().length != config.get().size()) {
            throw new RuntimeException("invalid macro configuration");
        }

        return true;
    }
}
