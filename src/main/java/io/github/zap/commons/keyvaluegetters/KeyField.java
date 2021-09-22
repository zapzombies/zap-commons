package io.github.zap.commons.keyvaluegetters;

import java.lang.reflect.ParameterizedType;

/**
 * Information about a field annotated with {@link KeyDeclaration}
 */
public record KeyField (
        String fieldName,
        String keyName,
        ParameterizedType parameterizedType,
        boolean required,
        String description){}
