package io.github.zap.commons.keyvaluegetters.keytransformers;

import io.github.zap.commons.keyvaluegetters.KeyTransformer;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * This class transform camelCase, PascalCase into uppercase,snake_case name
 */
public class EnvironmentVariableKeyTransformer implements KeyTransformer {
    @Override
    public @NotNull String transform(@NotNull String name) {
        Objects.requireNonNull(name, "name cannot be null!");

        StringBuilder nameBuilder = new StringBuilder();
        StringBuilder wordBuilder = new StringBuilder();
        for(char character : name.toCharArray()) {
            if (Character.isUpperCase(character)) {
                if(wordBuilder.isEmpty()) {
                    wordBuilder.append(character);
                } else {
                    if(!nameBuilder.isEmpty())
                        nameBuilder.append("_");
                    nameBuilder.append(wordBuilder.toString().toUpperCase());
                    wordBuilder = new StringBuilder();
                    wordBuilder.append(character);
                }
            } else {
                wordBuilder.append(character);
            }
        }

        if(!nameBuilder.isEmpty())
            nameBuilder.append("_");
        nameBuilder.append(wordBuilder.toString().toUpperCase());
        return nameBuilder.toString();
    }
}
