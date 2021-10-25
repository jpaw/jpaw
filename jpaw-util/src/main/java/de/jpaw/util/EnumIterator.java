package de.jpaw.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/** Provides an iterator which processes every instance of an enum.
 *
 * @author mbi
 *
 */
public class EnumIterator<E extends Enum<E>> implements Iterator<E> {

    private final E[] allValues;
    private int pos;

    public EnumIterator(final E[] allValues) {
        this.allValues = allValues;
        pos = 0;
    }

    @Override
    public boolean hasNext() {
        return pos < allValues.length;
    }

    @Override
    public E next() {
        if (pos >= allValues.length)
            throw new NoSuchElementException();
        return allValues[pos++];
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
