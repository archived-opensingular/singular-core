/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.lib.commons.lambda;

import java.io.Serializable;
import java.util.function.Predicate;

public interface IPredicate<T> extends Predicate<T>, Serializable {

    static <T> IPredicate<T> noneIfNull(IPredicate<T> predicate) {
        return (predicate != null) ? predicate : none();
    }

    static <T> IPredicate<T> allIfNull(IPredicate<T> predicate) {
        return (predicate != null) ? predicate : all();
    }

    static <T> IPredicate<T> all() {
        return t -> true;
    }
    static <T> IPredicate<T> none() {
        return t -> false;
    }
}
