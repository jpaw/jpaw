package de.jpaw.batch.integrationtests;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import de.jpaw.batch.endpoints.BatchReaderRepeater;
import de.jpaw.batch.endpoints.BatchReaderXmlFile;
import de.jpaw.batch.endpoints.BatchWriterDevNull;
import de.jpaw.batch.endpoints.BatchWriterTextFile;
import de.jpaw.batch.endpoints.BatchWriterXmlFile;
import de.jpaw.batch.impl.BatchExecutorUnthreaded;
import de.jpaw.batch.processors.BatchProcessorFactoryIdentity;
import de.jpaw.batch.processors.BatchProcessorFactoryToXml;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class TestXmlWriter {
    private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    private static final String HEADER = XML_HEADER + "<Data>\n";
    private static final String FOOTER = "</Data>\n";

    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    private static class DummyClass {
        private final String name;
        private final int value;

        DummyClass() {
            name = null;
            value = 0;
        }
        DummyClass(String name, int value) {
            this.name = name;
            this.value = value;
        }
    }

    @Test
    public void testXmlInWriter() throws Exception {
        DummyClass data = new DummyClass("hello", 42);
        String[] cmdline = { "-n", "3", "-o", "/tmp/data1.xml.gz", "--formatted" };
        JAXBContext context = JAXBContext.newInstance(DummyClass.class);

        new BatchExecutorUnthreaded<DummyClass, DummyClass>().run(
                cmdline,
                new BatchReaderRepeater<DummyClass>(data),
                new BatchWriterXmlFile(context, HEADER, FOOTER),
                new BatchProcessorFactoryIdentity<DummyClass>());

    }

    @Test
    public void testXmlInProcessor() throws Exception {
        DummyClass data = new DummyClass("hello", 42);
        String[] cmdline = { "-n", "3", "-o", "/tmp/data2.xml.gz", "--formatted" };
        JAXBContext context = JAXBContext.newInstance(DummyClass.class);

        new BatchExecutorUnthreaded<Object, String>().run(
                cmdline,
                new BatchReaderRepeater<DummyClass>(data),
                new BatchWriterTextFile(HEADER, FOOTER),
                new BatchProcessorFactoryToXml(context));

    }


    @Test
    public void testXmlReader() throws Exception {
        String[] cmdline = { "-i", "/tmp/data1.xml.gz" };
        JAXBContext context = JAXBContext.newInstance(DummyClass.class);

        new BatchExecutorUnthreaded<DummyClass, DummyClass>().run(
                cmdline,
                new BatchReaderXmlFile<DummyClass>(context, DummyClass.class),
                new BatchWriterDevNull<Object>(),
                new BatchProcessorFactoryIdentity<TestXmlWriter.DummyClass>());

    }

}
