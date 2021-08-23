package de.jpaw8.batch.consumers;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import de.jpaw.cmdline.CmdlineParserContext;
import de.jpaw8.batch.consumers.impl.BatchWriterTextFileAbstract;

public class BatchWriterXmlFile extends BatchWriterTextFileAbstract<Object> {
    private final JAXBContext context;
    private boolean formatted = false;
    private Marshaller m;
    private final CmdlineParserContext ctx;

    public BatchWriterXmlFile(JAXBContext context, String header, String footer) {
        super(header, footer);
        this.context = context;
        ctx = CmdlineParserContext.getContext();
        ctx.addSwitch("formatted", 'F', "write formatted output");
    }

    @Override
    public void open() throws Exception {
        super.open();
        formatted = ctx.getBoolean("formatted");

        m = context.createMarshaller();
        if (formatted)
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.setProperty(Marshaller.JAXB_ENCODING, encoding.toString());
        m.setProperty(Marshaller.JAXB_FRAGMENT, true);
    }

    @Override
    public void store(Object response, int no) {
        // marshall the object and write it to the output
        try {
            m.marshal(response, bufferedWriter);
            if (formatted)
                bufferedWriter.append('\n');
        } catch (Exception e) {
            // sneaky-throw
            throw new RuntimeException(e);
        }
    }

}
