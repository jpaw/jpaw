package de.jpaw.xenums.init;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.util.ApplicationException;

public class ExceptionInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionInitializer.class);

    public static void initializeExceptionClasses(String packageName) {
        initializeExceptionClasses(ReflectionsPackageCache.get(packageName));
    }

    public static void initializeExceptionClasses(Reflections ... reflections) {
        for (int i = 0; i < reflections.length; ++i) {
            int ctr = 0;
            for (Class<? extends ApplicationException> cls : reflections[i].getSubTypesOf(ApplicationException.class)) {
                try {
                    Class.forName(cls.getCanonicalName()); // initialize the class, to load the error descriptions
                    ++ctr;
                } catch (Exception e) {
                    LOGGER.warn("Cannot initialize application exception class {}: {}", cls.getCanonicalName(), e.getMessage());
                }
            }
            LOGGER.info("Startup: Loaded {} application exception classes", ctr);
        }
    }
}
