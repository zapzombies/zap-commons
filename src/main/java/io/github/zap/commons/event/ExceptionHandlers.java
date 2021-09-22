package io.github.zap.commons.event;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class providing several default implementations of {@link ExceptionHandler}.
 */
public final class ExceptionHandlers {
    private ExceptionHandlers() {}

    public static @NotNull ExceptionHandler logHandler(@NotNull Logger logger) {
        Objects.requireNonNull(logger, "logger cannot be null");
        return exception -> logger.log(Level.WARNING, "runtime exceptions(s) thrown by event handler(s)", exception);
    }

    public static @NotNull ExceptionHandler rethrow() {
        return exception -> {
            throw exception;
        };
    }
}
