package br.net.mirante.singular.util.wicket.lambda;

import java.io.Serializable;
import java.util.function.Predicate;

public interface IPredicate<T> extends Predicate<T>, Serializable {

    static <T> IPredicate<T> noneIfNull(IPredicate<T> predicate) {
        return (predicate != null) ? predicate : t -> false;
    }

    static <T> IPredicate<T> allIfNull(IPredicate<T> predicate) {
        return (predicate != null) ? predicate : t -> true;
    }

}
