package io.github.zap.commons.event;

import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;

class BukkitProxy<T extends org.bukkit.event.Event> extends SimpleEvent<T> {
    private static final Map<Class<?>, HandlerList> handlerLists = new IdentityHashMap<>();

    private final Listener listener = new Listener() {};

    private final Plugin plugin;
    private final Class<T> bukkitEventClass;
    private final EventPriority priority;
    private final boolean ignoreCancelled;

    private boolean reflectionFailed = false;

    private HandlerList handlerList = null;
    private final EventExecutor executor;
    private final RegisteredListener registeredListener;

    BukkitProxy(@NotNull Plugin plugin, @NotNull Class<T> bukkitEventClass, @NotNull EventPriority priority,
                boolean ignoreCancelled) {
        this.plugin = Objects.requireNonNull(plugin, "plugin cannot be null");
        this.bukkitEventClass = Objects.requireNonNull(bukkitEventClass, "bukkitEventClass cannot be null");
        this.priority = Objects.requireNonNull(priority, "priority cannot be null");
        this.ignoreCancelled = ignoreCancelled;

        //noinspection unchecked
        executor = (ignored, event) -> this.handle(this, (T)event);
        registeredListener = new RegisteredListener(listener, executor, priority, plugin, ignoreCancelled);
    }

    private HandlerList getHandlerList() {
        if(handlerList == null && !reflectionFailed) {
            handlerList = handlerLists.computeIfAbsent(bukkitEventClass, (key) -> {
                try {
                    return (HandlerList)bukkitEventClass.getMethod("getHandlerList").invoke(null);
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
                    plugin.getLogger().log(Level.WARNING, "failed to reflect getHandlerList", exception);
                    BukkitProxy.this.reflectionFailed = true;
                }

                return null;
            });
        }

        return handlerList;
    }

    private void register() {
        HandlerList handlerList = getHandlerList();

        if(handlerList != null) {
            handlerList.register(registeredListener);
        }
        else {
            plugin.getServer().getPluginManager().registerEvent(bukkitEventClass, listener, priority, executor, plugin,
                    ignoreCancelled);
        }
    }

    private void unregister() {
        HandlerList handlerList = getHandlerList();

        if(handlerList != null) {
            handlerList.unregister(registeredListener);
        }
        else {
            HandlerList.unregisterAll(listener);
        }
    }

    @Override
    protected void onHandlerCountChange(int oldSize, int newSize) {
        super.onHandlerCountChange(oldSize, newSize);

        if(oldSize > 0 && newSize == 0) {
            unregister();
        }
        else if(oldSize == 0 && newSize > 0) {
            register();
        }
    }
}
