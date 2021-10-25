package de.jpaw.util;

import java.lang.reflect.Field;

public final class LibraryPathUtil {

    private LibraryPathUtil() { }

    // workaround caching required. See http://blog.cedarsoft.com/2010/11/setting-java-library-path-programmatically/
    public static void setSystemLibraryPath(final String path) {
        System.out.println("Setting library path to " + path);
        System.setProperty("java.library.path", path);

        Field fieldSysPath;
        try {
            fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
            fieldSysPath.setAccessible(true);
            fieldSysPath.set(null, null);
        } catch (final NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (final SecurityException e) {
            throw new RuntimeException(e);
        } catch (final IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (final IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setDefaultSystemLibraryPath() {
        final String path = System.getProperty("user.home") + "/lib";
        setSystemLibraryPath(path);
    }
}
