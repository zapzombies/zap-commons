package io.github.zap.commons.event;

import org.jetbrains.annotations.NotNull;

class SynchronizedEvent<T> extends WrappedEvent<T> {
    private final Object lock;

    SynchronizedEvent(@NotNull Event<T> wrapped, @NotNull Object lock) {
        super(wrapped);
        this.lock = lock;
    }

    SynchronizedEvent(@NotNull Event<T> wrapped) {
        this(wrapped, new Object());
    }

    @Override
    public void handle(Object sender, T args) {
        synchronized(lock) {
            super.handle(sender, args);
        }
    }

    @Override
    public void addHandler(@NotNull EventHandler<T> handler) {
        synchronized (lock) {
            super.addHandler(handler);
        }
    }

    @Override
    public void removeHandler(@NotNull EventHandler<T> handler) {
        synchronized (lock) {
            super.removeHandler(handler);
        }
    }

    @Override
    public boolean hasHandler(@NotNull EventHandler<T> handler) {
        synchronized (lock) {
            return super.hasHandler(handler);
        }
    }

    @Override
    public void clearHandlers() {
        synchronized (lock) {
            super.clearHandlers();
        }
    }

    @Override
    public int handlerCount() {
        synchronized (lock) {
            return super.handlerCount();
        }
    }

    @Override
    public void addHandlers(@NotNull Iterable<EventHandler<T>> eventHandlers) {
        synchronized (lock) {
            super.addHandlers(eventHandlers);
        }
    }

    @Override
    public void removeHandlers(@NotNull Iterable<EventHandler<T>> eventHandlers) {
        synchronized (lock) {
            super.removeHandlers(eventHandlers);
        }
    }
}
