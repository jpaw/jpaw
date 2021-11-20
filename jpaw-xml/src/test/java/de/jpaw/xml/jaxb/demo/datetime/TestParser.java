package de.jpaw.xml.jaxb.demo.datetime;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import de.jpaw.xml.jaxb.LocalDateTimeAdapter;

public class TestParser {
    private static final String TEST_STRING = "2021-09-21T13:44:55";
    private static final String TEST_STRING_FRACTIONAL = "2021-09-21T13:44:55.676";
//    private static final String TEST_STRING_UTC = "2021-09-21T13:44:55Z";
//    private static final String TEST_STRING_UTC_FRACTIONAL = "2021-09-21T13:44:55.676Z";

    @Test
    public void testLocalDateTimeParser() throws Exception {
        LocalDateTime a = LocalDateTime.parse(TEST_STRING);

        System.out.println("TS is " + a.toString());
//        LocalDateTimeAdapter.ignoreFractionalSeconds = true;
//        LocalDateTimeAdapter.tolerateSuffixUTC = true;
        LocalDateTimeAdapter adap = new LocalDateTimeAdapter();
        System.out.println("TS0 is " + adap.unmarshal(TEST_STRING));
        System.out.println("TS1 is " + adap.unmarshal(TEST_STRING_FRACTIONAL));
//        System.out.println("TS1 is " + adap.unmarshal(TEST_STRING_UTC));
//        System.out.println("TS2 is " + adap.unmarshal(TEST_STRING_UTC_FRACTIONAL));

//        LocalDateTimeAdapter.addSuffixTimezone = "Z";
//        LocalDateTimeAdapter.outputFractionalSeconds = false;
        System.out.println("TS0 is " + adap.marshal(LocalDateTime.now()));
        System.out.println("TS0 is " + adap.marshal(adap.unmarshal(TEST_STRING)));
        System.out.println("TS0 is " + adap.marshal(adap.unmarshal(TEST_STRING_FRACTIONAL)));
//        System.out.println("TS1 is " + adap.marshal(adap.unmarshal(TEST_STRING_UTC)));
//        System.out.println("TS2 is " + adap.marshal(adap.unmarshal(TEST_STRING_UTC_FRACTIONAL)));

        BigDecimal x = BigDecimal.valueOf(17).setScale(6);
        System.out.println("x is " + x.toString() + ", plain=" + x.toPlainString());

    }

//    @Test
//    public void testLocalDateTimeParserUTC() throws Exception {
//        LocalDateTime a = LocalDateTime.parse(TEST_STRING_UTC);
//
//        System.out.println("TS is " + a.toString());
//    }

    @Test
    public void testLocalDateTimeFormatterNow() throws Exception {
        LocalDateTime a = LocalDateTime.now();
        Instant i = Instant.now();

        System.out.println("TS is " + a.toString());
        System.out.println("Instant is " + i.toString());
    }
}
