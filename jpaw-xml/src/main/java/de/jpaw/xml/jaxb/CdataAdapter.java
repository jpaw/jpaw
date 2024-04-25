package de.jpaw.xml.jaxb;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class CdataAdapter extends XmlAdapter<String, String> {
    private static final String CDATA_PREFIX  = "<![CDATA[";
    private static final String CDATA_SUFFIX  = "]]>";
    private static final int CDATA_PREFIX_LEN = CDATA_PREFIX.length();
    private static final int CDATA_SUFFIX_LEN = CDATA_SUFFIX.length();

    @Override
    public String marshal(final String rawData) throws Exception {
        return CDATA_PREFIX + rawData + CDATA_SUFFIX;
    }

    @Override
    public String unmarshal(String data) throws Exception {
        if (data != null && data.startsWith(CDATA_PREFIX) && data.endsWith(CDATA_SUFFIX)) {
            return data.substring(CDATA_PREFIX_LEN, data.length() - CDATA_SUFFIX_LEN);
        }
        return data;
    }
}
