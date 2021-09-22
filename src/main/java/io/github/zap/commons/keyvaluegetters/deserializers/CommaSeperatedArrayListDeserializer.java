package io.github.zap.commons.keyvaluegetters.deserializers;

import io.github.zap.commons.keyvaluegetters.DeserializerEngine;
import io.github.zap.commons.keyvaluegetters.KeyField;
import io.github.zap.commons.keyvaluegetters.OperationResult;
import io.github.zap.commons.keyvaluegetters.ValueDeserializer;
import io.github.zap.commons.utils.ClassUtils;
import io.github.zap.commons.utils.NonParameterizedType;
import io.github.zap.commons.utils.StringUtils;

import java.lang.reflect.ParameterizedType;
import java.util.*;

/**
 * Deserialize comma seperated string to an {@link ArrayList}
 */
public class CommaSeperatedArrayListDeserializer implements ValueDeserializer<ArrayList<?>> {
    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public OperationResult<ArrayList<?>> deserialize(String value, ParameterizedType pt, DeserializerEngine engine) {
        var typeParam = ClassUtils.getGenericAncestor(pt, Iterable.class).stream()
                .flatMap(Collection::stream)
                .findFirst();

        if(typeParam.isPresent()) {
            ArrayList items = new ArrayList();
            for(var item : StringUtils.fromCommaSeperated(value)) {
                var result = engine.deserialize(NonParameterizedType.fromClass(typeParam.get()), item);
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
