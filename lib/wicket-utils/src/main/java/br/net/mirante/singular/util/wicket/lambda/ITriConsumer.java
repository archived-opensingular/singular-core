package br.net.mirante.singular.util.wicket.lambda;

import java.io.Serializable;

public interface ITriConsumer<T, U, V> extends Serializable {

    /**
     * Performs this operation on the given arguments.
     *
     * @param t the first input argument
     * @param u the second input argument
     * @param v the third input argument
     */
    void accept(T t, U u, V v);

    static <T, U, V> ITriConsumer<T, U, V> noop() {
        return (t, u, v) -> {
        };
    }

    static <T, U, V> ITriConsumer<T, U, V> noopIfNull(ITriConsumer<T, U, V> consumer) {
        return (consumer != null) ? consumer : noop();
    }

}
