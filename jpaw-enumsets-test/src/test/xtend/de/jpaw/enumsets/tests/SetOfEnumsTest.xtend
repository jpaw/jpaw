package de.jpaw.enumsets.tests

import de.jpaw.enums.AbstractIntEnumSet
import de.jpaw.enums.AbstractLongEnumSet
import de.jpaw.enums.AbstractStringEnumSet
import de.jpaw.enums.EnumSetMarker
import de.jpaw.enumsets.SetOfEnum
import org.junit.Test

import static org.junit.Assert.*

enum Weekday {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
}

// enum with implements not working as of xtend 2.7.3
//enum WeekdayAlpha implements TokenizableEnum {
//    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
//
//    override String getToken() {
//        return name.substring(0, 3)
//    }
//}

@SetOfEnum
class SetOfWeekdays extends AbstractIntEnumSet<Weekday> {
    override EnumSetMarker ret$MutableClone(boolean deepCopy, boolean unfreezeCollections) { return null; }
    override EnumSetMarker ret$FrozenClone() { return null; }
}

@SetOfEnum
class LongSetOfWeekdays extends AbstractLongEnumSet<Weekday> {
    override EnumSetMarker ret$MutableClone(boolean deepCopy, boolean unfreezeCollections) { return null; }
    override EnumSetMarker ret$FrozenClone() { return null; }
}

@SetOfEnum
class StringSetOfWeekdays extends AbstractStringEnumSet<WeekdayAlpha> {
    override EnumSetMarker ret$MutableClone(boolean deepCopy, boolean unfreezeCollections) { return null; }
    override EnumSetMarker ret$FrozenClone() { return null; }
}


class SetOfEnumsTest {

    @Test
    def void testSetOfEnums() {
        val mySet = SetOfWeekdays.ofTokens(Weekday.MONDAY, Weekday.WEDNESDAY, Weekday.FRIDAY)

        for (d : mySet)
            println('''«d.name» is in the set''')
        println('''The bitmap is «mySet.bitmap»''')
    }

    @Test
    def void testLongSetOfEnums() {
        val mySet = LongSetOfWeekdays.ofTokens(Weekday.MONDAY, Weekday.WEDNESDAY, Weekday.FRIDAY)

        for (d : mySet)
            println('''«d.name» is in the set''')
        println('''The bitmap is «mySet.bitmap»''')
    }

    @Test
    def void testStringSetOfEnums() {
        val mySet = StringSetOfWeekdays.ofTokens(WeekdayAlpha.MONDAY, WeekdayAlpha.WEDNESDAY, WeekdayAlpha.FRIDAY)

        for (d : mySet)
            println('''«d.name» is in the set''')
        println('''The bitmap is «mySet.bitmap»''')
    }


    @Test
    def void testLongComplement() {
        val mySet = LongSetOfWeekdays.ofTokens(Weekday.MONDAY, Weekday.WEDNESDAY, Weekday.FRIDAY)

        mySet.complement

        assertEquals(mySet.size, 4)

        for (d : mySet)
            println('''«d.name» is in the set''')
        println('''The bitmap is «mySet.bitmap»''')
    }


    @Test
    def void testLongXor() {
        val flipsInTheMorning = LongSetOfWeekdays.ofTokens(Weekday.MONDAY, Weekday.WEDNESDAY, Weekday.FRIDAY)
        val flipsInTheEvening = LongSetOfWeekdays.ofTokens(Weekday.SUNDAY, Weekday.FRIDAY, Weekday.SATURDAY)

        val changeMidnightToMidnight = new LongSetOfWeekdays();
        changeMidnightToMidnight.exactlyOneOf(flipsInTheMorning)
        changeMidnightToMidnight.exactlyOneOf(flipsInTheEvening)

        assertEquals(changeMidnightToMidnight.size, 4)

        assertEquals(changeMidnightToMidnight, LongSetOfWeekdays.ofTokens(Weekday.MONDAY, Weekday.WEDNESDAY, Weekday.SUNDAY, Weekday.SATURDAY))
    }

    @Test
    def void testXor() {
        val flipsInTheMorning = SetOfWeekdays.ofTokens(Weekday.MONDAY, Weekday.WEDNESDAY, Weekday.FRIDAY)
        val flipsInTheEvening = SetOfWeekdays.ofTokens(Weekday.SUNDAY, Weekday.FRIDAY, Weekday.SATURDAY)

        val changeMidnightToMidnight = new SetOfWeekdays();
        changeMidnightToMidnight.exactlyOneOf(flipsInTheMorning)
        changeMidnightToMidnight.exactlyOneOf(flipsInTheEvening)

        assertEquals(changeMidnightToMidnight.size, 4)

        assertEquals(changeMidnightToMidnight, SetOfWeekdays.ofTokens(Weekday.MONDAY, Weekday.WEDNESDAY, Weekday.SUNDAY, Weekday.SATURDAY))
    }

}
