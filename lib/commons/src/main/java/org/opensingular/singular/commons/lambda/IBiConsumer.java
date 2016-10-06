/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.commons.lambda;

import java.io.Serializable;
import java.util.function.BiConsumer;

public interface IBiConsumer<T, U> extends BiConsumer<T, U>, Serializable {

    static <T, U> IBiConsumer<T, U> noop() {
        return (a, b) -> {
        };
    }

    static <T, U> IBiConsumer<T, U> noopIfNull(IBiConsumer<T, U> consumer) {
        return (consumer != null) ? consumer : noop();
    }

}
