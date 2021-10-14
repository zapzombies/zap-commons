package io.github.zap.commons.event;

import org.apache.commons.lang.mutable.MutableInt;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SimpleEventTest {
    private static final ExceptionHandler handler = exception -> {
        throw new RuntimeException(exception);
    };

    private final Event<Integer> simpleEvent = new SimpleEvent<>(handler, 8);

    @Test
    void multipleHandlers() {
        MutableInt mutableInt = new MutableInt(0);

        simpleEvent.addHandler((event, args) -> mutableInt.increment());
        simpleEvent.addHandler((event, args) -> mutableInt.increment());
        simpleEvent.addHandler((event, args) -> mutableInt.increment());
        simpleEvent.handle(this,69420);

        Assertions.assertSame( 3, mutableInt.intValue());
    }

    @Test
    void singleRemove() {
        MutableInt first = new MutableInt(0);

        EventHandler<Integer> remove = (event, args) -> first.increment();
        simpleEvent.addHandler((event, args) -> first.increment());
        simpleEvent.addHandler(remove);

        simpleEvent.removeHandler(remove);
        simpleEvent.handle(this,0);

        Assertions.assertSame(1, first.intValue());
    }

    @Test
    void clear() {
        MutableInt mutableInt = new MutableInt(0);

        simpleEvent.addHandler((sender, args) -> mutableInt.increment());
        simpleEvent.addHandler((sender, args) -> mutableInt.increment());
        simpleEvent.addHandler((sender, args) -> mutableInt.increment());
        simpleEvent.clearHandlers();

        simpleEvent.handle(this,0);

        Assertions.assertSame(0, mutableInt.intValue());
    }

    @Test
    void nestedRegistration() {
        MutableInt mutableInt = new MutableInt(0);

        simpleEvent.addHandler((sender, args) -> {
            mutableInt.increment();
            simpleEvent.addHandler((sender1, args1) -> mutableInt.increment());
        });

        simpleEvent.handle(this,0);
        simpleEvent.handle(this,0);

        Assertions.assertSame(3, mutableInt.intValue());
    }

    @Test
    void nestedRemoval() {
        MutableInt mutableInt = new MutableInt(0);

        EventHandler<Integer> removed = (event, args) -> mutableInt.increment();

        simpleEvent.addHandler(removed);
        simpleEvent.addHandler((sender, args) -> {
            mutableInt.increment();
            simpleEvent.removeHandler(removed);
        });

        simpleEvent.handle(this,0);
        Assertions.assertSame(2, mutableInt.intValue());
        mutableInt.setValue(0);

        simpleEvent.handle(this, 0);
        Assertions.assertSame(1, mutableInt.intValue());
    }

    @Test
    void sporadicPreRemoval() {
        MutableInt mutableInt = new MutableInt(0);

        EventHandler<Integer> removed = (sender, args) -> mutableInt.increment();
        EventHandler<Integer> removed1 = (sender, args) -> mutableInt.increment();
        EventHandler<Integer> removed2 = (sender, args) -> mutableInt.increment();

        simpleEvent.addHandler((sender, args) -> mutableInt.increment());
        simpleEvent.addHandler((sender, args) -> mutableInt.increment());
        simpleEvent.addHandler(removed);
        simpleEvent.addHandler((sender, args) -> mutableInt.increment());
        simpleEvent.addHandler(removed1);
        simpleEvent.addHandler(removed2);

        simpleEvent.removeHandler(removed);
        simpleEvent.removeHandler(removed1);
        simpleEvent.removeHandler(removed2);

        simpleEvent.handle(this,0);
        Assertions.assertSame(3, mutableInt.intValue());
    }

    @Test
    void sporadicNestedRemoval() {
        MutableInt mutableInt = new MutableInt(0);

        EventHandler<Integer> removed = (event, args) -> mutableInt.increment();
        EventHandler<Integer> removed1 = (event, args) -> mutableInt.increment();
        EventHandler<Integer> removed2 = (event, args) -> mutableInt.increment();

        simpleEvent.addHandler((sender, args) -> mutableInt.increment());
        simpleEvent.addHandler(removed);
        simpleEvent.addHandler(removed1);
        simpleEvent.addHandler((sender, args) -> mutableInt.increment());
        simpleEvent.addHandler((sender, args) -> mutableInt.increment());
        simpleEvent.addHandler((sender, args) -> {
            simpleEvent.removeHandler(removed);
            simpleEvent.removeHandler(removed1);
            simpleEvent.removeHandler(removed2);
        });
        simpleEvent.addHandler(removed2);

        simpleEvent.handle(this,0);
        Assertions.assertSame(6, mutableInt.intValue());

        mutableInt.setValue(0);
        simpleEvent.handle(this,0);
        Assertions.assertSame(3, mutableInt.intValue());
    }

    @Test
    void nestedClear() {
        MutableInt mutableInt = new MutableInt(0);
        simpleEvent.addHandler((event, args) -> {
            mutableInt.increment();
            simpleEvent.clearHandlers();
        });

        simpleEvent.handle(this,0);
        Assertions.assertSame(1, mutableInt.intValue());

        simpleEvent.handle(this,0);
        Assertions.assertSame(1, mutableInt.intValue());
    }

    @Test
    void hasHandler() {
        EventHandler<Integer> handler = (event, args) -> {};
        simpleEvent.addHandler(handler);

        Assertions.assertTrue(simpleEvent.hasHandler(handler));
    }

    @Test
    void exception() {
        MutableInt integer = new MutableInt();
        simpleEvent.addHandler((sender, args) -> integer.increment());
        simpleEvent.addHandler((sender, args) -> {
            integer.increment();
            throw new ArrayIndexOutOfBoundsException();
        });
        simpleEvent.addHandler((sender, args) -> integer.increment());

        Assertions.assertThrows(RuntimeException.class, () -> simpleEvent.handle(this, 0));
        Assertions.assertSame(3, integer.intValue());
    }
}