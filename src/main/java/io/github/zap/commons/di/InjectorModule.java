package io.github.zap.commons.di;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A Injector factory that define necessary components and services for a plugin.
 * Although this class can be used directly, it is recommended to create a subclass of this class
 * and configure your plugin dependency
 * @param <T>
 */
public class InjectorModule<T extends JavaPlugin> extends AbstractModule {
    protected final T plugin;

    public InjectorModule(@NotNull T plugin) {
        Objects.requireNonNull(plugin, "plugin cannot be null!");
        this.plugin = plugin;
    }

    /**
     * Create an injector with the declared dependencies
     * @return a configured injector
     */
    @NotNull
    public Injector createInjector() {
        return Guice.createInjector(this);
    }


    /**
     * This method is intended to by used by the DI framework
     * @return plugin declared in this instance
     */
    @Provides
    @NotNull
    public T providesPlugin() {
        return plugin;
    }
}
