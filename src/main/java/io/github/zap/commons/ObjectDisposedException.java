package io.github.zap.commons;

public class ObjectDisposedException extends RuntimeException {
    public ObjectDisposedException() {
        super("Object has been disposed.");
    }
}
