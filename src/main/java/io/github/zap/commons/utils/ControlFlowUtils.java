package io.github.zap.commons.utils;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Contains several methods to help with the flow of code execution
 */
public class ControlFlowUtils {
    /**
     * A clone of Supplier<T> that allow implementor to throw Throwable
     * @param <T> the type of results supplied by this supplier
     */
    @FunctionalInterface
    public interface ThrowableSupplier<T> {
        /**
         * Gets a result.
         *
         * @return a result
         */
        T get() throws Throwable;
    }

    /**
     * Attempt to execute the action and retrying if the action throw any throwable
     * @param action action to execute
     * @param <T> the return type of the action
     * @return the action result
     * @throws RuntimeException a wrapped exception represent the last failure attempt error
     */
    public static <T> T retry(@NotNull ThrowableSupplier<T> action) throws RuntimeException {
        return retry(3, action);
    }

    /**
     * Attempt to execute the action and retrying if the action throw any throwable
     * @param retryCount the number of attempt to execute action on failure
     * @param action action to execute
     * @param <T> the return type of the action
     * @return the action result
     * @throws RuntimeException a wrapped exception represent the last failure attempt error
     */
    public static <T> T retry(int retryCount, @NotNull ThrowableSupplier<T> action) throws RuntimeException {
        Throwable lastError = null;
        for(int i = 0; i < retryCount; i++) {
            try {
                return action.get();
            } catch (Throwable t) {
                lastError = t;
            }
        }

        throw new RuntimeException(lastError);
    }
}
