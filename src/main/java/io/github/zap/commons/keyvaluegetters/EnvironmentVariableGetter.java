package io.github.zap.commons.keyvaluegetters;

import io.github.zap.commons.keyvaluegetters.keytransformers.EnvironmentVariableKeyTransformer;

/**
 * A {@link KeyValueGetter} that using Environment Variables as its source
 */
public class EnvironmentVariableGetter extends AbstractKeyValueGetter {
    protected EnvironmentVariableGetter(Builder builder) {
        super(builder);
    }

    @Override
    protected OperationResult<String> getFieldValue(KeyField kf) {
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
        public EnvironmentVariableGetter build() {
            return new EnvironmentVariableGetter(this);
        }
    }
}
