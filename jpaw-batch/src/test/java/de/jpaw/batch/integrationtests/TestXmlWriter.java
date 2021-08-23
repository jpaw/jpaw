package de.jpaw.batch.integrationtests;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import de.jpaw.batch.endpoints.BatchReaderRepeater;
import de.jpaw.batch.endpoints.BatchReaderXmlFile;
import de.jpaw.batch.endpoints.BatchWriterDevNull;
import de.jpaw.batch.endpoints.BatchWriterTextFile;
import de.jpaw.batch.endpoints.BatchWriterXmlFile;
import de.jpaw.batch.impl.BatchExecutorUnthreaded;
import de.jpaw.batch.processors.BatchProcessorFactoryIdentity;
import de.jpaw.batch.processors.BatchProcessorFactoryToXml;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestXmlWriter {
    static private final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    static private final String HEADER = XML_HEADER + "<Data>\n";
    static private final String FOOTER = "</Data>\n";

    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    static class DummyClass {
        String name;
        int value;

        DummyClass() {
        }
        DummyClass(String name, int value) {
            this.name = name;
            this.value = value;
        }
    }

    @Test
    public void testXmlInWriter() throws Exception {
        DummyClass data = new DummyClass("hello", 42);
        String [] cmdline = { "-n", "3", "-o", "/tmp/data1.xml.gz", "--formatted" };
        JAXBContext context = JAXBContext.newInstance(DummyClass.class);

        new BatchExecutorUnthreaded<DummyClass,DummyClass>().run(
                cmdline,
                new BatchReaderRepeater<DummyClass>(data),
                new BatchWriterXmlFile(context, HEADER, FOOTER),
                new BatchProcessorFactoryIdentity<DummyClass>());

    }

    @Test
    public void testXmlInProcessor() throws Exception {
        DummyClass data = new DummyClass("hello", 42);
        String [] cmdline = { "-n", "3", "-o", "/tmp/data2.xml.gz", "--formatted" };
        JAXBContext context = JAXBContext.newInstance(DummyClass.class);

        new BatchExecutorUnthreaded<Object,String>().run(
                cmdline,
                new BatchReaderRepeater<DummyClass>(data),
                new BatchWriterTextFile(HEADER, FOOTER),
                new BatchProcessorFactoryToXml(context));

    }


    @Test
    public void testXmlReader() throws Exception {
        String [] cmdline = { "-i", "/tmp/data1.xml.gz" };
        JAXBContext context = JAXBContext.newInstance(DummyClass.class);

        new BatchExecutorUnthreaded<DummyClass,DummyClass>().run(
                cmdline,
                new BatchReaderXmlFile<DummyClass>(context, DummyClass.class),
                new BatchWriterDevNull<Object>(),
                new BatchProcessorFactoryIdentity<TestXmlWriter.DummyClass>());

    }

}
