package io.github.zap.commons.keyvaluegetters.deserializers;

import io.github.zap.commons.keyvaluegetters.DeserializerEngine;
import io.github.zap.commons.keyvaluegetters.OperationResult;
import io.github.zap.commons.keyvaluegetters.ValueDeserializer;

import java.lang.reflect.ParameterizedType;

/**
 * Deserialize {@link String} to itself :). Only created to conform with library spec
 */
public class StringDeserializer implements ValueDeserializer<String> {
    @Override
    public OperationResult<String> deserialize(String value, ParameterizedType pt, DeserializerEngine engine) {
        return OperationResult.of(value);
    }
}
