package br.net.mirante.singular.util.wicket.lambda;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.collections.ComparatorUtils;
import org.apache.wicket.model.IModel;

public interface ILambdasMixin {

    @SuppressWarnings("unchecked")
    default <T, R> IFunction<T, R> castOrNull(Class<R> resultType) {
        return it -> (it != null && resultType.isAssignableFrom(it.getClass())) ? (R) it : null;
    }

    default <T> IFunction<T, Stream<T>> nonNulls() {
        return it -> (it != null) ? Stream.of(it) : Stream.empty();
    }

    @SuppressWarnings("unchecked")
    default <T, R> IFunction<? super T, Stream<R>> instancesOf(Class<? super R> resultType) {
        return it -> (it != null && resultType.isAssignableFrom(it.getClass())) ? Stream.of((R) it) : Stream.empty();
    }

    @SuppressWarnings("unchecked")
    default <T, R> IFunction<? super T, Optional<R>> instanceOf(Class<? super R> resultType) {
        return it -> (it != null && resultType.isAssignableFrom(it.getClass())) ? Optional.of((R) it) : Optional.empty();
    }

    default <T> IPredicate<? super T> is(Class<?> type) {
        return t -> (t != null) && type.isAssignableFrom(t.getClass());
    }

    default <T, V> IPredicate<? super T> eq(V value, IFunction<T, V> function) {
        return it -> Objects.equals(value, function.apply(it));
    }

    default <T, V> IPredicate<? super T> eq(V value) {
        return it -> Objects.equals(value, it);
    }

    default <T, V> IPredicate<? super T> ne(V value, IFunction<T, V> function) {
        return it -> !Objects.equals(value, function.apply(it));
    }

    default <T, V> IPredicate<T> ne(V value) {
        return it -> !Objects.equals(value, it);
    }

    default <T> IPredicate<? super T> not(IPredicate<T> predicate) {
        return it -> !predicate.test(it);
    }

    default <T> IPredicate<? super T> notNull() {
        return it -> it != null;
    }

    default <T extends Serializable, R> ISupplier<R> supplyFrom(T input, IFunction<T, R> function) {
        return () -> function.apply(input);
    }

    default <T extends Serializable, R> ISupplier<R> supply(IModel<T> inputModel, IFunction<T, R> function) {
        return () -> function.apply(inputModel.getObject());
    }

    @SuppressWarnings("unchecked")
    default <T, R extends Comparable<R>> Comparator<T> compareWith(IFunction<T, R> func) {
        return (T a, T b) -> ComparatorUtils.nullLowComparator(Comparator.naturalOrder())
            .compare(func.apply(a), func.apply(b));
    }

}
