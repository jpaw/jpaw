package de.jpaw.batch.api;


/** API of file (or stream) based inputs */
public interface BatchFileReader<E> extends BatchReader<E> {

    String getFilename();       // provides the source filename
    int getSkip();              // provides the number of lines to skip
    int getMaxRecords();        // provides the maximum number of lines to process;
    String getEncoding();       // provides the configured input file encoding;
}
