package de.jpaw.xenums.init;

import java.util.concurrent.ConcurrentHashMap;

import org.reflections.Reflections;

/** Caches scanned packages, because they might be needed multiple times. */
public final class ReflectionsPackageCache {
    private static final ConcurrentHashMap<String, Reflections> SCANNED_PACKAGES = new ConcurrentHashMap<>();

    private ReflectionsPackageCache() { }

    /** Clears all cached entries. Use this after initialization is complete. */
    public static void clear() {
        SCANNED_PACKAGES.clear();
    }

    public static Reflections get(final String packagename) {
        Reflections r = SCANNED_PACKAGES.get(packagename);
        if (r == null) {
            r = new Reflections(packagename);
            final Reflections r2 = SCANNED_PACKAGES.putIfAbsent(packagename, r);
            if (r2 != null)
                r = r2;  // returns the initial Reflections instance, such that long term only the first instance is used
        }
        return r;
    }

    /** Scan a list of package names and returns the array of Reflections. */
    public static Reflections[] getAll(final String... packagename) {
        final Reflections[] result = new Reflections[packagename.length];
        for (int i = 0; i < packagename.length; ++i) {
            result[i] = get(packagename[i]);
        }
        return result;
    }
}
