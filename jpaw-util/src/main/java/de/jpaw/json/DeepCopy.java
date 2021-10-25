package de.jpaw.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class DeepCopy {

    private DeepCopy() { }

    // This method is intended for use of Json objects, i.e. it makes assumptions that elements are either Set, List, Map or immutable components.
    // arrays of primitives are currently not supported
    public static <T> T deepCopy(final T obj) {
        if (obj instanceof List<?>) {
            final List<Object> l = (List<Object>)obj;
            final List<Object> newL = new ArrayList<>(l.size());
            for (final Object e: l) {
                newL.add(deepCopy(e));
            }
            return (T)newL;
        }
        if (obj instanceof Set<?>) {
            final Set<Object> l = (Set<Object>)obj;
            final Set<Object> newL = new HashSet<>(l.size());
            for (final Object e: l) {
                newL.add(deepCopy(e));
            }
            return (T)newL;
        }
        if (obj instanceof Map<?, ?>) {
            final Map<String, Object> l = (Map<String, Object>)obj;
            final Map<String, Object> newL = new HashMap<>(l.size());
            for (final Map.Entry<String, Object> e: l.entrySet()) {
                newL.put(e.getKey(), deepCopy(e.getValue()));
            }
            return (T)newL;
        }

        return obj;
    }
}
