package io.github.zap.commons.keyvaluegetters;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.ParameterizedType;

/**
 * Base class for all keyvaluegetter deserializer
 * @param <T> the return type
 */
@FunctionalInterface
public interface ValueDeserializer<T> {
    @NotNull OperationResult<T> deserialize(@NotNull String value, @NotNull ParameterizedType pt, @NotNull DeserializerEngine engine);
}
