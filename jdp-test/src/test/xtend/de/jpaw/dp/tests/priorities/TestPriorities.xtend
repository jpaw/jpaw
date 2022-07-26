package de.jpaw.dp.tests.priorities

import de.jpaw.dp.Default
import de.jpaw.dp.Fallback
import de.jpaw.dp.Jdp
import de.jpaw.dp.Singleton
import de.jpaw.dp.exceptions.NonuniqueImplementationException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertThrows

interface Leaden {}
interface Metal {}
interface Medal {}
interface Letal {}
interface Grey {}

@Fallback
@Singleton
class Lead implements Metal, Letal, Leaden, Grey {
}

@Singleton
class Bronze implements Metal, Medal {
}

@Singleton
class Silver implements Metal, Medal, Grey {
}

@Default
@Singleton
class Gold implements Metal, Medal {
}

@Singleton
class Mercury implements Metal, Letal, Grey {
}

class TestPriorities {

    @BeforeEach
    def void setup() {
        Jdp.reset
        Jdp.init("de.jpaw.dp.tests.priorities")
    }

    @Test
    def void testFallback1() {
        assertEquals(Lead, Jdp.getRequired(Leaden).class)  // the only class is the fallback one
    }

    @Test
    def void testFallback2() {
        assertEquals(Mercury, Jdp.getRequired(Letal).class)  // fallback is skipped in favour of other implementation
    }

    @Test
    def void testDefault1() {
        assertEquals(Gold, Jdp.getRequired(Metal).class)  // Gold has priority (with Fallback)
    }

    @Test
    def void testDefault2() {
        assertEquals(Gold, Jdp.getRequired(Medal).class)  // Gold has priority (no Fallback)
    }

    @Test
    def void testNonUnique() {
        assertThrows(NonuniqueImplementationException, [
            Jdp.getRequired(Grey)                           // fallback and no default, and multiple candidates
        ])
    }
}
