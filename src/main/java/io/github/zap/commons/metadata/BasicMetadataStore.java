package io.github.zap.commons.metadata;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

class BasicMetadataStore implements MetadataStore {
    private final Map<UUID, Map<String, MetadataValue>> mappings = new IdentityHashMap<>();

    BasicMetadataStore() {}

    @Override
    public @NotNull Optional<MetadataValue> getValue(@NotNull UUID id, @NotNull String key) {
        Map<String, MetadataValue> idValues = mappings.get(id);

        if(idValues != null) {
            return Optional.ofNullable(idValues.get(key));
        }

        return Optional.empty();
    }

    @Override
    public void add(@NotNull UUID id, @NotNull String key, @Nullable Object object) {
        mappings.computeIfAbsent(id, (ignored) -> new HashMap<>()).put(key, new BasicMetadataValue(object));
    }

    @Override
    public void remove(@NotNull UUID id, @NotNull String key) {
        Map<String, MetadataValue> map = mappings.get(id);
        if(map != null) {
            map.remove(key);
        }
    }

    @Override
    public void removeAll(@NotNull UUID id) {
        mappings.remove(id);
    }
}
