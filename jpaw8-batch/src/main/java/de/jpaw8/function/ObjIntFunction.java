package de.jpaw8.function;

import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface ObjIntFunction<T, R> {

    /**
     * Applies this function to the given argument.
     *
     * @param t the function argument
     * @return the function result
     */
    R apply(T t, int i);

    /**
     * Returns a composed function that first applies the {@code before}
     * function to its input, and then applies this function to the result.
     * If evaluation of either function throws an exception, it is relayed to
     * the caller of the composed function.
     *
     * @param <V> the type of input to the {@code before} function, and to the
     *           composed function
     * @param before the function to apply before this function is applied
     * @return a composed function that first applies the {@code before}
     * function and then applies this function
     * @throws NullPointerException if before is null
     *
     * @see #andThen(Function)
     */
    default <V> ObjIntFunction<V, R> compose(ObjIntFunction<? super V, ? extends T> before) {
        Objects.requireNonNull(before);
        return (V v, int i) -> apply(before.apply(v, i), i);
    }
    default <V> ObjIntFunction<V, R> compose(Function<? super V, ? extends T> before) {
        Objects.requireNonNull(before);
        return (V v, int i) -> apply(before.apply(v), i);
    }

    /**
     * Returns a composed function that first applies this function to
     * its input, and then applies the {@code after} function to the result.
     * If evaluation of either function throws an exception, it is relayed to
     * the caller of the composed function.
     *
     * @param <V> the type of output of the {@code after} function, and of the
     *           composed function
     * @param after the function to apply after this function is applied
     * @return a composed function that first applies this function and then
     * applies the {@code after} function
     * @throws NullPointerException if after is null
     *
     * @see #compose(Function)
     */
    default <V> ObjIntFunction<T, V> andThen(ObjIntFunction<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (T t, int i) -> after.apply(apply(t, i), i);
    }
    default <V> ObjIntFunction<T, V> andThen(Function<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (T t, int i) -> after.apply(apply(t, i));
    }

    /**
     * Returns a function that always returns its input argument.
     *
     * @param <T> the type of the input and output objects to the function
     * @return a function that always returns its input argument
     */
    static <T> ObjIntFunction<T, T> identity() {
        return (t, i) -> t;
    }

}
