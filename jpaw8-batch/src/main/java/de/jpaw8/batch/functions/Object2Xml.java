package de.jpaw8.batch.functions;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.martiansoftware.jsap.JSAP;

import de.jpaw.cmdline.CmdlineCallback;
import de.jpaw.cmdline.CmdlineParserContext;
import de.jpaw8.batch.impl.BatchCharsetUtil;


public class Object2Xml<T> implements Function <T,String>, CmdlineCallback {
    private final JAXBContext context;
    private final boolean threadSafe;       // if not (only sequential use), then the marshaller can be reused.
    private boolean formatted = false;
    protected String encoding = StandardCharsets.UTF_8.toString();
    private Marshaller m = null;

    /** Command line configurable constructor. */
    public Object2Xml(JAXBContext context, boolean threadSafe) {
        this.context = context;
        this.threadSafe = threadSafe;
        CmdlineParserContext.getContext()
            .addSwitch("formatted", 'F', "write formatted output")
            .addFlaggedOption("xmlEncoding", JSAP.STRING_PARSER, null, JSAP.NOT_REQUIRED, JSAP.NO_SHORTFLAG, "XML encoding (default is UTF-8, LIST to get a list of available character sets)")
            .registerCallback(this);
    }

    /** Programmatic configuration. */
    public Object2Xml(JAXBContext context, boolean threadSafe, Charset encoding, boolean formatted) {
        this.context = context;
        this.threadSafe = threadSafe;
        this.encoding = encoding.toString();
        this.formatted = formatted;
        if (!threadSafe) {
            m = createMarshaller();
        }
    }

    private Marshaller createMarshaller() {
        Marshaller mm;
        try {
            mm = context.createMarshaller();
            if (formatted)
                mm.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            mm.setProperty(Marshaller.JAXB_ENCODING, encoding);
            mm.setProperty(Marshaller.JAXB_FRAGMENT, true);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
        return mm;
    }

    @Override
    public void readParameters(CmdlineParserContext ctx) {
        encoding = BatchCharsetUtil.charsetFromStringWithHelp(ctx.getString("xmlEncoding")).toString();
        formatted = ctx.getBoolean("formatted");
        if (!threadSafe) {
            m = createMarshaller();
        }
    }

    @Override
    public String apply(T t) {
        try {
            Marshaller mm = threadSafe ? createMarshaller() : m;
            StringWriter w = new StringWriter();
            mm.marshal(t, w);
            if (formatted)
                w.append('\n');
            return w.toString();
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

}
