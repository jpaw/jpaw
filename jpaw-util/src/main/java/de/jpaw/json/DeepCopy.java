package de.jpaw.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DeepCopy {

    // This method is intended for use of Json objects, i.e. it makes assumptions that elements are either Set, List, Map or immutable components.
    // arrays of primitives are currently not supported
    public static <T> T deepCopy(T obj) {
        if (obj instanceof List<?>) {
            List<Object> l = (List<Object>)obj;
            List<Object> newL = new ArrayList<Object>(l.size());
            for (Object e: l)
                newL.add(deepCopy(e));
            return (T)newL;
        }
        if (obj instanceof Set<?>) {
            Set<Object> l = (Set<Object>)obj;
            Set<Object> newL = new HashSet<Object>(l.size());
            for (Object e: l)
                newL.add(deepCopy(e));
            return (T)newL;
        }
        if (obj instanceof Map<?,?>) {
            Map<String,Object> l = (Map<String,Object>)obj;
            Map<String,Object> newL = new HashMap<String,Object>(l.size());
            for (Map.Entry<String, Object> e: l.entrySet())
                newL.put(e.getKey(), deepCopy(e.getValue()));
            return (T)newL;
        }

        return obj;
    }
}
