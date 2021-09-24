package io.github.zap.commons;

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
