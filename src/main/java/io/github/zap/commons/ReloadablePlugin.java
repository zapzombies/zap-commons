package io.github.zap.commons;

import io.github.zap.commons.event.Event;
import io.github.zap.commons.event.EventHandler;
import io.github.zap.commons.event.SimpleEvent;
import org.jetbrains.annotations.NotNull;

/**
 * A reload-sensitive plugin. Provides events for loading, enabling, and disabling.
 * @param <T> The subclass type
 */
public abstract class ReloadablePlugin<T extends ReloadablePlugin<T>> extends ZapPlugin {
    private final Event<T> loadEvent = new SimpleEvent<>();
    private final Event<T> enableEvent = new SimpleEvent<>();
    private final Event<T> disableEvent = new SimpleEvent<>();

    @Override
    public final void doLoad() {
        loadEvent.invoke(this, getPlugin());
    }

    @Override
    public final void doEnable() {
        enableEvent.invoke(this, getPlugin());
    }

    @Override
    public final void doDisable() {
        disableEvent.invoke(this, getPlugin());
    }

    public void registerLoadHandler(@NotNull EventHandler<T> handler) {
        loadEvent.addHandler(handler);
    }

    public void registerEnableHandler(@NotNull EventHandler<T> handler) {
        enableEvent.addHandler(handler);
    }

    public void registerDisableHandler(@NotNull EventHandler<T> handler) {
        disableEvent.addHandler(handler);
    }

    protected abstract @NotNull T getPlugin();
}
