package de.jpaw.util;

public class ExceptionUtil {
    
    /** Returns a string of the exception with message, and a chain of causes (but not the full stack trace). */
    static public String causeChain(Throwable e) {
        final StringBuilder b = new StringBuilder(1000);
        do {
            b.append(e.getClass().getCanonicalName());
            b.append(": ");
            b.append(e.getMessage());
            e = e.getCause();
            if (e != null)
                b.append("\n... caused by: ");
        } while (e != null);
        return b.toString();
    }
}
