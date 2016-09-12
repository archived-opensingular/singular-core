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

    @SafeVarargs
    public static Predicate<SInstance> noneMatches(Predicate<SInstance>... predicates) {
        return i -> Arrays.asList(predicates).stream().noneMatch(p -> p.test(i));
    }

    public static <T> Predicate<SInstance> typeValueMatches(STypeSimple<? extends SISimple<T>, T> type, Predicate<T> predicate) {
        return si -> predicate.test(Value.of(si, type));
    }

    public static Predicate<SInstance> typeValueIsNotNull(STypeComposite<SIComposite> type) {
        return si -> Value.notNull(si, type);
    }

    public static <T> Predicate<SInstance> typeValueIsNotNull(STypeSimple<? extends SISimple<T>, T> type) {
        return si -> Value.notNull(si, type);
    }

    public static Predicate<SInstance> typeValueIsNull(STypeComposite<SIComposite> type) {
        return si -> !Value.notNull(si, type);
    }

    public static <T> Predicate<SInstance> typeValueIsNull(STypeSimple<? extends SISimple<T>, T> type) {
        return si -> !Value.notNull(si, type);
    }

    public static <T> Predicate<SInstance> typeValueIsNotIn(STypeSimple<? extends SISimple<T>, T> type, List<T> vals) {
        return i -> vals.stream().filter(v -> v.equals(Value.of(i, type))).count() == 0;
    }

    @SafeVarargs
    public static <T> Predicate<SInstance> typeValueIsNotIn(STypeSimple<? extends SISimple<T>, T> type, T... vals) {
        return i -> Arrays.stream(vals).filter(v -> v.equals(Value.of(i, type))).count() == 0;
    }

    @SafeVarargs
    public static <T> Predicate<SInstance> typeValueIsIn(STypeSimple<? extends SISimple<T>, T> type, T... vals) {
        return i -> Arrays.stream(vals).filter(v -> v.equals(Value.of(i, type))).count() > 0;
    }

    public static <T> Predicate<SInstance> typeValueIsIn(STypeSimple<? extends SISimple<T>, T> type, List<T> vals) {
        return i -> vals.stream().filter(v -> v.equals(Value.of(i, type))).count() > 0;
    }

    public static <T> Predicate<SInstance> typeValueIsEqualsTo(STypeSimple<? extends SISimple<T>, T> type, T val) {
        return i -> val.equals(Value.of(i, type));
    }

    public static <T> Predicate<SInstance> typeValueIsNotEqualsTo(STypeSimple<? extends SISimple<T>, T> type, T val) {
        return i -> !val.equals(Value.of(i, type));
    }

    public static Predicate<SInstance> typeValueIsTrue(STypeSimple<? extends SISimple<Boolean>, Boolean> type) {
        return i -> Boolean.TRUE.equals(Value.of(i, type));
    }

    public static Predicate<SInstance> typeValueIsFalse(STypeSimple<? extends SISimple<Boolean>, Boolean> type) {
        return i -> Boolean.FALSE.equals(Value.of(i, type));
    }

}