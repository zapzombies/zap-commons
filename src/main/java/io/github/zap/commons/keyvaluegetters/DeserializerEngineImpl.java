package io.github.zap.commons.keyvaluegetters;

import io.github.zap.commons.utils.ReflectionUtils;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Default implement of a {@link DeserializerEngine}, using a hashmap to store all managed {@link ValueDeserializer}s
 */
class DeserializerEngineImpl implements DeserializerEngine {
    private final Map<Class<?>, ValueDeserializer<?>> deserializers = new HashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public <T> OperationResult<T> deserialize(ParameterizedType pt, String s) {
        try {
            var deserializer = getDeserializerFor((Class<?>) pt.getRawType());
            if(deserializer != null) {
                return (OperationResult<T>)deserializer.deserialize(s, pt, this);
            } else {
                return OperationResult.error("Cannot find a compatible deserializer matching type: " + pt.getRawType());
            }
        } catch (Throwable throwable) {
            return  OperationResult.error(throwable);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> ValueDeserializer<T> getDeserializerFor(Class<? extends T> clazz) {
        var targetClazz = ReflectionUtils.nearestSubclass(clazz, deserializers.keySet());
        return (ValueDeserializer<T>) deserializers.get(targetClazz);
    }

    @Override
    public <T> void addDeserializer(Class<T> clazz, ValueDeserializer<? extends T> deserializer) {
        deserializers.put(clazz, deserializer);
    }

    @Override
    public <T> void removeDeserializer(Class<T> clazz) {
        deserializers.remove(clazz);
    }

    @Override
    public Set<Map.Entry<Class<?>, ValueDeserializer<?>>> getAllDeserializers() {
        return deserializers.entrySet();
    }

    @Override
    public void clearAllDeserializers() {
        deserializers.clear();
    }
}