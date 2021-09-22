package io.github.zap.commons.event;

import org.apache.commons.lang.mutable.MutableInt;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SimpleEventTest {
    private final ExceptionHandler handler = exception -> {
        throw exception;
    };

    private final Event<Integer> simpleEvent = new SimpleEvent<>(handler, 8);

    @Test
    void identity() {
        MutableInt mutableInt = new MutableInt();
        simpleEvent.addHandler((event, integer) -> {
            assertSame(simpleEvent, event);
            mutableInt.setValue(69);
        });

        simpleEvent.invoke(0);
        Assertions.assertSame( 69, mutableInt.intValue(), "event was not called");
    }

    @Test
    void multipleHandlers() {
        MutableInt mutableInt = new MutableInt(0);

        simpleEvent.addHandler((event, args) -> mutableInt.increment());
        simpleEvent.addHandler((event, args) -> mutableInt.increment());
        simpleEvent.addHandler((event, args) -> mutableInt.increment());
        simpleEvent.invoke(69420);

        Assertions.assertSame( 3, mutableInt.intValue());
    }

    @Test
    void singleRemove() {
        MutableInt first = new MutableInt(0);

        EventHandler<Integer> remove = (event, args) -> first.increment();
        simpleEvent.addHandler((event, args) -> first.increment());
        simpleEvent.addHandler(remove);

        simpleEvent.removeHandler(remove);
        simpleEvent.invoke(0);

        Assertions.assertSame(1, first.intValue());
    }

    @Test
    void clear() {
        MutableInt mutableInt = new MutableInt(0);

        simpleEvent.addHandler((event, args) -> mutableInt.increment());
        simpleEvent.addHandler((event, args) -> mutableInt.increment());
        simpleEvent.addHandler((event, args) -> mutableInt.increment());
        simpleEvent.clearHandlers();

        simpleEvent.invoke(0);

        Assertions.assertSame(0, mutableInt.intValue());
    }

    @Test
    void nestedRegistration() {
        MutableInt mutableInt = new MutableInt(0);

        simpleEvent.addHandler((event, args) -> {
            mutableInt.increment();
            event.addHandler((event1, args1) -> mutableInt.increment());
        });

        simpleEvent.invoke(0);
        simpleEvent.invoke(0);

        Assertions.assertSame(3, mutableInt.intValue());
    }

    @Test
    void nestedRemoval() {
        MutableInt mutableInt = new MutableInt(0);

        EventHandler<Integer> removed = (event, args) -> mutableInt.increment();

        simpleEvent.addHandler(removed);
        simpleEvent.addHandler((event, args) -> {
            mutableInt.increment();
            event.removeHandler(removed);
        });

        simpleEvent.invoke(0);
        Assertions.assertSame(2, mutableInt.intValue());
        mutableInt.setValue(0);

        simpleEvent.invoke(0);
        Assertions.assertSame(1, mutableInt.intValue());
    }

    @Test
    void sporadicPreRemoval() {
        MutableInt mutableInt = new MutableInt(0);

        EventHandler<Integer> removed = (event, args) -> mutableInt.increment();
        EventHandler<Integer> removed1 = (event, args) -> mutableInt.increment();
        EventHandler<Integer> removed2 = (event, args) -> mutableInt.increment();

        simpleEvent.addHandler((event, args) -> mutableInt.increment());
        simpleEvent.addHandler((event, args) -> mutableInt.increment());
        simpleEvent.addHandler(removed);
        simpleEvent.addHandler((event, args) -> mutableInt.increment());
        simpleEvent.addHandler(removed1);
        simpleEvent.addHandler(removed2);

        simpleEvent.removeHandler(removed);
        simpleEvent.removeHandler(removed1);
        simpleEvent.removeHandler(removed2);

        simpleEvent.invoke(0);
        Assertions.assertSame(3, mutableInt.intValue());
    }

    @Test
    void sporadicNestedRemoval() {
        MutableInt mutableInt = new MutableInt(0);

        EventHandler<Integer> removed = (event, args) -> mutableInt.increment();
        EventHandler<Integer> removed1 = (event, args) -> mutableInt.increment();
        EventHandler<Integer> removed2 = (event, args) -> mutableInt.increment();

        simpleEvent.addHandler((event, args) -> mutableInt.increment());
        simpleEvent.addHandler(removed);
        simpleEvent.addHandler(removed1);
        simpleEvent.addHandler((event, args) -> mutableInt.increment());
        simpleEvent.addHandler((event, args) -> mutableInt.increment());
        simpleEvent.addHandler((event, args) -> {
            event.removeHandler(removed);
            event.removeHandler(removed1);
            event.removeHandler(removed2);
        });
        simpleEvent.addHandler(removed2);

        simpleEvent.invoke(0);
        Assertions.assertSame(6, mutableInt.intValue());

        mutableInt.setValue(0);
        simpleEvent.invoke(0);
        Assertions.assertSame(3, mutableInt.intValue());
    }

    @Test
    void nestedClear() {
        MutableInt mutableInt = new MutableInt(0);
        simpleEvent.addHandler((event, args) -> {
            mutableInt.increment();
            event.clearHandlers();
        });

        simpleEvent.invoke(0);
        Assertions.assertSame(1, mutableInt.intValue());

        simpleEvent.invoke(0);
        Assertions.assertSame(1, mutableInt.intValue());
    }

    @Test
    void hasHandler() {
        EventHandler<Integer> handler = (event, args) -> {};
        simpleEvent.addHandler(handler);

        Assertions.assertTrue(simpleEvent.hasHandler(handler));
    }
}