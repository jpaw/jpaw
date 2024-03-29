package de.jpaw.xenums.init;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jpaw.enums.AbstractXEnumBase;

public class XenumInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(XenumInitializer.class);

    public static void initializeXenums(final String packageName) {
        initializeXenums(ReflectionsPackageCache.get(packageName));
    }

    public static void initializeXenums(final Reflections ... reflections) {
        for (int i = 0; i < reflections.length; ++i) {
            int ctr = 0;
            for (final Class<? extends AbstractXEnumBase> cls : reflections[i].getSubTypesOf(AbstractXEnumBase.class)) {
                try {
                    cls.getMethod("xenum$MetaData").invoke(null);
                    ++ctr;
                } catch (final Exception e) {
                    LOGGER.warn("Cannot initialize xenum {}: {}", cls.getCanonicalName(), e.getMessage());
                }
            }
            LOGGER.info("Startup: Loaded {} xenum classes", ctr);
        }
    }
}
