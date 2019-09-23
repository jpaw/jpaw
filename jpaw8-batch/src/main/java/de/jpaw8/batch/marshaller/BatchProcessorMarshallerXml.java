package de.jpaw8.batch.marshaller;

import java.util.function.Function;

import javax.xml.bind.JAXBContext;

import de.jpaw8.batch.api.BatchMarshaller;
import de.jpaw8.batch.functions.Object2Xml;
import de.jpaw8.batch.functions.Xml2Object;

public class BatchProcessorMarshallerXml<T> implements BatchMarshaller<T> {
    private static final String MEDIA_TYPE = "application/xml";
    private final Function <T,String> toXml;
    private final Function <String,T> fromXml;

    /** Command line configurable constructor. */
    public BatchProcessorMarshallerXml(JAXBContext context, Class<T> clazz) {
        toXml = new Object2Xml<T>(context, true);
        fromXml = new Xml2Object<T>(context, clazz, true);
    }
    @Override
    public String getContentType() {
        return MEDIA_TYPE;
    }

    @Override
    public byte[] marshal(T request) throws Exception {
        return toXml.apply(request).getBytes();
    }

    @Override
    public T unmarshal(byte[] response, int length) throws Exception {
        return fromXml.apply(new String(response, 0, length));
    }
}
