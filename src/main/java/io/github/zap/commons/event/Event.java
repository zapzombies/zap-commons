package io.github.zap.commons.event;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Broadcasts events to registered listeners. This class is additionally responsible for logging all uncaught
 * exceptions thrown by listeners.
 * @param <T> The type of parameter event listeners must accept
 */
public class Event<T> {
    protected final Logger logger;

    private final List<EventHandler<T>> handlers = new ArrayList<>();

    private final List<EventHandler<T>> pendingAdditions = new ArrayList<>();
    private final List<EventHandler<T>> pendingRemovals = new ArrayList<>();

    private boolean invoking = false;
    private boolean clearFlag = false;

    public Event(@NotNull Logger logger) {
        this.logger = Objects.requireNonNull(logger, "logger cannot be null");
    }

    private void applyHandlerListModifications() {
        if(clearFlag) {
            handlers.clear();
            clearFlag = false;
        }
        else {
            if(!pendingAdditions.isEmpty()) {
                handlers.addAll(pendingAdditions);
                pendingAdditions.clear();
            }

            if(!pendingRemovals.isEmpty()) {
                handlers.removeAll(pendingRemovals);
                pendingRemovals.clear();
            }
        }
    }

    private void invokeInternal(T args) {
        Throwable root = null;
        for(EventHandler<T> handler : handlers) {
            try {
                handler.invoke(this, args);
            }
            catch (Throwable throwable) {
                if(root == null) {
                    root = throwable;
                }
                else {
                    root.addSuppressed(throwable);
                }
            }
        }

        if(root != null) {
            logger.log(Level.WARNING, "exceptions(s) thrown in handler loop", root);
        }
    }

    /**
     * Calls the event handlers for this event, in the order they were registered. If handlers are added or removed
     * during execution, the modifications will only apply after the next invocation of this method.
     * @param args The arguments to call each handler with
     */
    public void invoke(T args) {
        if(invoking) {
            throw new IllegalStateException("cannot recursively invoke an event");
        }

        invoking = true;
        invokeInternal(args);
        invoking = false;

        applyHandlerListModifications();
    }

    /**
     * Adds a handler to this event.
     * @param handler The handler to add
     */
    public void addHandler(@NotNull EventHandler<T> handler) {
        Objects.requireNonNull(handler, "handler cannot be null");
        if(invoking) {
            pendingAdditions.add(handler);
        }
        else {
            handlers.add(handler);
        }
    }

    /**
     * Removings a handler from this event.
     * @param handler The handler to remove
     */
    public void removeHandler(@NotNull EventHandler<T> handler) {
        Objects.requireNonNull(handler, "handler cannot be null");
        if(invoking) {
            pendingRemovals.add(handler);
        }
        else {
            handlers.add(handler);
        }
    }

    /**
     * Clears all handlers from this event. If handlers are in the process of being called, they will finish before any
     * are removed.
     */
    public void clearHandlers() {
        if(invoking) {
            clearFlag = true;
        }
        else {
            handlers.clear();
        }
    }

    /**
     * Returns the number of active handlers. Note that if this function is called inside a handler, and previously
     * called handlers added or removed handlers, this value will not reflect the number of handlers that will be
     * called on the next execution of {@link Event#invoke(Object)}.
     * @return The active handler count
     */
    public int handlerCount() {
        return handlers.size();
    }

    /**
     * Returns the logger used by this event.
     * @return The logger used by this event
     */
    public @NotNull Logger getLogger() {
        return logger;
    }
}