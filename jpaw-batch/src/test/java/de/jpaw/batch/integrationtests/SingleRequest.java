package de.jpaw.batch.integrationtests;


import org.junit.jupiter.api.Test;

import de.jpaw.batch.examples.BatchTextFileCopyMT;
import de.jpaw.batch.examples.BatchTextFileCopyST;

public class SingleRequest {

	@Test
    public void testTmpST() throws Exception {
        BatchTextFileCopyST.main(new String[] { "-i", "/tmp/in", "-o", "/tmp/out" });  // mocked cmdline args
    }

    @Test
    public void testTmpMT() throws Exception {
        BatchTextFileCopyMT.main(new String[] { "-i", "/tmp/in", "-o", "/tmp/out" });  // mocked cmdline args
    }
}
