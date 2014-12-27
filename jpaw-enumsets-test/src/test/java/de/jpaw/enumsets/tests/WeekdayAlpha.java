package de.jpaw.enumsets.tests;

import de.jpaw.enums.TokenizableEnum;

public enum WeekdayAlpha implements TokenizableEnum {
    MONDAY, TUESDAY, WEDNESDAY, DONNERSTAG, FRIDAY, SATURDAY, ENDE;  // arrange tokens to start with different letters for demo purposes
    
    @Override
    public String getToken() {
        return name().substring(0, 1);
    }
}
