package de.jpaw8.function;

import java.util.Objects;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

/** A mixture of Predicate<E> and IntPredicate, comparable to ObjIntConsumer<E> */
@FunctionalInterface
public interface ObjIntPredicate<T> {
    public boolean test(T obj, int n);

    /**
     * Returns a predicate that represents the logical negation of this
     * predicate.
     *
     * @return a predicate that represents the logical negation of this
     * predicate
     */
    default ObjIntPredicate<T> negate() {
        return (t, n) -> !test(t, n);
    }


    /**
     * Returns a composed predicate that represents a short-circuiting logical
     * AND of this predicate and another.  When evaluating the composed
     * predicate, if this predicate is {@code false}, then the {@code other}
     * predicate is not evaluated.
     *
     * <p>Any exceptions thrown during evaluation of either predicate are relayed
     * to the caller; if evaluation of this predicate throws an exception, the
     * {@code other} predicate will not be evaluated.
     *
     * @param other a predicate that will be logically-ANDed with this
     *              predicate
     * @return a composed predicate that represents the short-circuiting logical
     * AND of this predicate and the {@code other} predicate
     * @throws NullPointerException if other is null
     */
    default ObjIntPredicate<T> and(ObjIntPredicate<? super T> other) {
        Objects.requireNonNull(other);
        return (t, n) -> test(t, n) && other.test(t, n);
    }
    default ObjIntPredicate<T> and(Predicate<? super T> other) {
        Objects.requireNonNull(other);
        return (t, n) -> test(t, n) && other.test(t);
    }
    default ObjIntPredicate<T> and(IntPredicate other) {
        Objects.requireNonNull(other);
        return (t, n) -> test(t, n) && other.test(n);
    }



    /**
     * Returns a composed predicate that represents a short-circuiting logical
     * OR of this predicate and another.  When evaluating the composed
     * predicate, if this predicate is {@code true}, then the {@code other}
     * predicate is not evaluated.
     *
     * <p>Any exceptions thrown during evaluation of either predicate are relayed
     * to the caller; if evaluation of this predicate throws an exception, the
     * {@code other} predicate will not be evaluated.
     *
     * @param other a predicate that will be logically-ORed with this
     *              predicate
     * @return a composed predicate that represents the short-circuiting logical
     * OR of this predicate and the {@code other} predicate
     * @throws NullPointerException if other is null
     */
    default ObjIntPredicate<T> or(ObjIntPredicate<? super T> other) {
        Objects.requireNonNull(other);
        return (t, n) -> test(t, n) || other.test(t, n);
    }
    default ObjIntPredicate<T> or(Predicate<? super T> other) {
        Objects.requireNonNull(other);
        return (t, n) -> test(t, n) || other.test(t);
    }
    default ObjIntPredicate<T> or(IntPredicate other) {
        Objects.requireNonNull(other);
        return (t, n) -> test(t, n) || other.test(n);
    }
}
