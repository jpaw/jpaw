package de.jpaw.batch.impl;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Parameter;
import com.martiansoftware.jsap.SimpleJSAP;

import de.jpaw.batch.api.BatchExecutor;
import de.jpaw.batch.api.BatchProcessorFactory;
import de.jpaw.batch.api.BatchReader;
import de.jpaw.batch.api.BatchWriter;

/** Implements the main thread of batch processing, as well as central components such as worker thread creation and termination,
 * buffer allocation, statistics output and more.
 *
 * A specific application must call the mainSub method with appropriate parameters for input, output and processing.
 */

abstract public class BatchMain<E, F> extends ContributorNoop implements BatchExecutor<E,F> {
    private static final Logger LOG = LoggerFactory.getLogger(BatchMain.class);
    
    // some statistics data
    protected Date programStart;
    protected Date programEnd;
    protected Date parsingStart;
    protected Date parsingEnd;
    
    private static double recPerSec(long timeInMillis, int numRecords) {
        return timeInMillis == 0 ? 0.0 : 1000.0 * numRecords / timeInMillis;
    }
    
    public void run(String [] args,
            BatchReader<? extends E> reader,
            BatchWriter<? super F> writer,
            BatchProcessorFactory<E,F> processorFactory) throws Exception {

        BatchExecutor<E,F> executor = this;             // extensions of this class should implement the engine
        programStart = new Date();
        // add the main command line parameters
        SimpleJSAP commandLineOptions = null;
        try {
            commandLineOptions = new SimpleJSAP("Bonaparte batch processor", "Runs batched tasks with multithreading", new Parameter[] {});
        } catch (JSAPException e) {
            LOG.error("Cannot create command line parameters: {}", e);
            System.exit(1);
        }
        // add input / output related options
        reader.addCommandlineParameters(commandLineOptions);
        writer.addCommandlineParameters(commandLineOptions);
        processorFactory.addCommandlineParameters(commandLineOptions);
        executor.addCommandlineParameters(commandLineOptions);
        
        JSAPResult params = commandLineOptions.parse(args);
        if (commandLineOptions.messagePrinted()) {
            System.err.println("(use option --help for usage)");
            System.exit(1);
        }
        
        reader.evalCommandlineParameters(params);
        writer.evalCommandlineParameters(params);
        processorFactory.evalCommandlineParameters(params);
        executor.evalCommandlineParameters(params);
        
        executor.open(processorFactory, writer);
        
        parsingStart = new Date();
        LOG.info("{}, Bonaparte batch: Starting to parse", parsingStart);
        
        reader.produceTo(executor);
        
        parsingEnd = new Date();
        long timediffInMillis = parsingEnd.getTime() - parsingStart.getTime();
        int numRecords = executor.getNumberOfRecordsTotal();
        int numExceptions = executor.getNumberOfRecordsException();
        LOG.info("{}, Bonaparte batch: read {} records, total time = {} ms, {} records per second",
                parsingEnd, numRecords, timediffInMillis, recPerSec(timediffInMillis, numRecords));
        
        executor.close();
        
        // We are done. Close the inputs and outputs
        reader.close();             // nothing should happen here...
        writer.close();             // flush and close output files.
        processorFactory.close();   // close remote connections
        
        programEnd = new Date();
        timediffInMillis = programEnd.getTime() - programStart.getTime();
        LOG.info("{}, Bonaparte batch: processed {} records, total time = {} ms, {} records per second, {} exceptions",
                programEnd,numRecords, timediffInMillis, recPerSec(timediffInMillis, numRecords), numExceptions);
    }
}
