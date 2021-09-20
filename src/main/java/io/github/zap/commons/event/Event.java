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

    private boolean invoking = false;
    private boolean clearFlag = false;
    private final List<EventHandler<T>> handlers = new ArrayList<>();

    private final List<EventHandler<T>> pendingAdditions = new ArrayList<>();
    private final List<EventHandler<T>> pendingRemovals = new ArrayList<>();

    public Event(@NotNull Logger logger) {
        this.logger = logger;
    }

    protected void applyHandlerListModifications() {
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

    protected void invokeInternal(T args) {
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
            logger.log(Level.WARNING, "exceptions(s) thrown during handler loop", root);
        }
    }

    /**
     * Calls the event handlers for this event, in the order they were registered. If handlers are added or removed
     * during execution, the modifications will only apply after the next invocation of this method.
     * @param args The arguments to call each handler with
     */
    public void invoke(T args) {
        if(isInvoking()) {
            throw new IllegalStateException("cannot recursively invoke an event");
        }

        setInvoking(true);
        invokeInternal(args);
        setInvoking(false);

        applyHandlerListModifications();
    }

    /**
     * Adds a handler to this event.
     * @param handler The handler to add
     */
    public void addHandler(@NotNull EventHandler<T> handler) {
        Objects.requireNonNull(handler, "handler cannot be null");
        if(isInvoking()) {
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
        if(isInvoking()) {
            pendingRemovals.add(handler);
        }
        else {
            handlers.add(handler);
        }
    }

    public void clearHandlers() {
        if(isInvoking()) {
            clearFlag = true;
        }
        else {
            handlers.clear();
        }
    }

    /**
     * Returns the number of active handlers. Note that if this function is called inside of a handler, and previously
     * called handlers added or removed handlers, this value will not reflect the number of handlers that will be
     * called on the next execution of {@link Event#invoke(Object)}.
     * @return The active handler count
     */
    public int handlerCount() {
        return handlers.size();
    }

    /**
     * Returns true if this event is in the process of calling its handlers; and false otherwise.
     * @return True if this event is in the process of calling its handlers; and false otherwise
     */
    public boolean isInvoking() {
        return invoking;
    }

    protected void setInvoking(boolean invoking) {
        this.invoking = invoking;
    }

    /**
     * Returns the logger used by this event.
     * @return The logger used by this event
     */
    public @NotNull Logger getLogger() {
        return logger;
    }
}