package de.jpaw.xml.jaxb.demo.joda;

// courtesy of http://blog.bdoughan.com/2011/05/jaxb-and-joda-time-dates-and-times.html

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@XmlRootElement
@XmlType(propOrder={
   "dateMidnight",
   "localDate",
   "localTime",
   "localDateTime"})

public class JodaTimeMappers {

    private LocalDate localDate;
    private LocalTime localTime;
    private LocalDateTime localDateTime;

    public LocalDate getLocalDate() {
        return localDate;
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }

    public LocalTime getLocalTime() {
        return localTime;
    }

    public void setLocalTime(LocalTime localTime) {
        this.localTime = localTime;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }
}
