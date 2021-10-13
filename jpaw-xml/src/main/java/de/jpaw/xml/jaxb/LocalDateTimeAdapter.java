package de.jpaw.xml.jaxb;

import java.io.IOException;
import java.time.LocalDateTime;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import de.jpaw.util.FormattersAndParsers;

public class LocalDateTimeAdapter extends XmlAdapter<String, LocalDateTime> {
    public static String  addSuffixTimezone         = null;     // add suffix "Z" (or other) on output (to simulate UTC time zone)    
    public static boolean tolerateSuffixUTC         = false;    // ignore suffix "Z" when parsing
    public static boolean ignoreFractionalSeconds   = false;    // ignore fractional seconds when parsing
    public static boolean outputFractionalSeconds   = true;     // output fractional seconds

    @Override
    public String marshal(LocalDateTime dateTime) {
        try {
            final StringBuilder sb = new StringBuilder(30);
            FormattersAndParsers.appendLocalDateTime(sb, dateTime, outputFractionalSeconds, addSuffixTimezone);
            return sb.toString();
        } catch (IOException e) {
            return null;  // cannot happen (IOException on StringBuilder)
        }
    }

    @Override
    public LocalDateTime unmarshal(String dateTime) {
        return FormattersAndParsers.parseLocalDateTime(dateTime, ignoreFractionalSeconds, tolerateSuffixUTC);
    }
}
