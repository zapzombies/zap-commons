package io.github.zap.commons.event;

import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Logger;

/**
 * Basic implementation of {@link Event}. It may be used directly or subclassed to add additional functionality.
 *
 * SimpleEvent uses an array internally to store handlers. Iterating an array can be faster than iterating a list, so
 * this class should be performant in the "typical" usage case for an event (where handler addition or removal is rare,
 * but handler invocation is common, perhaps extremely often as in the case of Bukkit events like
 * {@link org.bukkit.event.player.PlayerMoveEvent}). In these instances, the performance bonus for using an array is
 * significant, and in fact Bukkit takes advantage of this (see {@link org.bukkit.event.HandlerList}).
 *
 * The size of the internal array scales similarly to that of {@link ArrayList}, except you can specify the amount by
 * which the array size is increased per resize in the constructor. The array is only resized when it is necessary to
 * do so to fit a new handler. The default increment is 8.
 *
 * SimpleEvent instances catch all exceptions thrown by any registered handlers. Caught exceptions may only be
 * "handled" by user code after all events have been called. This is done by supplying a custom instance of
 * {@link ExceptionHandler} in the constructor (see {@link ExceptionHandlers} for default factory methods). The
 * handler will be passed the <i>first</i> {@link Throwable} instance that was produced, if any, and subsequent
 * Throwables will be suppressed (see {@link Throwable#addSuppressed(Throwable)}). The default exception handler will
 * be used if none is supplied, which is to simply log exceptions using the global logger.
 *
 * This class is not thread safe. It does not support recursively invoking handlers (that is, calling the
 * {@link SimpleEvent#invoke(Object)} method while inside a handler that is currently being invoked by this instance).
 * Attempting to do so will result in an unchecked {@link IllegalStateException}.
 * @param <T> The type of argument handlers will receive
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class SimpleEvent<T> implements Event<T> {
    private static final int DEFAULT_EXCESS_CAPACITY = 8;

    private class HandlerModification {
        private final boolean isAdd;
        private final EventHandler<T> handler;

        private HandlerModification(boolean isAdd, EventHandler<T> handler) {
            this.isAdd = isAdd;
            this.handler = handler;
        }
    }

    private EventHandler[] bakedHandlers;

    private final ExceptionHandler exceptionHandler;
    private final int excessCapacity;
    private final List<HandlerModification> modifications = new LinkedList<>();

    private int size = 0;
    private boolean invoking = false;
    private boolean clearFlag = false;
    private boolean rebuildFlag = false;

    public SimpleEvent(@NotNull ExceptionHandler exceptionHandler, int excessCapacity) {
        Validate.isTrue(excessCapacity >= 0, "excessCapacity cannot be negative");

        this.exceptionHandler = Objects.requireNonNull(exceptionHandler, "exceptionHandler cannot be null");
        this.excessCapacity = excessCapacity;
        this.bakedHandlers = new EventHandler[excessCapacity];
    }

    public SimpleEvent(@NotNull ExceptionHandler exceptionHandler) {
        this(exceptionHandler, DEFAULT_EXCESS_CAPACITY);
    }

    public SimpleEvent(@NotNull Logger logger) {
        this(ExceptionHandlers.logHandler(logger));
    }

    public SimpleEvent() {
        this(ExceptionHandlers.logHandler(Logger.getGlobal()));
    }

    private void addHandlerInternal(EventHandler<T> handler) {
        if(size >= bakedHandlers.length) {
            bakedHandlers = Arrays.copyOf(bakedHandlers, bakedHandlers.length + excessCapacity + 1);
        }

        bakedHandlers[size++] = handler;
    }

    private boolean removeHandlerInternal(EventHandler<T> handler) {
        for(int i = 0; i < size; i++) {
            if(bakedHandlers[i] == handler) {
                bakedHandlers[i] = null;
                return true;
            }
        }

        return false;
    }

    private void clearHandlersInternal() {
        modifications.clear();
        Arrays.fill(bakedHandlers, null);
        size = 0;
    }

    private void rebuild() {
        if(clearFlag) {
            clearHandlersInternal();
            clearFlag = false;
        }

        if(rebuildFlag) {
            int previousNull = -1;
            boolean hasPreviousNull = false;

            int newSize = size;
            for(int i = 0; i < size; i++) {
                EventHandler handler = bakedHandlers[i];

                if(hasPreviousNull) {
                    if(handler != null) {
                        bakedHandlers[previousNull] = handler;
                        bakedHandlers[i] = null;
                        previousNull++;
                    }
                    else {
                        newSize--;
                    }
                }
                else if(handler == null) {
                    hasPreviousNull = true;
                    previousNull = i;
                    newSize--;
                }
            }

            size = newSize;
            rebuildFlag = false;
        }
    }

    private void invokeInternal(T args) {
        RuntimeException first = null;
        for(int i = 0; i < size; i++) {
            EventHandler handler = bakedHandlers[i];

            try {
                handler.invoke(this, args);
            }
            catch (RuntimeException exception) {
                if(first == null) {
                    first = exception;
                }
                else {
                    first.addSuppressed(exception);
                }
            }
        }

        if(first != null) {
            exceptionHandler.handle(first);
        }
    }

    private void processModifications() {
        if(!modifications.isEmpty()) {
            Iterator<HandlerModification> modificationIterator = modifications.listIterator();

            boolean removedAny = false;
            while(modificationIterator.hasNext()) {
                HandlerModification modification = modificationIterator.next();
                modificationIterator.remove();

                if(modification.isAdd) {
                    addHandlerInternal(modification.handler);
                }
                else if(removeHandlerInternal(modification.handler)) {
                    removedAny = true;
                }
            }

            if(removedAny) {
                rebuildFlag = true;
            }
        }

        rebuild();
    }

    @Override
    public void invoke(T args) {
        if(invoking) {
            throw new IllegalStateException("cannot recursively invoke an event");
        }

        processModifications();

        invoking = true;
        invokeInternal(args);
        invoking = false;

        processModifications();
    }

    @Override
    public void addHandler(@NotNull EventHandler<T> handler) {
        if(!invoking) {
            addHandlerInternal(handler);
        }
        else {
            modifications.add(new HandlerModification(true, handler));
        }
    }

    @Override
    public void removeHandler(@NotNull EventHandler<T> handler) {
        if(!invoking) {
            if(removeHandlerInternal(handler)) {
                rebuildFlag = true;
            }
        }
        else {
            modifications.add(new HandlerModification(false, handler));
        }
    }

    @Override
    public boolean hasHandler(@NotNull EventHandler<T> handler) {
        for(int i = 0; i < size; i++) {
            if(bakedHandlers[i] == handler) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void clearHandlers() {
        modifications.clear();
        rebuildFlag = false;

        if(!invoking) {
            clearHandlersInternal();
        }
        else {
            clearFlag = true;
        }
    }

    @Override
    public int handlerCount() {
        return size;
    }
}