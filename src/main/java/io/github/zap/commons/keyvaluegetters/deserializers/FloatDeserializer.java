package io.github.zap.commons.keyvaluegetters.deserializers;

import io.github.zap.commons.keyvaluegetters.DeserializerEngine;
import io.github.zap.commons.keyvaluegetters.OperationResult;
import io.github.zap.commons.keyvaluegetters.ValueDeserializer;

import java.lang.reflect.ParameterizedType;

/**
 * Deserialize {@link String} to {@link Float}
 */
public class FloatDeserializer implements ValueDeserializer<Float> {
    @Override
    public OperationResult<Float> deserialize(String value, ParameterizedType pt, DeserializerEngine engine) {
        try {
            return OperationResult.of(Float.parseFloat(value));
        } catch (NumberFormatException e) {
            return OperationResult.error(e);
        }
    }
}
