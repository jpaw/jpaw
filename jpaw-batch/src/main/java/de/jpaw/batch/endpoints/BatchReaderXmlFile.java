package de.jpaw.batch.endpoints;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import com.martiansoftware.jsap.JSAPResult;

import de.jpaw.batch.api.BatchMainCallback;
import de.jpaw.batch.api.BatchReader;
import de.jpaw.batch.impl.BatchReaderFile;

public class BatchReaderXmlFile<E> extends BatchReaderFile implements BatchReader<E> {

//    private final JAXBContext context;
    private final Class<E> targetClass;
    private final Unmarshaller u;
    private XMLStreamReader r;
    
    public BatchReaderXmlFile(JAXBContext context, Class<E> targetClass) throws JAXBException {
        this.targetClass = targetClass;
        u = context.createUnmarshaller();
    }

    @Override
    public void produceTo(BatchMainCallback<? super E> whereToPut) throws Exception {
        // use JAXB to unmarshal the element
        System.out.println("Reading XML Stream");
        while (r.getEventType() == XMLStreamConstants.START_ELEMENT) {
            Object elem = u.unmarshal(r);
            if (targetClass.isAssignableFrom(elem.getClass())) {
                whereToPut.scheduleForProcessing((E)elem);
                r.nextTag();
            } else {
                throw new Exception("Record is of type " + elem.getClass().getCanonicalName());
            }
        }
    }
    
    @Override
    public void evalCommandlineParameters(JSAPResult params) throws Exception {
        super.evalCommandlineParameters(params);
        
        // this method is used to set up the parser
        XMLInputFactory f = XMLInputFactory.newInstance();
        r = f.createXMLStreamReader(uncompressedStream);
        r.nextTag();
        r.require(XMLStreamConstants.START_ELEMENT, null, null);
        r.nextTag();

    }
}
