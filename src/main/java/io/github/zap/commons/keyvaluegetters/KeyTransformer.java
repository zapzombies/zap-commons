package io.github.zap.commons.keyvaluegetters;

import org.jetbrains.annotations.NotNull;

/**
 * Transforms a field name to a KeyValue compatible name.
 * eg: from camelCase field usually use to SNAKE_CASE in environment variable
 */
public interface KeyTransformer {
    /**
     * Transform field name
     * @param name the name of a field
     * @return KeyValue compatible name
     */
    @NotNull String transform(@NotNull String name);
}
