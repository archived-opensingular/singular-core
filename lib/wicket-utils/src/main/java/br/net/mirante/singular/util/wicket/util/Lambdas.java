package br.net.mirante.singular.util.wicket.util;

import br.net.mirante.singular.util.wicket.lambda.IFunction;
import br.net.mirante.singular.util.wicket.lambda.IPredicate;
import br.net.mirante.singular.util.wicket.lambda.ISupplier;
import org.apache.commons.collections.ComparatorUtils;
import org.apache.wicket.model.IModel;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public enum Lambdas implements IModelsMixin {
    $L;

    @SuppressWarnings("unchecked")
    public <T, R> IFunction<T, R> castOrNull(Class<R> resultType)
    {
        return it -> (it != null && resultType.isAssignableFrom(it.getClass())) ? (R) it : null;
    }

    public <T> IFunction<T, Stream<T>> nonNulls()
    {
        return it -> (it != null) ? Stream.of(it) : Stream.empty();
    }

    @SuppressWarnings("unchecked")
    public <T, R> IFunction<? super T, Stream<R>> instancesOf(Class<R> resultType)
    {
        return it -> (it != null && resultType.isAssignableFrom(it.getClass())) ? Stream.of((R) it) : Stream.empty();
    }

    @SuppressWarnings("unchecked")
    public <T, R> IFunction<? super T, Optional<R>> instanceOf(Class<? super R> resultType)
    {
        return it -> (it != null && resultType.isAssignableFrom(it.getClass())) ? Optional.of((R) it) : Optional.empty();
    }

    public <T> IPredicate<? super T> is(Class<?> type)
    {
        return t -> (t != null) && type.isAssignableFrom(t.getClass());
    }

    public <T, V> IPredicate<? super T> eq(V value, IFunction<T, V> function)
    {
        return it -> Objects.equals(value, function.apply(it));
    }

    public <T, V> IPredicate<? super T> eq(V value)
    {
        return it -> Objects.equals(value, it);
    }

    public <T, V> IPredicate<? super T> ne(V value, IFunction<T, V> function)
    {
        return it -> !Objects.equals(value, function.apply(it));
    }

    public <T, V> IPredicate<T> ne(V value)
    {
        return it -> !Objects.equals(value, it);
    }

    public <T> IPredicate<? super T> not(IPredicate<T> predicate)
    {
        return it -> !predicate.test(it);
    }

    public <T> IPredicate<? super T> notNull()
    {
        return it -> it != null;
    }

    public <T extends Serializable, R> ISupplier<R> supplyFrom(T input, IFunction<T, R> function)
    {
        return new ISupplier<R>()
        {
            @Override
            public R get()
            {
                return function.apply(input);
            }
        };
    }

    public <T extends Serializable, R> ISupplier<R> supply(IModel<T> inputModel, IFunction<T, R> function)
    {
        return new ISupplier<R>()
        {
            @Override
            public R get()
            {
                return function.apply(inputModel.getObject());
            }
        };
    }

    @SuppressWarnings("unchecked")
    public <T, R extends Comparable<R>> Comparator<T> compareWith(IFunction<T, R> func)
    {
        Comparator<R> comparator = ComparatorUtils.nullLowComparator(Comparator.naturalOrder());
        return (T a, T b) -> comparator
            .compare(func.apply(a), func.apply(b));
    }

}
