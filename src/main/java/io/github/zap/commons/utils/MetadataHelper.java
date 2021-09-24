package io.github.zap.commons.utils;

import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.LazyMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Function;

/**
 * Utility class which provides several static methods for reading and writing metadata from/to Bukkit
 * {@link Metadatable} objects.
 */
public final class MetadataHelper {
    private MetadataHelper() {}

    /**
     * Obtains the {@link MetadataValue} object associated with the given plugin and name, on the given
     * {@link Metadatable} instance.
     * @param target The Metadatable object to search for metadata on
     * @param plugin The plugin to which the metadata must belong
     * @param metadataName The name of the metadata
     * @return An {@link Optional} which may contain a MetadataValue. If no metadata could be found under the specified
     * name, that also belongs to the given plugin, the Optional will be empty
     */
    public static @NotNull Optional<MetadataValue> getMetadataValue(@NotNull Metadatable target, @NotNull Plugin plugin,
                                                                    @NotNull String metadataName) {
        for(MetadataValue value : target.getMetadata(metadataName)) {
            if(value.getOwningPlugin() == plugin) {
                return Optional.of(value);
            }
        }

        return Optional.empty();
    }

    /**
     * Obtains the metadata instance (i.e. the object MetadataValue returns when its value() method is called) after
     * obtaining a MetadataValue instance as per {@link MetadataHelper#getMetadataValue(Metadatable, Plugin, String)}.
     * Assuming the MetadataValue is present, an unchecked cast will be performed on its underlying object. If the
     * MetadataValue object is <i>not</i> present, a {@link NoSuchElementException} will be thrown. This is necessary
     * to distinguish between null values for present metadata and metadata that is not present.
     * @param target The Metadatable object to search for metadata on
     * @param plugin The plugin to which the metadata must belong
     * @param metadataName The name of the metadata
     * @param <T> The instance type, to which the underlying value will be cast
     * @return The MetadataValue's value() object, after casting to T, which may be null if a null value was stored
     * @throws NoSuchElementException if no MetadataValue exists; see
     * {@link MetadataHelper#getMetadataValue(Metadatable, Plugin, String)}
     */
    public static <T> T getMetadataInstance(@NotNull Metadatable target, @NotNull Plugin plugin,
                                            @NotNull String metadataName) {
        Optional<MetadataValue> valueOptional = getMetadataValue(target, plugin, metadataName);

        if(valueOptional.isPresent()) {
            MetadataValue value = valueOptional.get();
            //noinspection unchecked
            return (T) value.value();
        }

        throw new NoSuchElementException("no metadata found named '" + metadataName + "' under plugin '" +
                plugin.getName() + "' for Metadatable '" + target + "'");
    }

    /**
     * Similarly to {@link java.util.Map#computeIfAbsent(Object, Function)}, computes a new value and registers it
     * under the specified name for the {@link Metadatable} target if a value is not already present. If a value
     * <i>is</i> present, no new value is computed and the original result is obtained.
     * @param target The Metadatable object to read or write metadata from
     * @param plugin The plugin to which the metadata belongs
     * @param metadataName The name of the metadata
     * @param valueFunc A {@link Function} that will be used to produce values if they are not already present. The
     *                  function receive a single string, which is the metadata name, and returns an object, which is
     *                  the metadata value
     * @return The old metadata value (if present) or the new metadata value (if not present)
     */
    public static @NotNull MetadataValue computeFixedValueIfAbsent(@NotNull Metadatable target, @NotNull Plugin plugin,
                                                                   @NotNull String metadataName,
                                                                   @NotNull Function<String, Object> valueFunc) {
        Optional<MetadataValue> currentValue = getMetadataValue(target, plugin, metadataName);
        if(currentValue.isPresent()) {
            return currentValue.get();
        }

        MetadataValue value = new FixedMetadataValue(plugin, valueFunc.apply(metadataName));
        target.setMetadata(metadataName, value);
        return value;
    }

    /**
     * Sets named, fixed metadata on a target for a plugin.
     * @param target The {@link Metadatable} object to set metadata for
     * @param owner The owning plugin
     * @param name The name of the metadata
     * @param value The metadata's value
     */
    public static void setFixedMetadata(@NotNull Metadatable target, @NotNull Plugin owner, @NotNull String name,
                                        @Nullable Object value) {
        target.setMetadata(name, new FixedMetadataValue(owner, value));
    }

    /**
     * Sets named, lazy metadata on a target for a plugin. This differs from
     * {@link MetadataHelper#setFixedMetadata(Metadatable, Plugin, String, Object)} in that the metadata's value will
     * only be computed when it is requested.
     * @param target The {@link Metadatable} object to set metadata for
     * @param owner The owning plugin
     * @param name The name of the metadata
     * @param lazyValue The metadata's value
     */
    public static void setLazyMetadata(@NotNull Metadatable target, @NotNull Plugin owner, @NotNull String name,
                                       @NotNull Callable<Object> lazyValue) {
        target.setMetadata(name, new LazyMetadataValue(owner, lazyValue));
    }

    /**
     * Sets named, lazy metadata on a target for a plugin, using the provided {@link LazyMetadataValue.CacheStrategy}
     * to determine how to cache the computed metadata value.
     * @param target The {@link Metadatable} object to set metadata for
     * @param owner The owning plugin
     * @param name The name of the metadata
     * @param lazyValue The metadata's value
     * @param cacheStrategy The cache strategy to use
     */
    public static void setLazyMetadata(@NotNull Metadatable target, @NotNull Plugin owner, @NotNull String name,
                                       @NotNull Callable<Object> lazyValue,
                                       @NotNull LazyMetadataValue.CacheStrategy cacheStrategy) {
        target.setMetadata(name, new LazyMetadataValue(owner, cacheStrategy, lazyValue));
    }
}
