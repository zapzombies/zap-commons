package io.github.zap.commons.event;

import org.apache.commons.lang.Validate;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * <p>Broadcasts events to all registered listeners.</p>
 *
 * <p>The order in which handlers are executed is not specified and is left up to implementations. Thread-safety, or
 * lack thereof, is also implementation-dependent. Handlers are not guaranteed to be invoked on the same thread as the
 * one calling {@link Event#invoke(Object, Object)} (although implementations may offer this guarantee).</p>
 *
 * <p>Handlers may be added and removed at any time (even while other handlers are being called). Recursive invocation
 * is typically disallowed.</p>
 *
 * <p>Events should, but are not required to use reference equality to compare {@link EventHandler} instances.</p>
 *
 * <p>This interface also holds a number of potentially useful utility methods.</p>
 * @param <T> The type of parameter event listeners must accept
 */
public interface Event<T> {
    /**
     * Calls the event handlers for this event. The order in which handlers are invoked is not deterministic for some
     * implementations.
     * @param sender The object responsible for calling this method
     * @param args The arguments to call each handler with
     */
    void invoke(Object sender, T args);

    /**
     * Adds an event handler to this event. If this function is called inside the body of a handler while it is being
     * invoked by this event, the newly-registered handler will typically only be called during the next call to
     * {@link Event#invoke(Object, Object)}.
     *
     * Some Event implementations may support duplicate handlers, in which case the same handler will be called twice.
     * Others may not support duplicates, in which case a runtime exception will be thrown.
     * @param handler The handler to register
     */
    void addHandler(@NotNull EventHandler<T> handler);

    /**
     * Removes a handler from this event, if it is present. If this function is called inside the body of a handler
     * while it is being invoked by this event, the newly-removed handler will still be called (if it wasn't already).
     * The next call to {@link Event#invoke(Object, Object)} will not include the removed handler. If there are multiple
     * handlers that are equal to the provided handler, only one will be removed.
     * @param handler The handler to remove
     */
    void removeHandler(@NotNull EventHandler<T> handler);

    /**
     * Determines if this event contains the specified handler.
     * @param handler The handler to search for
     * @return true if the handler exists; false otherwise
     */
    boolean hasHandler(@NotNull EventHandler<T> handler);

    /**
     * Removes all handlers from this event.
     */
    void clearHandlers();

    /**
     * @return The number of handlers that are currently registered.
     */
    int handlerCount();

    /**
     * <p>Creates an Event implementation that proxies a Bukkit event (see {@link org.bukkit.event.Event}). Such
     * "proxy events" will have their handlers invoked whenever the proxied Bukkit event is fired.</p>
     *
     * <p>Registration with the Bukkit API should occur lazily, only when handlers actually exist, so it is safe to
     * create proxy events even if it is not guaranteed that they will be used.</p>
     *
     * <p>The returned event offers no thread safety by default, as the majority of Bukkit events are synchronous. For
     * the few that aren't, use {@link Event#synchronize()}.</p>
     * @param plugin The plugin to register the proxied Bukkit event under
     * @param bukkitEventClass The class of the Bukkit event
     * @param priority The EventPriority
     * @param ignoreCancelled Whether this event ignores cancelled events
     * @param <T> The type of Bukkit event
     * @return An implementation of Event that is designed to proxy Bukkit's API
     * @throws NullPointerException if any of the arguments are null
     */
    static <T extends org.bukkit.event.Event> Event<T> bukkitProxy(@NotNull Plugin plugin,
                                                                   @NotNull Class<T> bukkitEventClass,
                                                                   @NotNull EventPriority priority,
                                                                   boolean ignoreCancelled) {
        return new BukkitProxy<>(
                Objects.requireNonNull(plugin, "plugin cannot be null"),
                Objects.requireNonNull(bukkitEventClass, "bukkitEventClass cannot be null"),
                Objects.requireNonNull(priority, "priority cannot be null"), ignoreCancelled);
    }

    /**
     * See {@link Event#bukkitProxy(Plugin, Class, EventPriority, boolean)}. Event priority defaults to normal.
     * @param plugin The plugin to register the proxied Bukkit event under
     * @param bukkitEventClass The class of the Bukkit event
     * @param ignoreCancelled Whether this event ignores cancelled events
     * @param <T> The type of Bukkit event
     * @return An implementation of Event that is designed to proxy Bukkit's API
     * @throws NullPointerException if any of the arguments are null
     */
    static <T extends org.bukkit.event.Event> Event<T> bukkitProxy(@NotNull Plugin plugin,
                                                                   @NotNull Class<T> bukkitEventClass,
                                                                   boolean ignoreCancelled) {
        return bukkitProxy(plugin, bukkitEventClass, EventPriority.NORMAL, ignoreCancelled);
    }

    /**
     * See {@link Event#bukkitProxy(Plugin, Class, EventPriority, boolean)}. Event priority defaults to normal,
     * cancelled events ignored.
     * @param plugin The plugin to register the proxied Bukkit event under
     * @param bukkitEventClass The class of the Bukkit event
     * @param <T> The type of Bukkit event
     * @return An implementation of Event that is designed to proxy Bukkit's API
     * @throws NullPointerException if any of the arguments are null
     */
    static <T extends org.bukkit.event.Event> Event<T> bukkitProxy(@NotNull Plugin plugin,
                                                                   @NotNull Class<T> bukkitEventClass) {
        return bukkitProxy(plugin, bukkitEventClass, EventPriority.NORMAL, false);
    }

    /**
     * Creates a synchronized wrapper around this event. Calls to all methods defined in the {@link Event} interface
     * are synchronized on the same object. Otherwise, the returned event will exhibit the same implementation
     * characteristics as this one.
     * @return A thread-safe Event instance
     */
    default @NotNull Event<T> synchronize() {
        return new Event<>() {
            private final Object lock = new Object();

            @Override
            public void invoke(Object sender, T args) {
                synchronized(lock) {
                    Event.this.invoke(sender, args);
                }
            }

            @Override
            public void addHandler(@NotNull EventHandler<T> handler) {
                synchronized (lock) {
                    Event.this.addHandler(handler);
                }
            }

            @Override
            public void removeHandler(@NotNull EventHandler<T> handler) {
                synchronized (lock) {
                    Event.this.removeHandler(handler);
                }
            }

            @Override
            public boolean hasHandler(@NotNull EventHandler<T> handler) {
                synchronized (lock) {
                    return Event.this.hasHandler(handler);
                }
            }

            @Override
            public void clearHandlers() {
                synchronized (lock) {
                    Event.this.clearHandlers();
                }
            }

            @Override
            public int handlerCount() {
                synchronized (lock) {
                    return Event.this.handlerCount();
                }
            }
        };
    }

    /**
     * <p>Creates a new event from this one. Calls to {@link Event#invoke(Object, Object)} on the new event will invoke
     * this event's handlers if and only if the argument satisfies the predicate.</p>
     *
     * <p>If this event is synchronized, any calls to test the predicate will not be synchronized. Users can call
     * {@link Event#synchronize()} afterwards if it is necessary to synchronize the predicate.</p>
     * @param predicate The predicate to test arguments against
     * @return The new event
     */
    default @NotNull Event<T> filter(@NotNull Predicate<T> predicate) {
        Objects.requireNonNull(predicate, "predicate cannot be null");

        return new WrappedEvent<>(this) {
            @Override
            public void invoke(Object sender, T args) {
                if(predicate.test(args)) {
                    super.invoke(sender, args);
                }
            }
        };
    }

    /**
     * <p>Creates a new event from this event, whose invoke method will call another event's handlers directly after
     * this event's.</p>
     *
     * <p>It is important to avoid cyclically linking events, as typical implementations do not support recursive
     * invocation.</p>
     *
     * <p>If this event is synchronized, calls to the linked event's invoke method will not be synchronized unless the
     * linked method is itself synchronized; in which case the synchronization will occur on a different object. If it
     * is necessary to synchronize both calls, call {@link Event#synchronize()} on the returned object.</p>
     * @param other The event to link to
     * @return A wrapper for this event, which is linked to the provided event
     * @throws IllegalArgumentException if other is null or the same event as this
     */
    default @NotNull Event<T> linkTo(@NotNull Event<T> other) {
        Validate.isTrue(other != this, "cannot chain to the same object");
        Objects.requireNonNull(other, "other cannot be null");

        return new WrappedEvent<>(this) {
            @Override
            public void invoke(Object sender, T args) {
                super.invoke(sender, args);
                other.invoke(sender, args);
            }
        };
    }

    /**
     * Creates a new event from this event, whose invoke method will call another event's handlers directly after
     * this event's, and after applying a mapping function to the argument. Otherwise, this behaves identically to
     * {@link Event#linkTo(Event)}.
     * @param other The event to map calls to
     * @param mapper The mapping function
     * @param <V> The type the other event receives
     * @return A wrapper for this event, which is linked to the provided event of a different type
     * @throws IllegalArgumentException if other or mapper are null, or other is the same object as this
     */
    default @NotNull <V> Event<T> mapTo(@NotNull Event<V> other, @NotNull Function<T, V> mapper) {
        Validate.isTrue(other != this, "cannot map to the same object");
        Objects.requireNonNull(other, "other cannot be null");
        Objects.requireNonNull(mapper, "mapper cannot be null");

        return new WrappedEvent<>(this) {
            @Override
            public void invoke(Object sender, T args) {
                super.invoke(sender, args);
                other.invoke(sender, mapper.apply(args));
            }
        };
    }
}
