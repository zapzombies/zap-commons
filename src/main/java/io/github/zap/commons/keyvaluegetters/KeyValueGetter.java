package io.github.zap.commons.keyvaluegetters;

import io.github.zap.commons.keyvaluegetters.deserializers.*;
import io.github.zap.commons.keyvaluegetters.keytransformers.RetainKeyTransformer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Retrieving data from a datasource to a key-value structure
 */
public interface KeyValueGetter {
    /**
     * Retrieving data using the specified type as a template
     * @param type target type
     * @param <T> target type
     * @return a new instance of target type containing retrieved data
     * @throws GetFailedException errors during the operation
     */
    @NotNull
    <T> T get(@NotNull Class<T> type) throws GetFailedException;

    /**
     * Retrieving data using the specified object as a template, the result will be assigned into its fields
     * @param o target object
     * @throws GetFailedException errors during the operation
     */
    void bind(@NotNull Object o) throws GetFailedException;

    /**
     * Helper class to instantiate a {@link KeyValueGetter}
     */
    abstract class Builder {
        private KeyTransformer keyTransformer;
        private Logger logger;
        private DeserializerEngine deserializerEngine;

        public Builder() {
            // Defaults
            withDeserializerEngine(new DeserializerEngineImpl());
            withKeyTransformation(new RetainKeyTransformer());
            addDeserializer(String.class, new StringDeserializer());
            addDeserializer(Integer.class, new IntegerDeserializer());
            addDeserializer(Integer.TYPE, new IntegerDeserializer());
            addDeserializer(Float.class, new FloatDeserializer());
            addDeserializer(Float.TYPE, new FloatDeserializer());
            addDeserializer(Boolean.class, new BooleanDeserializer());
            addDeserializer(Boolean.TYPE, new BooleanDeserializer());
            addDeserializer(ArrayList.class, new CommaSeperatedArrayListDeserializer());
        }

        @NotNull
        public Builder withKeyTransformation(@NotNull KeyTransformer keyTransformer) {
            Objects.requireNonNull(keyTransformer, "keyTransformer cannot be null!");
            this.keyTransformer = keyTransformer;
            return this;
        }

        @NotNull
        public Builder withDeserializerEngine(@NotNull DeserializerEngine engine) {
            Objects.requireNonNull(engine, "engine cannot be null!");
            this.deserializerEngine = engine;
            return this;
        }

        @NotNull
        public Builder withLogger(@Nullable Logger logger) {
            this.logger = logger;
            return this;
        }

        @NotNull
        public <T> Builder addDeserializer(@NotNull Class<T> clazz, @NotNull ValueDeserializer<? extends T> deserializer) {
            getDeserializerEngine().addDeserializer(clazz, deserializer);
            return this;
        }

        @NotNull
        public KeyTransformer getKeyTransformer() {
            return keyTransformer;
        }

        /**
         * Build the {@link KeyValueGetter}
         * @return a configured {@link KeyValueGetter}
         */
        @NotNull
        public abstract KeyValueGetter build();

        @NotNull
        public Logger getLogger() {
            return logger;
        }

        @NotNull
        public DeserializerEngine getDeserializerEngine() {
            return deserializerEngine;
        }
    }
}
