package io.github.zap.commons.keyvaluegetters.deserializers;

import io.github.zap.commons.keyvaluegetters.DeserializerEngine;
import io.github.zap.commons.keyvaluegetters.OperationResult;
import io.github.zap.commons.keyvaluegetters.ValueDeserializer;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.ParameterizedType;
import java.util.Objects;

/**
 * Deserialize {@link String} to {@link Float}
 */
public class FloatDeserializer implements ValueDeserializer<Float> {
    @Override
    public @NotNull OperationResult<Float> deserialize(
            @NotNull String value,
            @NotNull ParameterizedType pt,
            @NotNull DeserializerEngine engine) {
        Objects.requireNonNull(value, "value cannot be null!");
        Objects.requireNonNull(pt, "pt cannot be null!");
        Objects.requireNonNull(engine, "engine cannot be null!");

        try {
            return OperationResult.of(Float.parseFloat(value));
        } catch (NumberFormatException e) {
            return OperationResult.error(e);
        }
    }
}
