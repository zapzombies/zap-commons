package io.github.zap.commons.keyvaluegetters.deserializers;

import io.github.zap.commons.keyvaluegetters.DeserializerEngine;
import io.github.zap.commons.keyvaluegetters.OperationResult;
import io.github.zap.commons.keyvaluegetters.ValueDeserializer;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.ParameterizedType;
import java.util.Objects;

/**
 * Deserialize {@link String} to itself :). Only created to conform with library spec
 */
public class StringDeserializer implements ValueDeserializer<String> {
    @Override
    public @NotNull OperationResult<String> deserialize(
            @NotNull String value,
            @NotNull ParameterizedType pt,
            @NotNull DeserializerEngine engine) {
        Objects.requireNonNull(value, "value cannot be null!");
        Objects.requireNonNull(pt, "pt cannot be null!");
        Objects.requireNonNull(engine, "engine cannot be null!");

        return OperationResult.of(value);
    }
}
