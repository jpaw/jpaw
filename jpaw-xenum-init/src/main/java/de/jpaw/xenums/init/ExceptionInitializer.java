package de.jpaw.xenums.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.util.ApplicationException;

public class ExceptionInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionInitializer.class);
    
    public static void initializeExceptionClasses(String packageName) {
        int ctr = 0;
        for (Class<? extends ApplicationException> cls : ReflectionsPackageCache.get(packageName).getSubTypesOf(ApplicationException.class)) {
            try {
                Class.forName(cls.getCanonicalName());  // initialize the class, to load the error descriptions
                ++ctr;
            } catch (Exception e) {
                LOGGER.warn("Cannot initialize application exception class {}: {}", cls.getCanonicalName(), e.getMessage());
            }
        }
        LOGGER.info("Startup: Loaded {} application exception classes", ctr);
    }
}
