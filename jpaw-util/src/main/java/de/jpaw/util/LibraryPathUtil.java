package de.jpaw.util;

import java.lang.reflect.Field;

public class LibraryPathUtil {

    // workaround caching required. See http://blog.cedarsoft.com/2010/11/setting-java-library-path-programmatically/
    public static void setSystemLibraryPath(String path) {
        System.out.println("Setting library path to " + path);
        System.setProperty( "java.library.path", path);

        Field fieldSysPath;
        try {
            fieldSysPath = ClassLoader.class.getDeclaredField( "sys_paths" );
            fieldSysPath.setAccessible( true );
            fieldSysPath.set( null, null );
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setDefaultSystemLibraryPath() {
        String path = System.getProperty("user.home") + "/lib";
        setSystemLibraryPath(path);
    }
}
