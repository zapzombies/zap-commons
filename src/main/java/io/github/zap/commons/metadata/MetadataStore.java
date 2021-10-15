package io.github.zap.commons.metadata;

import io.github.zap.commons.utils.MathUtils;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public interface MetadataStore {
    @NotNull Optional<MetadataValue> getValue(@NotNull UUID id, @NotNull String key);

    void add(@NotNull UUID id, @NotNull String key, @Nullable Object object);

    default void add(@NotNull Entity entity, @NotNull String key, @Nullable Object object) {
        add(entity.getUniqueId(), key, object);
    }

    default void add(@NotNull World world, @NotNull String key, @Nullable Object object) {
        add(world.getUID(), key, object);
    }

    default void add(@NotNull Block block, @NotNull String key, @Nullable Object object) {
        UUID id = new UUID(MathUtils.longFromInts(block.getX(), block.getZ()), block.getY());
        add(id, key, object);
    }

    void remove(@NotNull UUID id, @NotNull String key);

    default void remove(@NotNull Entity entity, @NotNull String key, @Nullable Object object) {
        remove(entity.getUniqueId(), key);
    }

    default void remove(@NotNull World world, @NotNull String key, @Nullable Object object) {
        remove(world.getUID(), key);
    }

    default void remove(@NotNull Block block, @NotNull String key, @Nullable Object object) {
        UUID id = new UUID(MathUtils.longFromInts(block.getX(), block.getZ()), block.getY());
        remove(id, key);
    }

    void removeAll(@NotNull UUID id);

    default void removeAll(@NotNull Entity entity, @NotNull String key, @Nullable Object object) {
        removeAll(entity.getUniqueId());
    }

    default void removeAll(@NotNull World world, @NotNull String key, @Nullable Object object) {
        removeAll(world.getUID());
    }

    default void removeAll(@NotNull Block block, @NotNull String key, @Nullable Object object) {
        UUID id = new UUID(MathUtils.longFromInts(block.getX(), block.getZ()), block.getY());
        removeAll(id);
    }
}