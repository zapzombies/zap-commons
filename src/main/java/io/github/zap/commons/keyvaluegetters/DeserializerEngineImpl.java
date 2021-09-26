package io.github.zap.commons.keyvaluegetters;

import io.github.zap.commons.utils.ReflectionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Default implement of a {@link DeserializerEngine}, using a hashmap to store all managed {@link ValueDeserializer}s
 */
class DeserializerEngineImpl implements DeserializerEngine {
    private final Map<Class<?>, ValueDeserializer<?>> deserializers = new HashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public <T> @Nullable OperationResult<T> deserialize(@NotNull ParameterizedType pt, @NotNull String s) {
        Objects.requireNonNull(pt, "pt cannot be null!");
        Objects.requireNonNull(s, "s cannot be null!");
        try {
            ValueDeserializer<?> deserializer = getDeserializerFor((Class<?>) pt.getRawType());
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
    public <T> @Nullable ValueDeserializer<T> getDeserializerFor(@NotNull Class<? extends T> clazz) {
        Objects.requireNonNull(clazz, "clazz cannot be null!");
        Class<?> targetClazz = ReflectionUtils.nearestSubclass(clazz, deserializers.keySet());
        return (ValueDeserializer<T>) deserializers.get(targetClazz);
    }

    @Override
    public <T> void addDeserializer(@NotNull Class<T> clazz, @Nullable ValueDeserializer<? extends T> deserializer) {
        Objects.requireNonNull(clazz, "clazz cannot be null!");
        deserializers.put(clazz, deserializer);
    }

    @Override
    public <T> void removeDeserializer(@NotNull Class<T> clazz) {
        Objects.requireNonNull(clazz, "clazz cannot be null!");
        deserializers.remove(clazz);
    }

    @Override
    public @NotNull Set<Map.Entry<Class<?>, ValueDeserializer<?>>> getAllDeserializers() {
        return deserializers.entrySet();
    }

    @Override
    public void clearAllDeserializers() {
        deserializers.clear();
    }
}