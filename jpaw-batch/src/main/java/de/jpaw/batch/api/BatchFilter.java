package de.jpaw.batch.api;

/** BatchFilter is Java 8 <code>Predicate</code> plus Contributor. */
public interface BatchFilter<E> extends Contributor {
    public boolean test(E data);

}
