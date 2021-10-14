package io.github.zap.commons.event;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class providing several default implementations of {@link ExceptionHandler}.
 */
public final class ExceptionHandlers {
    private static final ExceptionHandler RETHROW = exception -> {
        throw new RuntimeException(exception);
    };

    private static final String DEFAULT_ERROR_MESSAGE = "runtime exception(s) thrown by handler(s)";

    private ExceptionHandlers() {}

    /**
     * Creates and returns a new {@link ExceptionHandler} that logs all exceptions thrown using the provided logger,
     * at the provided level, with the provided message.
     * @param logger The logger to use
     * @param level The level at which to record messages
     * @param message The message to show along with each exception
     * @return An ExceptionHandler that logs exceptions
     * @throws NullPointerException if logger or level are null
     */
    public static @NotNull ExceptionHandler logHandler(@NotNull Logger logger, @NotNull Level level,
                                                       @Nullable String message) {
        Objects.requireNonNull(logger, "logger cannot be null");
        Objects.requireNonNull(level, "level cannot be null");
        return exception -> logger.log(level, message, exception);
    }

    /**
     * Overload for {@link ExceptionHandlers#logHandler(Logger, Level, String)} that uses the default error message for
     * all exceptions, and the provided log level.
     * @param logger The logger to use
     * @param level The log level to use
     * @return An ExceptionHandler that logs exceptions
     */
    public static @NotNull ExceptionHandler logHandler(@NotNull Logger logger, @NotNull Level level) {
        return logHandler(logger, level, DEFAULT_ERROR_MESSAGE);
    }

    /**
     * Overload for {@link ExceptionHandlers#logHandler(Logger, Level, String)} that uses the default log level,
     * Level.WARNING, for all exceptions, and the provided error message.
     * @param logger The logger to use
     * @param message The message to send with each exception
     * @return An ExceptionHandler that logs exceptions
     */
    public static @NotNull ExceptionHandler logHandler(@NotNull Logger logger, @NotNull String message) {
        return logHandler(logger, Level.WARNING, message);
    }

    /**
     * Overload for {@link ExceptionHandlers#logHandler(Logger, Level, String)} that uses the default log level,
     * Level.WARNING, for all exceptions, along with the default error message.
     * @param logger The logger to use
     * @return An ExceptionHandler that logs exceptions
     */
    public static @NotNull ExceptionHandler logHandler(@NotNull Logger logger) {
        return logHandler(logger, Level.WARNING, DEFAULT_ERROR_MESSAGE);
    }

    /**
     * Returns the (singleton) {@link ExceptionHandler} that rethrows all {@link Throwable} instances it receives. Note
     * that the thrown exception will <i>always</i> be a {@link RuntimeException} whose cause it set to whatever
     * exception was thrown by handler(s).
     * @return The rethrowing ExceptionHandler
     */
    public static @NotNull ExceptionHandler rethrow() {
        return RETHROW;
    }
}
