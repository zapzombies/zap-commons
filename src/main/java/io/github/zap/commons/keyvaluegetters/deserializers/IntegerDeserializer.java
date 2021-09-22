package io.github.zap.commons.keyvaluegetters.deserializers;

import io.github.zap.commons.keyvaluegetters.DeserializerEngine;
import io.github.zap.commons.keyvaluegetters.OperationResult;
import io.github.zap.commons.keyvaluegetters.ValueDeserializer;

import java.lang.reflect.ParameterizedType;

/**
 * Deserialize {@link String} to {@link Integer}
 */
public class IntegerDeserializer implements ValueDeserializer<Integer> {
    @Override
    public OperationResult<Integer> deserialize(String value, ParameterizedType pt, DeserializerEngine engine) {
        try {
            return OperationResult.of(Integer.parseInt(value));
        } catch (NumberFormatException e) {
            return OperationResult.error(e);
        }
    }
}
