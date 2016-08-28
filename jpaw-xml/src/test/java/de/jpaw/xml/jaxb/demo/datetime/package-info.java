/** A copy of this annotation is required in every package where the mappings should be used. */

@XmlJavaTypeAdapters({
    @XmlJavaTypeAdapter(type=LocalDate.class,
        value=LocalDateAdapter.class),
    @XmlJavaTypeAdapter(type=LocalTime.class,
        value=LocalTimeAdapter.class),
    @XmlJavaTypeAdapter(type=LocalDateTime.class,
        value=LocalDateTimeAdapter.class)
})
package de.jpaw.xml.jaxb.demo.datetime;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import de.jpaw.xml.jaxb.LocalDateAdapter;
import de.jpaw.xml.jaxb.LocalDateTimeAdapter;
import de.jpaw.xml.jaxb.LocalTimeAdapter;
