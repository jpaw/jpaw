package de.jpaw.dp.tests.dependent

import de.jpaw.dp.Dependent
import de.jpaw.dp.Jdp
import java.util.concurrent.atomic.AtomicInteger
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

interface I1 {
    def int open();
}

interface I2 {
    def int close();
}

interface I3 {
    def int open();        // same function defined in another interface
}

@Dependent
class MultiInterfaceClass implements I1, I2, I3 {
    static final AtomicInteger instanceCounter = new AtomicInteger();

    val myInstanceNo = instanceCounter.incrementAndGet

    def static void resetCounter() {
        instanceCounter.set(0)
    }

    override open() {
        return myInstanceNo
    }

    override close() {
        return myInstanceNo
    }
}

class TestMIClassThenInterfaces {

    @BeforeEach
    def void setup() {
        Jdp.reset
        MultiInterfaceClass.resetCounter
        Jdp.init("de.jpaw.dp.tests.dependent")
    }

    @Test
    def void testClassThenInterfaces() {
        assertEquals(1, Jdp.getRequired(MultiInterfaceClass).open)
        assertEquals(2, Jdp.getRequired(I1).open)
        assertEquals(3, Jdp.getRequired(I2).close)
        assertEquals(4, Jdp.getRequired(I3).open)
        assertEquals(5, Jdp.getRequired(MultiInterfaceClass).close)
    }

    @Test
    def void testInterfaceThenClass() {
        assertEquals(1, Jdp.getRequired(I1).open)
        assertEquals(2, Jdp.getRequired(I2).close)
        assertEquals(3, Jdp.getRequired(I3).open)
        assertEquals(4, Jdp.getRequired(MultiInterfaceClass).close)
    }

    @Test
    def void testInterfaceThenClassOrdering2() {
        assertEquals(1, Jdp.getRequired(I2).close)
        assertEquals(2, Jdp.getRequired(I1).open)
        assertEquals(3, Jdp.getRequired(I3).open)
        assertEquals(4, Jdp.getRequired(MultiInterfaceClass).close)
    }
}
