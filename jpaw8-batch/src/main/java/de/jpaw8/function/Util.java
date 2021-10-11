package de.jpaw8.function;

import java.util.function.Consumer;
import java.util.function.ObjIntConsumer;

public class Util {
    /** Converts a OrdinalConsumer to a Consumer */
    public static <X> ObjIntConsumer<X> asObjIntConsumer(final Consumer<X> c) {
        return (X t, int i) -> c.accept(t);
    }
}
