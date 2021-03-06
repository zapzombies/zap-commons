package io.github.zap.commons.event;

import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Logger;

/**
 * <p>Basic implementation of {@link Event}. It may be used directly or subclassed to add additional functionality.</p>
 *
 * <p>SimpleEvent uses an array internally to store handlers. This allows for slight optimization in adding and
 * removing elements. The size of the internal handler array scales similarly to that of an {@link ArrayList}. The
 * default capacity is 8.</p>
 *
 * <p>SimpleEvent instances catch all exceptions thrown by any registered handlers. Caught exceptions may only be
 * "handled" by user code after all events have been called. This is done by supplying a custom instance of
 * {@link ExceptionHandler} in the constructor (see {@link ExceptionHandlers} for default factory methods). The handler
 * will be passed the <i>first</i> {@link Throwable} instance that was produced, if any, and subsequent Throwables will
 * be suppressed (see {@link Throwable#addSuppressed(Throwable)}). The default exception handler will be used if none is
 * supplied, which is to rethrow a single, representative exception with all subsequent exceptions after the first being
 * suppressed.</p>
 *
 * <p>This class is not thread safe. Unlike many Java collections, it does <i>not</i> guarantee fail-fast behavior if
 * improper concurrent modifications are made. The user is responsible for ensuring thread safety by using synchronized
 * implementations when necessary.</p>
 *
 * <p>SimpleEvent calls handlers in the same order that they are registered (FIFO). Adding, removing, and clearing
 * handlers also respect this order.</p>
 *
 * <p>An attempt to recursively invoke this event will result in an unchecked {@link IllegalStateException}. Events are
 * called recursively when at least one handler calls {@link Event#invoke(Object, Object)} during its own execution.</p>
 * @param <T> The type of argument handlers will receive
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class SimpleEvent<T> implements Event<T> {
    private static final int DEFAULT_INITIAL_CAPACITY = 8;

    private class HandlerModification {
        private final boolean isAdd;
        private final boolean isClear;
        private final EventHandler<T> handler;

        private HandlerModification(boolean isAdd, boolean isClear, EventHandler<T> handler) {
            this.isAdd = isAdd;
            this.isClear = isClear;
            this.handler = handler;
        }
    }

    private EventHandler[] bakedHandlers;

    private final ExceptionHandler exceptionHandler;
    private final List<HandlerModification> modifications = new ArrayList<>();

    private int size = 0;
    private boolean invoking = false;

    public SimpleEvent(@NotNull ExceptionHandler exceptionHandler, int initialCapacity) {
        Validate.isTrue(initialCapacity >= 0, "initialCapacity cannot be negative");

        this.exceptionHandler = Objects.requireNonNull(exceptionHandler, "exceptionHandler cannot be null");
        this.bakedHandlers = new EventHandler[initialCapacity];
    }

    public SimpleEvent(@NotNull ExceptionHandler exceptionHandler) {
        this(exceptionHandler, DEFAULT_INITIAL_CAPACITY);
    }

    public SimpleEvent(@NotNull Logger logger) {
        this(ExceptionHandlers.logHandler(logger));
    }

    public SimpleEvent() {
        this(ExceptionHandlers.rethrow());
    }

    private void addHandlerInternal(EventHandler<T> handler) {
        if(size >= bakedHandlers.length) {
            //scale by 3/2 each resize
            bakedHandlers = Arrays.copyOf(bakedHandlers, ((bakedHandlers.length * 3) >> 1) + 1);
        }

        bakedHandlers[size++] = handler;
    }

    private int removeHandlerInternal(EventHandler<T> handler) {
        for(int i = 0; i < size; i++) {
            if(bakedHandlers[i] == handler) {
                bakedHandlers[i] = null;
                return i;
            }
        }

        return -1;
    }

    private boolean clearHandlersInternal() {
        if(size > 0) {
            Arrays.fill(bakedHandlers, null);
            size = 0;
            return true;
        }

        return false;
    }

    private void rebuild(int start) {
        int previousNull = -1;
        boolean hasPreviousNull = false;
        int newSize = size;
        for(int i = start; i < size; i++) {
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
        if(bakedHandlers.length - size > bakedHandlers.length >> 1) {
            bakedHandlers = Arrays.copyOf(bakedHandlers, size + 1);
        }
    }

    private void processModifications() {
        if(!modifications.isEmpty()) {
            int oldSize = size;

            if(modifications.get(0).isClear) {
                clearHandlersInternal();
                modifications.remove(0);
            }

            int firstRemovedIndex = -1;
            for(HandlerModification modification : modifications) {
                int thisIndex;
                if(modification.isAdd) {
                    addHandlerInternal(modification.handler);
                }
                else if((thisIndex = removeHandlerInternal(modification.handler)) != -1 && firstRemovedIndex == -1) {
                    firstRemovedIndex = thisIndex;
                }
            }

            modifications.clear();

            if(firstRemovedIndex != -1) {
                rebuild(firstRemovedIndex);
            }

            if(oldSize != size) {
                onHandlerCountChange(oldSize, size);
            }
        }
    }

    private void invokeInternal(Object sender, T args) {
        RuntimeException first = null;
        for(int i = 0; i < size; i++) {
            EventHandler handler = bakedHandlers[i];

            try {
                handler.invoke(sender, args);
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

    @Override
    public void invoke(Object sender, T args) {
        if(invoking) {
            throw new IllegalStateException("cannot recursively invoke an event");
        }

        try {
            preInvoke();
            invoking = true;
            invokeInternal(sender, args);
        }
        finally { //if exceptionHandler throws a runtime exception, make sure we keep internal state consistent
            invoking = false;
            postInvoke();
        }
    }

    @Override
    public void addHandler(@NotNull EventHandler<T> handler) {
        if(!invoking) {
            int oldSize = size;
            addHandlerInternal(handler);

            if(oldSize != size) {
                onHandlerCountChange(oldSize, size);
            }
        }
        else {
            modifications.add(new HandlerModification(true, false, handler));
        }
    }

    @Override
    public void removeHandler(@NotNull EventHandler<T> handler) {
        if(!invoking) {
            int removedIndex;
            if((removedIndex = removeHandlerInternal(handler)) != -1) {
                int oldSize = size;
                rebuild(removedIndex);

                if(oldSize != size) {
                    onHandlerCountChange(oldSize, size);
                }
            }
        }
        else {
            modifications.add(new HandlerModification(false, false, handler));
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
        if(!invoking) {
            int oldSize = size;
            if(clearHandlersInternal()) {
                onHandlerCountChange(oldSize, 0);
            }
        }
        else {
            modifications.clear(); //remove all prior modifications
            modifications.add(new HandlerModification(false, true, null));
        }
    }

    @Override
    public int handlerCount() {
        return size;
    }

    /**
     * Called internally when modifications are made to the array of handlers. The default implementation does nothing;
     * this method is provided for the sole purpose of overriding by subclasses. For example, subclasses may need to
     * perform cleanup actions when all handlers are removed, or initialization actions when the first handler is added.
     * @param oldSize The previous number of handlers
     * @param newSize The new number of handlers (will not be equal to oldSize)
     */
    protected void onHandlerCountChange(int oldSize, int newSize) {}

    /**
     * Called internally just before handlers will be called. The default implementation does nothing.
     */
    protected void preInvoke() {}

    /**
     * Called internally directly after handlers are called (even if an exception is rethrown). The default
     * implementation ensures that the internal array of handlers is up-to-date with any modifications that have been
     * applied during handler execution.
     */
    protected void postInvoke() {
        processModifications();
    }
}