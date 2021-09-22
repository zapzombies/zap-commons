package io.github.zap.commons.keyvaluegetters;

import java.lang.reflect.ParameterizedType;
import java.util.Map;
import java.util.Set;

/**
 * Deserialize string to a desired type using a collection of {@link ValueDeserializer}
 */
public interface DeserializerEngine {
    /**
     * Convert a string to the specified type, using {@link ValueDeserializer}s that this instance manages
     * @param pt type to convert to
     * @param s input string
     * @param <T> result type
     * @return operation result
     */
    <T> OperationResult<T> deserialize(ParameterizedType pt, String s);

    /**
     * Get the matching {@link ValueDeserializer} capable of deserializing to the specified type
     * @param clazz target type
     * @param <T> target type
     * @return the matching {@link ValueDeserializer}
     */
    <T> ValueDeserializer<T> getDeserializerFor(Class<? extends T> clazz);

    /**
     * Add a {@link ValueDeserializer} to this instance
     * @param clazz class to deserialize
     * @param deserializer a {@link ValueDeserializer}
     * @param <T> result type
     */
    <T> void addDeserializer(Class<T> clazz, ValueDeserializer<? extends T> deserializer);

    /**
     * Remove {@link ValueDeserializer}(s) from this instance
     * @param clazz target type
     * @param <T> target type
     */
    <T> void removeDeserializer(Class<T> clazz);

    /**
     * Get all {@link ValueDeserializer}s this instance manages
     * @return a set of {@link ValueDeserializer}s
     */
    Set<Map.Entry<Class<?>, ValueDeserializer<?>>> getAllDeserializers();

    /**
     * Clear all {@link ValueDeserializer}s this class manages
     */
    void clearAllDeserializers();
}

