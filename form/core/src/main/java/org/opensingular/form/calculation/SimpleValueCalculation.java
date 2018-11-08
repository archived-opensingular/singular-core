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

package org.opensingular.form.calculation;

import org.opensingular.form.view.SView;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Predicate;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

/**
 * Represents a calculation of value that works in context of {@link CalculationContext}, which always have a reference
 * to a {@link org.opensingular.form.SInstance} for which the calcualtion will be executed.
 */
@FunctionalInterface
public interface SimpleValueCalculation<RESULT> {

    @Deprecated
    SimpleValueCalculation<?> NULL_RESULT = c -> null;

    @Nullable
    RESULT calculate(@Nonnull CalculationContext context);

    /** Creates a calculation that always returns null. */
    @Nonnull
    static <RESULT> SimpleValueCalculation<RESULT> nil(@Nonnull Class<RESULT> type) {
        return (SimpleValueCalculation<RESULT>) NULL_RESULT;
    }

    /** Creates a calculation that return the informed value if the condition is true, otherwise returns null. */
    @Nonnull
    static <RESULT> SimpleValueCalculation<RESULT> ifThen(@Nonnull Predicate<CalculationContext> condition,
            @Nullable RESULT val) {
        Objects.requireNonNull(condition);
        return c -> condition.test(c) ? val : null;
    }

    /**
     * Add a new possible result for the current calculation. If the current calculation returns null and the informed
     * predicate is true then returns the value informed.
     */
    @Nonnull
    default SimpleValueCalculation<RESULT> appendOn(Predicate<CalculationContext> condition, @Nullable RESULT val) {
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
    default SimpleValueCalculation<RESULT> prependOn(@Nonnull Predicate<CalculationContext> condition,
            @Nullable RESULT val) {
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
    default SimpleValueCalculation<RESULT> appendOnView(Class<? extends SView> viewClass, @Nullable RESULT val) {
        Objects.requireNonNull(viewClass);
        return appendOn(c -> (c.typeContext().getView() != null) &&
                (viewClass.isAssignableFrom(c.typeContext().getView().getClass())), val);
    }

    /**
     * Adds a new verification to be executed before to the current calculation. If the informed context type uses the
     * indicated {@link SView} then return the the informed value. If not, runs the current calculation.
     */
    @Nonnull
    default SimpleValueCalculation<RESULT> prependOnView(Class<? extends SView> viewClass, @Nullable RESULT val) {
        Objects.requireNonNull(viewClass);
        return prependOn(c -> (c.typeContext().getView() != null) &&
                (viewClass.isAssignableFrom(c.typeContext().getView().getClass())), val);
    }

    /**
     * Creates a new calculation that will return the informed value if the original calculation results in a null
     * value.
     */
    @Nonnull
    default SimpleValueCalculation<RESULT> orElse(@Nullable RESULT val) {
        if (this == NULL_RESULT) {
            return c -> val;
        }
        return c -> defaultIfNull(this.calculate(c), val);
    }
}