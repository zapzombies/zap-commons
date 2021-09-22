package io.github.zap.commons.keyvaluegetters;

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
    String transform(String name);
}
