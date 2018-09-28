package org.opensingular.form.calculation;

import org.opensingular.form.view.SView;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Predicate;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

/**
 * Represents a calculation of value that works in context of {@link CalculationContextInstanceOptional}, which may
 * only have a reference to the {@link org.opensingular.form.SType} and some times may have a reference to a {@link
 * org.opensingular.form.SInstance}.
 *
 * @author Daniel C. Bordin
 * @since 2018-09-27
 */
@FunctionalInterface
public interface SimpleValueCalculationInstanceOptional<RESULT> {

    /** Calculates the value for the given context. */
    @Nullable
    RESULT calculate(@Nonnull CalculationContextInstanceOptional context);

    /**
     * Transformns the original calculation into a calculation that will only be executed if there is a {@link
     * org.opensingular.form.SInstance} in context available to be used in the calculation.
     */
    @Nullable
    static <T> SimpleValueCalculationInstanceOptional<T> of(@Nullable SimpleValueCalculation<T> original) {
        if (original == null) {
            return null;
        }
        return context -> context.hasInstanceContext() ? original.calculate(context.asCalculationContext()) : null;
    }

    @Deprecated
    SimpleValueCalculationInstanceOptional<?> NULL_RESULT = c -> null;

    /** Creates a calculation that always returns null. */
    @Nonnull
    static <RESULT> SimpleValueCalculationInstanceOptional<RESULT> nil(@Nonnull Class<RESULT> type) {
        return (SimpleValueCalculationInstanceOptional<RESULT>) NULL_RESULT;
    }

    /** Creates a calculation that return the informed value if the condition is true, otherwise returns null. */
    @Nonnull
    static <RESULT> SimpleValueCalculationInstanceOptional<RESULT> ifThen(
            @Nonnull Predicate<CalculationContextInstanceOptional> condition, @Nullable RESULT val) {
        Objects.requireNonNull(condition);
        return c -> condition.test(c) ? val : null;
    }

    /**
     * Add a new possible result for the current calculation. If the current calculation returns null and the informed
     * predicate is true then returns the value informed.
     */
    @Nonnull
    default SimpleValueCalculationInstanceOptional<RESULT> appendOn(
            Predicate<CalculationContextInstanceOptional> condition, @Nullable RESULT val) {
        Objects.requireNonNull(condition);
        if (this == NULL_RESULT) {
            return ifThen(condition, val);
        }
        return c -> {
            RESULT res = this.calculate(c);
            if (res != null) {
                return res;
            }
            return (condition.test(c)) ? val : null;
        };
    }

    /**
     * Creates a new calculation adding the verification (predicate) to be evaluated before the current calculation. If
     * the predicate is true, returns the value. If false, executes the current calculation.
     */
    @Nonnull
    default SimpleValueCalculationInstanceOptional<RESULT> prependOn(
            @Nonnull Predicate<CalculationContextInstanceOptional> condition, @Nullable RESULT val) {
        Objects.requireNonNull(condition);
        if (this == NULL_RESULT) {
            return ifThen(condition, val);
        }
        return c -> condition.test(c) ? val : this.calculate(c);
    }

   /**
     * Adds a new verification to the end of the current calculation. If the the current calculation results in null,
     * then verifies if the informed context type uses the indicated {@link SView}. If true, return the informed value.
     * Otherwise, returns null.
     */
    @Nonnull
    default SimpleValueCalculationInstanceOptional<RESULT> appendOnView(Class<? extends SView> viewClass,
            @Nullable RESULT val) {
        Objects.requireNonNull(viewClass);
        return appendOn(c -> (c.typeContext().getView() != null) &&
                (viewClass.isAssignableFrom(c.typeContext().getView().getClass())), val);
    }

    /**
     * Adds a new verification to be executed before to the current calculation. If the informed context type uses the
     * indicated {@link SView} then return the the informed value. If not, runs the current calculation.
     */
    @Nonnull
    default SimpleValueCalculationInstanceOptional<RESULT> prependOnView(Class<? extends SView> viewClass,
            @Nullable RESULT val) {
        Objects.requireNonNull(viewClass);
        return prependOn(c -> (c.typeContext().getView() != null) &&
                (viewClass.isAssignableFrom(c.typeContext().getView().getClass())), val);
    }

    /**
     * Creates a new calculation that will return the informed value if the original calculation results in a null
     * value.
     */
    @Nonnull
    default SimpleValueCalculationInstanceOptional<RESULT> orElse(@Nullable RESULT val) {
        if (this == NULL_RESULT) {
            return c -> val;
        }
        return c -> defaultIfNull(this.calculate(c), val);
    }

}
