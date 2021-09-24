package io.github.zap.commons.keyvaluegetters;

import io.github.zap.commons.utils.ReflectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Base class for some {@link KeyValueGetter}, contains the default process to locate, deserialize and bind to fields
 */
public abstract class AbstractKeyValueGetter implements KeyValueGetter {
    protected final Builder builder;
    private final Map<Class<?>, Set<KeyField>> cachedKeyFields = new HashMap<>();

    protected AbstractKeyValueGetter(Builder builder) {
        this.builder = builder;
    }

    @Override
    @NotNull
    public <T> T get(@NotNull Class<T> type) throws GetFailedException {
        Objects.requireNonNull(type, "type cannot be null!");
        try {
            T object = type.getConstructor().newInstance();
            bind(object);
            return object;
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
            throw new GetFailedException("Unexpected failure: ", e);
        }
    }

    @Override
    public void bind(@NotNull Object object) throws GetFailedException {
        Objects.requireNonNull(object, "object cannot be null!");

        ArrayList<ImmutablePair<KeyField, OperationResult<String>>> faultyGetOperation = new ArrayList<ImmutablePair<KeyField, OperationResult<String>>>();
        ArrayList<ImmutablePair<KeyField, OperationResult<?>>> faultyDeserializeOperation = new ArrayList<ImmutablePair<KeyField, OperationResult<?>>>();
        ArrayList<ImmutablePair<KeyField, OperationResult<?>>> faultyBindOperation = new ArrayList<ImmutablePair<KeyField, OperationResult<?>>>();
        for(KeyField item : getKeyFields(object.getClass())) {
            ImmutablePair<KeyField, OperationResult<String>> getResult = ImmutablePair.of(item, getFieldValue(item));
            if(getResult.getRight().isSuccess()) {
                String rawValue = getResult.getRight().result().get();
                KeyField kf = getResult.getLeft();
                OperationResult<Object> deserializeResult = builder.getDeserializerEngine().deserialize(kf.parameterizedType(), rawValue);
                if(deserializeResult.isSuccess()) {
                    try {
                        object.getClass().getDeclaredField(kf.fieldName()).set(object, deserializeResult.result().get());
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        faultyBindOperation.add(ImmutablePair.of(kf, OperationResult.error(e)));
                    }
                } else {
                    faultyDeserializeOperation.add(ImmutablePair.of(kf, deserializeResult));
                }

            } else {
                faultyGetOperation.add(getResult);
            }
        }

        if (!faultyGetOperation.isEmpty() || !faultyDeserializeOperation.isEmpty() || !faultyBindOperation.isEmpty()) {
            //
            boolean shouldThrow = faultyGetOperation.stream().anyMatch(f -> f.getLeft().required()) ||
                    faultyDeserializeOperation.stream().anyMatch(f -> f.getLeft().required()) ||
                    faultyBindOperation.stream().anyMatch(f -> f.getLeft().required());

            faultyGetOperation.sort((o1, o2) -> prioritizeRequiredSort(o1.getLeft(), o2.getLeft()));
            faultyDeserializeOperation.sort((o1, o2) -> prioritizeRequiredSort(o1.getLeft(), o2.getLeft()));
            faultyBindOperation.sort((o1, o2) -> prioritizeRequiredSort(o1.getLeft(), o2.getLeft()));

            StringBuilder sb = new StringBuilder();
            if(shouldThrow)
                sb.append("Unable to get all the required fields:\n");
            else
                sb.append("Parse successful with warnings:\n");

            if(!faultyGetOperation.isEmpty()) {
                sb.append("Failed to get these fields: \n");
                faultyGetOperation.forEach(f -> reportError(sb, f.getLeft(), f.getRight()));
            }

            if(!faultyDeserializeOperation.isEmpty()) {
                sb.append("Failed to deserialize these fields: \n");
                faultyDeserializeOperation.forEach(f -> reportError(sb, f.getLeft(), f.getRight()));
            }

            if(!faultyBindOperation.isEmpty()) {
                sb.append("Failed to bind these fields: \n");
                faultyBindOperation.forEach(f -> reportError(sb, f.getLeft(), f.getRight()));
            }

            if(shouldThrow) {
                throw new GetFailedException(sb.toString(), faultyGetOperation, faultyDeserializeOperation, faultyBindOperation);
            } else if(builder.getLogger() != null){
                builder.getLogger().warning(sb.toString());
            }
        }
    }

    private void reportError(StringBuilder sb, KeyField kf, OperationResult<?> result) {
        if(!kf.required())
            sb.append("(Optional) ");

        sb.append(String.format("Name: %s, field: %s\n", kf.keyName(), kf.fieldName()));
        sb.append(String.format("Description: %s\n", kf.description()));
        sb.append(String.format("Class: %s\n", kf.parameterizedType()));
        if(result.message() != null)
            sb.append(String.format("Error: %s\n", result.message()));
        if(result.throwable() != null)
            sb.append(String.format("Exception: %s\n", result.throwable()));
        sb.append("\n");
    }

    private static int prioritizeRequiredSort(KeyField o1, KeyField o2) {
        return o1.required() == o2.required() ? 0 : o1.required() ? -1 : 1;
    }

    /**
     * Reflectively retrieve key declared field information of a class
     * @param clazz target class
     * @return a set contains all key declared of the class
     */
    @NotNull
    protected Set<KeyField> getKeyFields(@NotNull Class<?> clazz) {
        if (cachedKeyFields.containsKey(clazz)) {
            return cachedKeyFields.get(clazz);
        } else {
            Set<KeyField> result = new HashSet<>();
            for(Map.Entry<Field, KeyDeclaration> item : ReflectionUtils.getAnnotatedFields(clazz, KeyDeclaration.class).entrySet()) {
                String keyName = StringUtils.isNotEmpty(item.getValue().name()) ?
                        item.getValue().name() :
                        builder.getKeyTransformer().transform(item.getKey().getName());

                result.add(new KeyField(item.getKey(), keyName, item.getValue()));
            }

            cachedKeyFields.put(clazz, result);
            return result;
        }
    }

    /**
     * Get field value, up to the implementor to decide the source
     * @param kf key to retrieve value
     * @return result of this operation
     */
    @NotNull
    protected abstract OperationResult<String> getFieldValue(@NotNull KeyField kf);
}
