package io.github.zap.commons.keyvaluegetters;

import java.lang.reflect.ParameterizedType;

public interface ValueDeserializer<T> {
    OperationResult<T> deserialize(String value, ParameterizedType pt, DeserializerEngine deserializers);
}
