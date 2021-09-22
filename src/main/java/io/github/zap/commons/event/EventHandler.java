package io.github.zap.commons.event;

import org.jetbrains.annotations.NotNull;

/**
 * Functional interface representing an event handler.
 * @param <T> The type of argument the event handler receives
 */
@FunctionalInterface
public interface EventHandler<T> {
    /**
     * Calls the handler with the given argument.
     * @param sender The object responsible for sending this event
     * @param args The arguments, which may be null at the discretion of the calling event
     */
    void invoke(Object sender, T args);
}
