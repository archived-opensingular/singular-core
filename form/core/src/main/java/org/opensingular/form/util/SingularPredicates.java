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

package org.opensingular.form.util;


import org.opensingular.form.SInstance;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeSimple;
import org.opensingular.form.SIComposite;
import org.opensingular.form.util.transformer.Value;
import org.opensingular.form.SISimple;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class SingularPredicates {

    @SafeVarargs
    public static Predicate<SInstance> allMatches(Predicate<SInstance>... predicates) {
        return i -> Arrays.asList(predicates).stream().allMatch(p -> p.test(i));
    }

    @SafeVarargs
    public static Predicate<SInstance> anyMatches(Predicate<SInstance>... predicates) {
        return i -> Arrays.asList(predicates).stream().anyMatch(p -> p.test(i));
    }

    @SafeVarargs
    public static Predicate<SInstance> noneMatches(Predicate<SInstance>... predicates) {
        return i -> Arrays.asList(predicates).stream().noneMatch(p -> p.test(i));
    }

    public static <T extends Serializable> Predicate<SInstance> typeValueMatches(STypeSimple<? extends SISimple<T>, T> type, Predicate<T> predicate) {
        return si -> predicate.test(Value.of(si, type));
    }

    public static Predicate<SInstance> typeValueIsNotNull(STypeComposite<SIComposite> type) {
        return si -> Value.notNull(si, type);
    }

    public static <T extends Serializable> Predicate<SInstance> typeValueIsNotNull(STypeSimple<? extends SISimple<T>, T> type) {
        return si -> Value.notNull(si, type);
    }

    public static Predicate<SInstance> typeValueIsNull(STypeComposite<SIComposite> type) {
        return si -> !Value.notNull(si, type);
    }

    public static <T extends Serializable> Predicate<SInstance> typeValueIsNull(STypeSimple<? extends SISimple<T>, T> type) {
        return si -> !Value.notNull(si, type);
    }

    public static <T extends Serializable> Predicate<SInstance> typeValueIsNotIn(STypeSimple<? extends SISimple<T>, T> type, List<T> vals) {
        return i -> vals.stream().filter(v -> v.equals(Value.of(i, type))).count() == 0;
    }

    @SafeVarargs
    public static <T extends Serializable> Predicate<SInstance> typeValueIsNotIn(STypeSimple<? extends SISimple<T>, T> type, T... vals) {
        return i -> Arrays.stream(vals).filter(v -> v.equals(Value.of(i, type))).count() == 0;
    }

    @SafeVarargs
    public static <T extends Serializable> Predicate<SInstance> typeValueIsIn(STypeSimple<? extends SISimple<T>, T> type, T... vals) {
        return i -> Arrays.stream(vals).filter(v -> v.equals(Value.of(i, type))).count() > 0;
    }

    public static <T extends Serializable> Predicate<SInstance> typeValueIsIn(STypeSimple<? extends SISimple<T>, T> type, List<T> vals) {
        return i -> vals.stream().filter(v -> v.equals(Value.of(i, type))).count() > 0;
    }

    public static <T extends Serializable> Predicate<SInstance> typeValueIsEqualsTo(STypeSimple<? extends SISimple<T>, T> type, T val) {
        return i -> val.equals(Value.of(i, type));
    }

    public static <T extends Serializable> Predicate<SInstance> typeValueIsNotEqualsTo(STypeSimple<? extends SISimple<T>, T> type, T val) {
        return i -> !val.equals(Value.of(i, type));
    }

    public static Predicate<SInstance> typeValueIsTrue(STypeSimple<? extends SISimple<Boolean>, Boolean> type) {
        return i -> Boolean.TRUE.equals(Value.of(i, type));
    }

    public static Predicate<SInstance> typeValueIsFalse(STypeSimple<? extends SISimple<Boolean>, Boolean> type) {
        return i -> Boolean.FALSE.equals(Value.of(i, type));
    }

}