package io.github.zap.commons.keyvaluegetters.deserializers;

import io.github.zap.commons.keyvaluegetters.DeserializerEngine;
import io.github.zap.commons.keyvaluegetters.OperationResult;
import io.github.zap.commons.keyvaluegetters.ValueDeserializer;

import java.lang.reflect.ParameterizedType;

/**
 * Deserialize {@link String} to {@link Boolean}
 */
public class BooleanDeserializer implements ValueDeserializer<Boolean> {
    @Override
    public OperationResult<Boolean> deserialize(String value, ParameterizedType pt, DeserializerEngine deserializers) {
        if(value.isEmpty())
            return OperationResult.of(true);

        return OperationResult.of(Boolean.parseBoolean(value));
    }
}
