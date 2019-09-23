package de.jpaw8.xtend;

import java.util.Collection;
import java.util.function.ToIntFunction;

public class CollectionExtensions {

    /** Returns the collection element which has the maximum weight of all. */
    public static <T> T ofMaxWeight(Collection<T> list, ToIntFunction<? super T> evaluator) {
        int bestWeightSoFar = Integer.MIN_VALUE;
        T best = null;
        for (T e : list) {
            int newWeight = evaluator.applyAsInt(e);
            if (best == null || newWeight > bestWeightSoFar) {
                best = e;
                bestWeightSoFar = newWeight;
            }
        }
        return best;
    }
}
