package io.github.zap.commons.keyvaluegetters;

import io.github.zap.commons.utils.NonParameterizedType;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

/**
 * Information about a field annotated with {@link KeyDeclaration}
 */
public record KeyField (
        String fieldName,
        String keyName,
        ParameterizedType parameterizedType,
        boolean required,
        String description){
    public KeyField(Field field, String keyName, KeyDeclaration annotation) {
        this(field.getName(), keyName, NonParameterizedType.fromField(field), annotation.required(), annotation.description());
    }

}
