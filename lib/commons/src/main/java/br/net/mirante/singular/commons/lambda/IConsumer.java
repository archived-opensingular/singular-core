/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.commons.lambda;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Consumer;

public interface IConsumer<T> extends Consumer<T>, Serializable {

    static <T> IConsumer<T> noop() {
        return a -> {
        };
    }

    static <T> IConsumer<T> noopIfNull(IConsumer<T> consumer) {
        return (consumer != null) ? consumer : noop();
    }

    default IConsumer<T> andThen(IConsumer<? super T> after) {
        Objects.requireNonNull(after);
        return (T t) -> {
            accept(t);
            after.accept(t);
        };
    }
}
