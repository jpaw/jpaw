package de.jpaw.enumsets.tests

import de.jpaw.enums.AbstractEnumSet
import de.jpaw.enumsets.SetOfEnum
import org.testng.annotations.Test

enum Weekday {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
}

@SetOfEnum
class SetOfWeekdays extends AbstractEnumSet<Weekday> {}

class SetOfEnumsTest {

    @Test
    def public void testSetOfEnums() {
        val mySet = SetOfWeekdays.of(Weekday.MONDAY, Weekday.WEDNESDAY, Weekday.FRIDAY)
        
        for (d : mySet)
            println('''«d.name» is in the set''')
    }    
}
