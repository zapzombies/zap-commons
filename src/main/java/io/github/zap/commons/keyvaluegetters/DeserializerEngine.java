package io.github.zap.commons.keyvaluegetters;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.ParameterizedType;
import java.util.Map;
import java.util.Set;

/**
 * Deserialize string to a desired type using a collection of {@link ValueDeserializer}
 */
public interface DeserializerEngine {
    /**
     * Convert a string to the specified type, using {@link ValueDeserializer}s that this instance manages
     * @param <T> result type
     * @param pt type to convert to
     * @param s input string
     * @return operation result
     */
    <T> @Nullable OperationResult<T> deserialize(@NotNull ParameterizedType pt, @NotNull String s);

    /**
     * Get the matching {@link ValueDeserializer} capable of deserializing to the specified type
     * @param <T> target type
     * @param clazz target type
     * @return the matching {@link ValueDeserializer}
     */
    <T> @Nullable ValueDeserializer<T> getDeserializerFor(@NotNull Class<? extends T> clazz);

    /**
     * Add a {@link ValueDeserializer} to this instance
     * @param <T> result type
     * @param clazz class to deserialize
     * @param deserializer a {@link ValueDeserializer}
     */
    <T> void addDeserializer(@NotNull Class<T> clazz, @Nullable ValueDeserializer<? extends T> deserializer);

    /**
     * Remove {@link ValueDeserializer}(s) from this instance
     * @param <T> target type
     * @param clazz target type
     */
    <T> void removeDeserializer(@NotNull Class<T> clazz);

    /**
     * Get all {@link ValueDeserializer}s this instance manages
     * @return a set of {@link ValueDeserializer}s
     */
    @NotNull Set<Map.Entry<Class<?>, ValueDeserializer<?>>> getAllDeserializers();

    /**
     * Clear all {@link ValueDeserializer}s this class manages
     */
    void clearAllDeserializers();
}

