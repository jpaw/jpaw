package de.jpaw.util.tests;

import java.io.IOException;

import org.testng.annotations.Test;

import de.jpaw.util.ExceptionUtil;

public class TestExceptions {
    static void f1() throws IOException {
        throw new IOException("root cause");
    }
    static void f2() {
        try {
            f1();
        } catch (Exception e) {
            throw new RuntimeException("intermediate cause", e);
        }
    }
    
    @Test
    void exTest() {
        try {
            f2();
        } catch (Exception e) {
            System.out.println("Caught exception: " + ExceptionUtil.causeChain(e));
        }
    }
}
