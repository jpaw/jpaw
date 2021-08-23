package de.jpaw8.batch.producers;

import java.util.function.ObjIntConsumer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import de.jpaw8.batch.producers.impl.BatchReaderFile;

public class BatchReaderXmlFile<E> extends BatchReaderFile<E> {
    private final Class<E> targetClass;
    private final Unmarshaller u;

    public BatchReaderXmlFile(JAXBContext context, Class<E> targetClass) throws JAXBException {
        this.targetClass = targetClass;
        u = context.createUnmarshaller();
    }

    @Override
    public void produceTo(ObjIntConsumer<? super E> whereToPut) throws Exception {
        int n = 0;

        // this method is used to set up the parser
        XMLInputFactory f = XMLInputFactory.newInstance();
        XMLStreamReader r = f.createXMLStreamReader(uncompressedStream);
        r.nextTag();
        r.require(XMLStreamConstants.START_ELEMENT, null, null);
        r.nextTag();

        // use JAXB to unmarshal the element
        System.out.println("Reading XML Stream");
        while (r.getEventType() == XMLStreamConstants.START_ELEMENT) {
            Object elem = u.unmarshal(r);
            if (targetClass.isAssignableFrom(elem.getClass())) {
                whereToPut.accept((E)elem, ++n);
                r.nextTag();
            } else {
                throw new Exception("Record is of type " + elem.getClass().getCanonicalName());
            }
        }
    }
}
