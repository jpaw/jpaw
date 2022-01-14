package de.jpaw.xml.jaxb.demo.datetime;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


public class Demo {

    public static void main(String[] args) throws Exception {

        JavaTimeMappers root = new JavaTimeMappers();
        root.setLocalDate(LocalDate.of(2011, 5, 30));
        root.setLocalTime(LocalTime.of(11, 2, 30));
        root.setLocalDateTime(LocalDateTime.of(2011, 5, 30, 11, 2, 30));

        JAXBContext jc = JAXBContext.newInstance(JavaTimeMappers.class);

        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(root, System.out);
    }
}
