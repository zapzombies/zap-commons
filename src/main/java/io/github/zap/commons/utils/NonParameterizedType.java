package io.github.zap.commons.utils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * It is a hack to generalize rawType and genericType
 */
public class NonParameterizedType implements ParameterizedType {
    private final Type rawType;
    private final Type ownerType;

    public NonParameterizedType(Type rawType, Type ownerType) {
        this.rawType = rawType;
        this.ownerType = ownerType;
    }

    public static NonParameterizedType fromClass(Class<?> field) {
        return new NonParameterizedType(field, null);
    }

    public static ParameterizedType fromField(Field field) {
        var t = field.getGenericType();
        return t instanceof ParameterizedType pt ? pt : fromClass(field.getType());
    }


    @Override
    public Type[] getActualTypeArguments() {
        return new Type[0];
    }

    @Override
    public Type getRawType() {
        return rawType;
    }

    @Override
    public Type getOwnerType() {
        return ownerType;
    }

    @Override
    public String toString() {
        return getRawType().toString();
    }
}
