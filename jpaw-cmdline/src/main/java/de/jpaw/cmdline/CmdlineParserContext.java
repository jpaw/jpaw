package de.jpaw.cmdline;

import java.util.HashMap;
import java.util.Map;

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
    
    public CmdlineParserContext(String execName, String help) throws JSAPException {
        ctx = new SimpleJSAP(execName, help, new Parameter[0]);
    }
    
    private static String concat(String prefix, String longOption) {
        return prefix == null ? longOption : prefix + longOption;
    }
    private static boolean isSame(String a, String b) {
        return a == null ? b == null : a.equals(b);
    }
    
    public void addFlaggedOption(String prefix, String longOption, StringParser parser, String defaultValue, boolean isRequired,
            char shortOption, String help) throws JSAPException {
        String opt = concat(prefix, longOption);
        synchronized (lock) {
            ParserAttribs p = options.get(opt);
            if (p == null) {
                if (prefix != null) {
                    shortOption = JSAP.NO_SHORTFLAG;
                } else if (shortOption != JSAP.NO_SHORTFLAG) {
                    // short option must be unique (cannot map to two different long options)
                    if (shortOptions.get(shortOption) != null) {
                        LOG.error("Short option {} used twice, for {} as well as for {}, ignoring second definition",
                                  shortOption, shortOptions.get(shortOption), opt);
                        shortOption = JSAP.NO_SHORTFLAG;
                    } else {
                        shortOptions.put(shortOption, opt); 
                    }
                }
                // this is a new option: store it
                ctx.registerParameter(new FlaggedOption(opt, parser, defaultValue, isRequired, shortOption, opt, help));
                p = new ParserAttribs();
                p.longOption = opt;
                p.defaultValue = defaultValue;
                p.help = help;
                p.parser = parser;
                options.put(opt, p);  // remember it
            } else {
                // if the previous one is identical to this one, silently ignore duplicate definition (it will work anyway), else complain
                if (p.longOption.equals(opt) && p.help.equals(help) && isSame(p.defaultValue, defaultValue) && p.parser == parser) {
                    
                } else {
                    LOG.error("Contradicting definitions for option {}", opt);
                }
            }
        }
    }

    public void addSwitch(String prefix, String longOption, char shortOption, String help) throws JSAPException {
        String opt = concat(prefix, longOption);
        synchronized (lock) {
            ParserAttribs p = options.get(opt);
            if (p == null) {
                if (prefix != null) {
                    shortOption = JSAP.NO_SHORTFLAG;
                } else if (shortOption != JSAP.NO_SHORTFLAG) {
                    // short option must be unique (cannot map to two different long options)
                    if (shortOptions.get(shortOption) != null) {
                        LOG.error("Short option {} used twice, for {} as well as for {}, ignoring second definition", shortOption,
                                shortOptions.get(shortOption), opt);
                        shortOption = JSAP.NO_SHORTFLAG;
                    } else {
                        shortOptions.put(shortOption, opt);
                    }
                }
                // this is a new option: store it
                ctx.registerParameter(new Switch(opt, shortOption, opt, help));
                p = new ParserAttribs();
                p.longOption = opt;
                p.defaultValue = "--FALSE--";
                p.help = help;
                p.parser = null;
                options.put(opt, p); // remember it
            } else {
                // if the previous one is identical to this one, silently ignore duplicate definition (it will work anyway), else complain
                if (p.longOption.equals(opt) && p.help.equals(help) && isSame(p.defaultValue, "--FALSE--") && p.parser == null) {

                } else {
                    LOG.error("Contradicting definitions for switch {}", opt);
                }
            }
        }
    }

    public void parse(String ... args) throws Exception {
        result = ctx.parse(args);
        if (ctx.messagePrinted()) {
            throw new Exception("Command line parsing error");
        }
    }

    public boolean getBoolean(String prefix, String longOption) {
        return result.getBoolean(concat(prefix, longOption));
    }
    
    public String getString(String prefix, String longOption) {
        return result.getString(concat(prefix, longOption));
    }
    
    public int getInt(String prefix, String longOption) {
        return result.getInt(concat(prefix, longOption));
    }
    
    
    
    
    // static API: for convenience, to avoid passing around the context
    public static CmdlineParserContext getContext() {
        return lastContext;
    }
    public static void parseCommandLine(String ... args) {
        try {
            lastContext.parse(args);
        } catch (Exception e) {
            System.err.println("(use option --help for usage)");
            System.exit(1);
        }
    }
    
}

