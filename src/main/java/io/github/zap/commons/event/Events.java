package io.github.zap.commons.event;

import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Contains utility methods for working with events.
 */
public final class Events {
    private Events() {}

    private static final class BukkitProxy<T extends org.bukkit.event.Event> extends Event<T> {
        private final Plugin plugin;
        private final EventPriority priority;
        private final boolean ignoreCancelled;
        private final Class<T> bukkitEventClass;
        private final Event<T> wrappedEvent;
        private final Listener listener;

        private HandlerList handlerList;
        private RegisteredListener registeredListener;
        private boolean isRegistered = false;

        private BukkitProxy(@NotNull Plugin plugin, EventPriority priority, boolean ignoreCancelled,
                            @NotNull Class<T> bukkitEventClass, @NotNull Event<T> wrappedEvent) {
            super(wrappedEvent.logger);
            this.plugin = plugin;
            this.priority = priority;
            this.ignoreCancelled = ignoreCancelled;
            this.bukkitEventClass = bukkitEventClass;
            this.wrappedEvent = wrappedEvent;
            listener = new Listener() {};
        }

        private void tryRegister() {
            if(!isRegistered && handlerCount() > 0) {
                tryReflectHandlerList();

                //noinspection unchecked
                EventExecutor executor = (listener, event) -> invoke((T)event);

                if(handlerList != null) {
                    registeredListener = new RegisteredListener(listener, executor, priority, plugin, ignoreCancelled);
                }
                else {
                    plugin.getServer().getPluginManager().registerEvent(bukkitEventClass, listener, priority, executor,
                            plugin, ignoreCancelled);
                }

                isRegistered = true;
            }
        }

        private void tryDeregister() {
            if(isRegistered && handlerCount() == 0) {
                if(handlerList != null) {
                    handlerList.unregister(registeredListener);
                }
                else {
                    HandlerList.unregisterAll(listener);
                }

                isRegistered = false;
            }
        }

        private void tryReflectHandlerList() {
            if(handlerList == null) {
                try {
                    handlerList = (HandlerList)bukkitEventClass.getMethod("getHandlerList").invoke(null);
                }
                catch(NoSuchMethodException | IllegalAccessException | InvocationTargetException |
                        NullPointerException exception) {
                    wrappedEvent.logger.log(Level.WARNING, "failed to reflect handler list", exception);
                }
            }
        }

        @Override
        public void invoke(T args) {
            wrappedEvent.invoke(args);
            tryRegister();
            tryDeregister();
        }

        @Override
        public void addHandler(@NotNull EventHandler<T> handler) {
            wrappedEvent.addHandler(handler);
            tryRegister();
        }

        @Override
        public void removeHandler(@NotNull EventHandler<T> handler) {
            wrappedEvent.removeHandler(handler);
            tryDeregister();
        }

        @Override
        public void clearHandlers() {
            wrappedEvent.clearHandlers();
            tryDeregister();
        }

        @Override
        public int handlerCount() {
            return wrappedEvent.handlerCount();
        }

        @Override
        public @NotNull Logger getLogger() {
            return wrappedEvent.getLogger();
        }
    }

    public static <T extends org.bukkit.event.Event> @NotNull Event<T> bukkitProxy(@NotNull Plugin plugin,
                                                                                  @NotNull EventPriority priority,
                                                                                  boolean ignoreCancelled,
                                                                                  @NotNull Class<T> bukkitEventClass,
                                                                                  @NotNull Event<T> wrapped) {
        return new BukkitProxy<>(plugin, priority, ignoreCancelled, bukkitEventClass, wrapped);
    }

    public static <T extends org.bukkit.event.Event> @NotNull Event<T> bukkitProxy(@NotNull Plugin plugin,
                                                                                  @NotNull Class<T> bukkitEventClass,
                                                                                  @NotNull Event<T> wrapped) {
        return new BukkitProxy<>(plugin, EventPriority.NORMAL, false, bukkitEventClass, wrapped);
    }

    public static <T extends org.bukkit.event.Event> @NotNull Event<T> bukkitProxy(@NotNull Plugin plugin,
                                                                                  @NotNull Class<T> bukkitEventClass) {
        return new BukkitProxy<>(plugin, EventPriority.NORMAL, false, bukkitEventClass,
                new Event<>(plugin.getLogger()));
    }
}
