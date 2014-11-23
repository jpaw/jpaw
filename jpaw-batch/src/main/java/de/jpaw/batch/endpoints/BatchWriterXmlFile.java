package de.jpaw.batch.endpoints;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;

import de.jpaw.batch.api.BatchWriter;
import de.jpaw.batch.impl.BatchWriterTextFileAbstract;

public class BatchWriterXmlFile extends BatchWriterTextFileAbstract implements BatchWriter<Object> {
    private final JAXBContext context;
    private boolean formatted = false;
    private Marshaller m;
    
    public BatchWriterXmlFile(JAXBContext context, String header, String footer) {
        super(header, footer);
        this.context = context;
    }
    
    @Override
    public void apply(int no, Object response) throws Exception {
        // marshall the object and write it to the output
        m.marshal(response, bufferedWriter);
        if (formatted)
            bufferedWriter.append('\n');
    }

    
    @Override
    public void addCommandlineParameters(JSAP params) throws Exception {
        super.addCommandlineParameters(params);
        params.registerParameter(new Switch("formatted", 'F', "formatted", "write formatted output"));
    }
    
    
    @Override
    public void evalCommandlineParameters(JSAPResult params) throws Exception {
        formatted = params.getBoolean("formatted");
        
        // encoding has been clarified. Now technically everything is fine, get the actual file. That will provide the stream uncompressedStream in the superclass
        super.evalCommandlineParameters(params);
       
        m = context.createMarshaller();
        if (formatted)
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.setProperty(Marshaller.JAXB_ENCODING, encoding.toString());
        m.setProperty(Marshaller.JAXB_FRAGMENT, true);
    }

}
