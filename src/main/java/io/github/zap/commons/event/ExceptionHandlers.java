package io.github.zap.commons.event;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class providing several default implementations of {@link ExceptionHandler}.
 */
public final class ExceptionHandlers {
    private static final ExceptionHandler RETHROW = exception -> {
        throw exception;
    };

    private static final String ERROR_MESSAGE = "runtime exception(s) thrown by handler(s)";

    private ExceptionHandlers() {}

    public static @NotNull ExceptionHandler logHandler(@NotNull Logger logger) {
        Objects.requireNonNull(logger, "logger cannot be null");
        return exception -> logger.log(Level.WARNING, ERROR_MESSAGE, exception);
    }

    public static @NotNull ExceptionHandler rethrow() {
        return RETHROW;
    }
}
