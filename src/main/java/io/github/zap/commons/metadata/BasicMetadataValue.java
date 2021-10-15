package io.github.zap.commons.metadata;

import org.jetbrains.annotations.Nullable;

class BasicMetadataValue implements MetadataValue {
    private final Object object;

    BasicMetadataValue(@Nullable Object object) {
        this.object = object;
    }

    @Override
    public <T> T getValue() {
        //noinspection unchecked
        return (T)object;
    }
}
