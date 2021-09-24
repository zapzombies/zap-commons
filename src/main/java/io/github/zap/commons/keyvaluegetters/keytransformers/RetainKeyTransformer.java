package io.github.zap.commons.keyvaluegetters.keytransformers;

import io.github.zap.commons.keyvaluegetters.KeyTransformer;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Returns the original name
 */
public class RetainKeyTransformer implements KeyTransformer {
    @Override
    public @NotNull String transform(@NotNull String name) {
        Objects.requireNonNull(name, "name cannot be null!");
        return name;
    }
}
