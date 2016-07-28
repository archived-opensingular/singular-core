package br.net.mirante.singular.form.util;


import br.net.mirante.singular.form.*;
import br.net.mirante.singular.form.util.transformer.Value;

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

    public static <T> Predicate<SInstance> typeValMatches(STypeSimple<? extends SISimple<T>, T> type, Predicate<T> predicate) {
        return si -> predicate.test(Value.of(si, type));
    }

    public static Predicate<SInstance> typeValIsNotNull(STypeComposite<SIComposite> type) {
        return si -> Value.notNull(si, type);
    }

    public static <T> Predicate<SInstance> typeValIsNotNull(STypeSimple<? extends SISimple<T>, T> type) {
        return si -> Value.notNull(si, type);
    }

    public static Predicate<SInstance> typeValIsNull(STypeComposite<SIComposite> type) {
        return si -> !Value.notNull(si, type);
    }

    public static <T> Predicate<SInstance> typeValIsNull(STypeSimple<? extends SISimple<T>, T> type) {
        return si -> !Value.notNull(si, type);
    }

    public static <T> Predicate<SInstance> typeValIsNotIn(STypeSimple<? extends SISimple<T>, T> type, List<T> vals) {
        return i -> vals.stream().filter(v -> v.equals(Value.of(i, type))).count() == 0;
    }

    @SafeVarargs
    public static <T> Predicate<SInstance> typeValIsNotIn(STypeSimple<? extends SISimple<T>, T> type, T... vals) {
        return i -> Arrays.stream(vals).filter(v -> v.equals(Value.of(i, type))).count() == 0;
    }

    @SafeVarargs
    public static <T> Predicate<SInstance> typeValIsIn(STypeSimple<? extends SISimple<T>, T> type, T... vals) {
        return i -> Arrays.stream(vals).filter(v -> v.equals(Value.of(i, type))).count() > 0;
    }

    public static <T> Predicate<SInstance> typeValIsIn(STypeSimple<? extends SISimple<T>, T> type, List<T> vals) {
        return i -> vals.stream().filter(v -> v.equals(Value.of(i, type))).count() > 0;
    }

    public static <T> Predicate<SInstance> typeValIsEqualsTo(STypeSimple<? extends SISimple<T>, T> type, T val) {
        return i -> val.equals(Value.of(i, type));
    }

    public static <T> Predicate<SInstance> typeValIsNotEqualsTo(STypeSimple<? extends SISimple<T>, T> type, T val) {
        return i -> !val.equals(Value.of(i, type));
    }

    public static Predicate<SInstance> typeValIsTrue(STypeSimple<? extends SISimple<Boolean>, Boolean> type) {
        return i -> Boolean.TRUE.equals(Value.of(i, type));
    }

    public static Predicate<SInstance> typeValIsFalse(STypeSimple<? extends SISimple<Boolean>, Boolean> type) {
        return i -> Boolean.FALSE.equals(Value.of(i, type));
    }

}