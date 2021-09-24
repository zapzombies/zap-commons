package io.github.zap.commons;

/**
 * Same functionality as {@link Runnable} but allow throwable to throw without the need for try/catch
 */
@FunctionalInterface
public interface ThrowableRunnable {
    void run() throws Throwable;
}
