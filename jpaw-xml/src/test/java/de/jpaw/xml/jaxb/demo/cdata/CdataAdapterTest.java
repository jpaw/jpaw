package de.jpaw.xml.jaxb.demo.cdata;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jpaw.xml.jaxb.CdataAdapter;

public class CdataAdapterTest {
    private static final String TEST_STRING = "foobar";
    private static final String TEST_CDATA = "<![CDATA[" + TEST_STRING + "]]>";

    @Test
    public void cdataMarshalTest() throws Exception {
        final CdataAdapter adapter = new CdataAdapter();
        final String marshalled = adapter.marshal(TEST_STRING);
        Assertions.assertEquals(TEST_CDATA, marshalled);
    }

    @Test
    public void cdataUnmarshalTest() throws Exception {
        final CdataAdapter adapter = new CdataAdapter();
        final String unmarshalled = adapter.unmarshal(TEST_CDATA);
        Assertions.assertEquals(TEST_STRING, unmarshalled);
    }
}
