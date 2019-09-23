package de.jpaw8.batch.functions;

import java.io.StringReader;
import java.util.function.Function;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class Xml2Object<T> implements Function <String,T> {
    private final JAXBContext context;
    private final boolean threadSafe;       // if not (only sequential use), then the marshaller can be reused, set threadSafe to false then.
    private final Class<T> baseClass;
    private final Unmarshaller m;

    /** Command line configurable constructor. */
    public Xml2Object(JAXBContext context, Class<T> baseClass, boolean threadSafe) {
        this.context = context;
        this.baseClass = baseClass;
        this.threadSafe = threadSafe;
        m = threadSafe ? null : createUnmarshaller();
    }

    private final Unmarshaller createUnmarshaller() {
        Unmarshaller mm;
        try {
            mm = context.createUnmarshaller();
            mm.setProperty(Marshaller.JAXB_FRAGMENT, true);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
        return mm;
    }

    @Override
    public T apply(String t) {
        try {
            Unmarshaller mm = threadSafe ? createUnmarshaller() : m;
            StringReader r = new StringReader(t);
            Object o = mm.unmarshal(r);
            return baseClass.cast(o);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
