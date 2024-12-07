package org.mashov.suffering.macros.validation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.mashov.suffering.config.Config;
import org.mashov.suffering.macros.Macro;

import java.util.List;

public class MacroValidator {
    public static void validate(String name) throws Exception {
        List<String> macros = Config.create("configs/%s.config.json".formatted(name))
                .get("macros", new TypeReference<List<String>>() {});

        for (String macro : macros) {
            Macro.validateMacroUsedByTemplate(macro);
        }
    }
}
