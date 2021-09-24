package io.github.zap.commons.keyvaluegetters.deserializers;

import io.github.zap.commons.keyvaluegetters.DeserializerEngine;
import io.github.zap.commons.keyvaluegetters.OperationResult;
import io.github.zap.commons.keyvaluegetters.ValueDeserializer;
import io.github.zap.commons.utils.ReflectionUtils;
import io.github.zap.commons.utils.NonParameterizedType;
import io.github.zap.commons.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Deserialize comma seperated string to an {@link ArrayList}
 */
public class CommaSeperatedArrayListDeserializer implements ValueDeserializer<ArrayList<?>> {
    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public @NotNull OperationResult<ArrayList<?>> deserialize(
            @NotNull String value,
            @NotNull ParameterizedType pt,
            @NotNull DeserializerEngine engine) {
        Objects.requireNonNull(value, "value cannot be null!");
        Objects.requireNonNull(pt, "pt cannot be null!");
        Objects.requireNonNull(engine, "engine cannot be null!");

        Optional<? extends Type> typeParam = ReflectionUtils.getSuperclassTypeParams(pt, Iterable.class).stream()
                .flatMap(Collection::stream)
                .findFirst();

        if(typeParam.isPresent()) {
            ArrayList items = new ArrayList();
            for(String item : StringUtils.fromCommaSeperated(value)) {
                OperationResult<Object> result = typeParam.get() instanceof ParameterizedType childPt ?
                        engine.deserialize(childPt, item) :
                        engine.deserialize(NonParameterizedType.fromClass((Class<?>) typeParam.get()), item);
                if(result.isSuccess())
                    items.add(result.result().get());
                else
                    return OperationResult.error("Error occurred while deserializing collection", result.throwable());
            }

            return OperationResult.of(items);
        } else {
            return OperationResult.error("Cannot retrieve type params");
        }
    }
}
