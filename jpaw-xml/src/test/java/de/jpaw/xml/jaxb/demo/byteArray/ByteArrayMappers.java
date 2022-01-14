package de.jpaw.xml.jaxb.demo.byteArray;

// courtesy of http://blog.bdoughan.com/2010/07/xmladapter-jaxbs-secret-weapon.html

import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.time.LocalDate;
import de.jpaw.util.ByteArray;
import de.jpaw.xml.jaxb.ByteArrayAdapter;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ByteArrayMappers {

    public byte[] array1;
    @XmlJavaTypeAdapter(ByteArrayAdapter.class)
    public ByteArray array2;
    public LocalDate localDate;

}
