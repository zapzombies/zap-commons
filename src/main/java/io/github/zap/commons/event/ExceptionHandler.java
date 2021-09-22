package io.github.zap.commons.event;

import org.jetbrains.annotations.NotNull;

/**
 * This interface defines an error-handling mechanism for {@link Event} objects.
 */
@FunctionalInterface
public interface ExceptionHandler {
    /**
     * Handles the given {@link RuntimeException}.
     *
     * In general, this will be called directly after all registered handlers have finished being called, if and only
     * if at least one of them threw an exception that was caught by the event. If more than one threw an exception,
     * there may be suppressed exceptions.
     *
     * It is acceptable for some implementations to simply rethrow the given exception, but typically it should be
     * logged instead.
     * @param exception The exception that was caught by the event
     */
    void handle(@NotNull RuntimeException exception);
}
