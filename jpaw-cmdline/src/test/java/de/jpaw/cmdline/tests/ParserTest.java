package de.jpaw.cmdline.tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.martiansoftware.jsap.JSAP;

import de.jpaw.cmdline.CmdlineParserContext;

public class ParserTest {

    @Test
    public void testParser() throws Exception {

        CmdlineParserContext ctx = new CmdlineParserContext("testNG test", "This is a help text");
        
        ctx.addSwitch("verbose", 'v', "issues more log output");
        ctx.addFlaggedOption("in-encoding", JSAP.STRING_PARSER, "utf-47", JSAP.NOT_REQUIRED, JSAP.NO_SHORTFLAG, "set the input encoding");

        ctx.parse("-v");
        
        Assert.assertEquals(ctx.getBoolean("verbose"), true);
        Assert.assertEquals(ctx.getString("in-encoding"), "utf-47");
    }

}
