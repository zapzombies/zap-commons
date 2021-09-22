package io.github.zap.commons.event;

import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.logging.Level;

class BukkitProxy<T extends org.bukkit.event.Event> extends SimpleEvent<T> {
    private final Listener listener = new Listener() {};

    private final Plugin plugin;
    private final Class<T> bukkitEventClass;
    private final EventPriority priority;
    private final boolean ignoreCancelled;

    private boolean eventRegistered = false;
    private boolean reflectionFailed = false;
    private HandlerList handlerList;
    private RegisteredListener registeredListener;

    BukkitProxy(@NotNull Plugin plugin, @NotNull Class<T> bukkitEventClass, @NotNull EventPriority priority,
                boolean ignoreCancelled) {
        this.plugin = Objects.requireNonNull(plugin, "plugin cannot be null");
        this.bukkitEventClass = Objects.requireNonNull(bukkitEventClass, "bukkitEventClass cannot be null");
        this.priority = Objects.requireNonNull(priority, "priority cannot be null");
        this.ignoreCancelled = ignoreCancelled;
    }

    private HandlerList reflectHandlerList() {
        if(handlerList == null && !reflectionFailed) {
            try {
                return (HandlerList)bukkitEventClass.getMethod("getHandlerList").invoke(null);
            }
            catch(NoSuchMethodException | IllegalAccessException | InvocationTargetException |
                    NullPointerException exception) {
                plugin.getLogger().log(Level.WARNING, "Failed to retrieve handler list", exception);
                reflectionFailed = true;
            }
        }

        return null;
    }

    private void register() {
        if(super.handlerCount() > 0 && !eventRegistered) {
            handlerList = reflectHandlerList();

            //noinspection unchecked
            EventExecutor executor = (ignored, event) -> invoke(this, (T)event);

            if(handlerList != null) {
                registeredListener = new RegisteredListener(listener, executor, priority, plugin, ignoreCancelled);
                handlerList.register(registeredListener);
            }
            else {
                plugin.getServer().getPluginManager().registerEvent(bukkitEventClass, listener, priority,
                        executor, plugin, ignoreCancelled);
            }

            eventRegistered = true;
        }
    }

    private void unregister() {
        if(super.handlerCount() == 0 && eventRegistered) {
            if(handlerList != null) {
                handlerList.unregister(registeredListener);
            }
            else {
                HandlerList.unregisterAll(listener);
            }

            eventRegistered = false;
        }
    }

    @Override
    public void invoke(Object sender, T args) {
        super.invoke(sender, args);
        register();
        unregister();
    }

    @Override
    public void addHandler(@NotNull EventHandler<T> handler) {
        super.addHandler(handler);
        register();
    }

    @Override
    public void removeHandler(@NotNull EventHandler<T> handler) {
        super.removeHandler(handler);
        unregister();
    }

    @Override
    public boolean hasHandler(@NotNull EventHandler<T> handler) {
        return super.hasHandler(handler);
    }

    @Override
    public void clearHandlers() {
        super.clearHandlers();
        unregister();
    }

    @Override
    public int handlerCount() {
        return super.handlerCount();
    }
}
