/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
