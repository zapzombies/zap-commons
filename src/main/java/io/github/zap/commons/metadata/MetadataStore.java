package io.github.zap.commons.metadata;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public interface MetadataStore {
    @NotNull Optional<MetadataValue> getValue(@NotNull UUID id, @NotNull String key);

    void add(@NotNull UUID id, @NotNull String key, @Nullable Object object);

    void remove(@NotNull UUID id, @NotNull String key);

    void removeAll(@NotNull UUID id);

    static @NotNull MetadataStore basic() {
        return new BasicMetadataStore();
    }
}