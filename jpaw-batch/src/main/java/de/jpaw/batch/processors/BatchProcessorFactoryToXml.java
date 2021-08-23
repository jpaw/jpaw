package de.jpaw.batch.processors;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;

import de.jpaw.batch.api.BatchProcessor;
import de.jpaw.batch.api.BatchProcessorFactory;
import de.jpaw.batch.impl.ContributorNoop;

/** Processor to convert an object into its String representation. */
public class BatchProcessorFactoryToXml extends ContributorNoop implements BatchProcessorFactory<Object,String> {
    private final JAXBContext context;
    private boolean formatted = false;

    public BatchProcessorFactoryToXml(JAXBContext context) throws JAXBException {
        this.context = context;
    }

    @Override
    public void addCommandlineParameters(JSAP params) throws Exception {
        params.registerParameter(new Switch("formatted", 'F', "formatted", "write formatted output"));
    }

    @Override
    public void evalCommandlineParameters(JSAPResult params) throws Exception {
        formatted = params.getBoolean("formatted");
    }


    @Override
    public BatchProcessor<Object,String> getProcessor(int threadNo) throws Exception {
        // create a new Marshaller and hand it to the Processor
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FRAGMENT, true);
        if (formatted)
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        return new BatchProcessorToXml(m, formatted);
    }

    static private class BatchProcessorToXml implements BatchProcessor<Object,String> {
        private final Marshaller m;
        private final boolean formatted;

        private BatchProcessorToXml(Marshaller m, boolean formatted) {
            this.m = m;
            this.formatted = formatted;
        }

        @Override
        public String process(int recordNo, Object data) throws Exception {
            StringWriter w = new StringWriter(1000);
            m.marshal(data, w);
            if (formatted)
                w.write('\n');
            return w.toString();
        }

        @Override
        public void close() throws Exception {
        }
    }
}
