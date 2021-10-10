package io.github.zap.commons.event;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Used internally by some utility methods in {@link Event}.
 * @param <T> The type of argument the event receives
 */
class WrappedEvent<T> implements Event<T> {
     private final Event<T> wrapped;

     WrappedEvent(@NotNull Event<T> wrapped) {
        this.wrapped = Objects.requireNonNull(wrapped, "wrapped cannot be null");
     }

     @Override
     public void invoke(Object sender, T args) {
        wrapped.invoke(sender, args);
     }

     @Override
     public @NotNull Event<T> addHandler(@NotNull EventHandler<T> handler) {
        return wrapped.addHandler(handler);
     }

     @Override
     public @NotNull Event<T> removeHandler(@NotNull EventHandler<T> handler) {
        return wrapped.removeHandler(handler);
     }

     @Override
     public boolean hasHandler(@NotNull EventHandler<T> handler) {
         return wrapped.hasHandler(handler);
     }

     @Override
     public void clearHandlers() {
        wrapped.clearHandlers();
     }

     @Override
     public int handlerCount() {
         return wrapped.handlerCount();
     }
}
