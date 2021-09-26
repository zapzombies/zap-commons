package io.github.zap.commons.event;

import org.jetbrains.annotations.NotNull;

/**
 * This interface defines an error-handling mechanism for {@link Event} objects.
 */
@FunctionalInterface
public interface ExceptionHandler {
    /**
     * <p>Handles the given {@link RuntimeException}.</p>
     *
     * <p>In general, this will be called directly after all registered handlers have finished being called, if and only
     * if at least one of them threw an exception that was caught by the event. If more than one threw an exception,
     * there may be suppressed exceptions.</p>
     *
     * <p>{@link Event} implementations that use this class should be aware that this method may throw the same
     * exception that was passed to it.</p>
     * @param exception The exception that was caught by the event
     */
    void handle(@NotNull RuntimeException exception);
}
