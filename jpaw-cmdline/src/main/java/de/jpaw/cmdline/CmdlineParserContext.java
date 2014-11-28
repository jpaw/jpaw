package de.jpaw.cmdline;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Parameter;
import com.martiansoftware.jsap.SimpleJSAP;
import com.martiansoftware.jsap.StringParser;
import com.martiansoftware.jsap.Switch;

/** Encapsulates the SimpleJSAP class and JSAPResult.
 * This class offers additional functionality such as:
 * 
 *  - Sharing of options (no error if the same option
 * is initialized multiple times, as long as all attributes are the same.
 * 
 *  - Provising a static access point getContext(), to avoid the need to pass around the context in real (non-test) applications.
 *  
 *  - Callback functionality, to invoke methods as soon as the command line has been parsed.
 *
 */
public class CmdlineParserContext {
    private static final Logger LOG = LoggerFactory.getLogger(CmdlineParserContext.class);
    private static volatile CmdlineParserContext lastContext = null;

    private final Integer lock = 636238237;
    private final SimpleJSAP ctx;
    private JSAPResult result = null;
    
    static class ParserAttribs {
        String longOption;
        String defaultValue;
        String help;
        StringParser parser;
    }
    
    private final Map<String,ParserAttribs> options = new HashMap<String,ParserAttribs>(31);
    private final Map<Character,String> shortOptions = new HashMap<Character,String>(31);
    private final Set<CmdlineCallback> registeredInstances = new HashSet<CmdlineCallback>(40);
    
    public CmdlineParserContext(String execName, String help) {
        SimpleJSAP pctx = null;
        try {
            pctx = new SimpleJSAP(execName, help, new Parameter[] {});
        } catch (JSAPException e) {
            LOG.error("Cannot create command line parameters: {}", e);
            System.exit(1);
        }
        ctx = pctx;
        lastContext = this;
    }
    
    private static boolean isSame(String a, String b) {
        return a == null ? b == null : a.equals(b);
    }
    
    public void registerCallback(CmdlineCallback instance) {
        if (instance != null)
            registeredInstances.add(instance);
    }
    
    public CmdlineParserContext addFlaggedOption(String longOption, StringParser parser, String defaultValue, boolean isRequired,
            char shortOption, String help) {
        synchronized (lock) {
            ParserAttribs p = options.get(longOption);
            if (p == null) {
                if (shortOption != JSAP.NO_SHORTFLAG) {
                    // short option must be unique (cannot map to two different long options)
                    if (shortOptions.get(shortOption) != null) {
                        LOG.error("Short option {} used twice, for {} as well as for {}, ignoring second definition",
                                  shortOption, shortOptions.get(shortOption), longOption);
                        shortOption = JSAP.NO_SHORTFLAG;
                    } else {
                        shortOptions.put(shortOption, longOption); 
                    }
                }
                // this is a new option: store it
                try {
                    ctx.registerParameter(new FlaggedOption(longOption, parser, defaultValue, isRequired, shortOption, longOption, help));
                } catch (JSAPException e) {
                    LOG.error("Cannot register option {}, ignoring it, for reason ", longOption, e);
                }
                    
                p = new ParserAttribs();
                p.longOption = longOption;
                p.defaultValue = defaultValue;
                p.help = help;
                p.parser = parser;
                options.put(longOption, p);  // remember it
            } else {
                // if the previous one is identical to this one, silently ignore duplicate definition (it will work anyway), else complain
                if (p.longOption.equals(longOption) && p.help.equals(help) && isSame(p.defaultValue, defaultValue) && p.parser == parser) {
                    
                } else {
                    LOG.error("Contradicting definitions for option {}", longOption);
                }
            }
        }
        return this;
    }

    public CmdlineParserContext addSwitch(String longOption, char shortOption, String help) {
        synchronized (lock) {
            ParserAttribs p = options.get(longOption);
            if (p == null) {
                if (shortOption != JSAP.NO_SHORTFLAG) {
                    // short longoption must be unique (cannot map to two different long options)
                    if (shortOptions.get(shortOption) != null) {
                        LOG.error("Short option {} used twice, for {} as well as for {}, ignoring second definition", shortOption,
                                shortOptions.get(shortOption), longOption);
                        shortOption = JSAP.NO_SHORTFLAG;
                    } else {
                        shortOptions.put(shortOption, longOption);
                    }
                }
                // this is a new option: store it
                try {
                    ctx.registerParameter(new Switch(longOption, shortOption, longOption, help));
                } catch (JSAPException e) {
                    LOG.error("Cannot register option {}, ignoring it, for reason ", longOption, e);
                }
                p = new ParserAttribs();
                p.longOption = longOption;
                p.defaultValue = "--FALSE--";
                p.help = help;
                p.parser = null;
                options.put(longOption, p); // remember it
            } else {
                // if the previous one is identical to this one, silently ignore duplicate definition (it will work anyway), else complain
                if (p.longOption.equals(longOption) && p.help.equals(help) && isSame(p.defaultValue, "--FALSE--") && p.parser == null) {

                } else {
                    LOG.error("Contradicting definitions for switch {}", longOption);
                }
            }
        }
        return this;
    }
    
    private void callbackInvocations() {
        for (CmdlineCallback e : registeredInstances)
            e.readParameters(this);
    }

    public void parse(String ... args) throws Exception {
        result = ctx.parse(args);
        if (ctx.messagePrinted()) {
            throw new Exception("Command line parsing error");
        }
        callbackInvocations();
    }
    public void parseOrQuit(String ... args) {
        try {
            result = ctx.parse(args);
        } catch (Exception e) {
            System.err.println("(use option --help for usage)");
            System.exit(1);
        }
        callbackInvocations();
    }

    // Lots of wrappers to get results, required because the JSQPResult is not published.
    public boolean getBoolean(String longOption) {
        return result.getBoolean(longOption);
    }
    
    public String getString(String longOption) {
        return result.getString(longOption);
    }
    
    public int getInt(String longOption) {
        return result.getInt(longOption);
    }
    
    public URL getURL(String longOption) {
        return result.getURL(longOption);
    }
    
    public InetAddress getInetAddress(String longOption) {
        return result.getInetAddress(longOption);
    }
    
    public BigDecimal getBigDecimal(String longOption) {
        return result.getBigDecimal(longOption);
    }
    
    public Date getDate(String longOption) {
        return result.getDate(longOption);
    }
    
    public Class<?> getClass(String longOption) {
        return result.getClass(longOption);
    }
    
    public Package getPackage(String longOption) {
        return result.getPackage(longOption);
    }
    
    // static API: for convenience, to avoid passing around the context
    public static CmdlineParserContext getContext() {
        return lastContext;
    }
    
}

