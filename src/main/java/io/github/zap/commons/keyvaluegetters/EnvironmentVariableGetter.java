package io.github.zap.commons.keyvaluegetters;

import io.github.zap.commons.keyvaluegetters.keytransformers.EnvironmentVariableKeyTransformer;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A {@link KeyValueGetter} that using Environment Variables as its source
 */
public class EnvironmentVariableGetter extends AbstractKeyValueGetter {
    protected EnvironmentVariableGetter(Builder builder) {
        super(builder);
    }

    @Override
    protected @NotNull OperationResult<String> getFieldValue(@NotNull KeyField kf) {
        Objects.requireNonNull(kf, "kf cannot be null!");
        String value = System.getenv(kf.keyName());
        return value != null ?
                OperationResult.of(value) :
                OperationResult.error("Cannot find environment variable: " + kf.keyName());
    }

    /**
     * Helper class to instantiate a {@link EnvironmentVariableGetter}
     */
    public static class Builder extends KeyValueGetter.Builder {
        public Builder() {
            // Defaults
            withKeyTransformation(new EnvironmentVariableKeyTransformer());
        }

        @Override
        @NotNull
        public EnvironmentVariableGetter build() {
            return new EnvironmentVariableGetter(this);
        }
    }
}
