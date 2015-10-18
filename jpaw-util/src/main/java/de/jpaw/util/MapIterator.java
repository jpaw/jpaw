package de.jpaw.util;

import java.util.Iterator;
import java.util.Map;

/** Provides an iterator which alternately returns the key and the value of a Map.
 * This is useful when parsing JSON data structures.
 *  
 * @author mbi
 *
 */
public class MapIterator<E> implements Iterator<E> {

    private final Iterator<Map.Entry<E, E>> me;
    private boolean nextIsValue = false;
    private E nextValue = null;
    
    public MapIterator(Map<E, E> map) {
        me = map.entrySet().iterator();
    }
    
    @Override
    public boolean hasNext() {
        return nextIsValue || me.hasNext();
    }

    @Override
    public E next() {
        nextIsValue = !nextIsValue;
        if (!nextIsValue)
            return nextValue;
        Map.Entry<E, E> nextPair = me.next();
        nextValue = nextPair.getValue();
        return nextPair.getKey();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
    
}
