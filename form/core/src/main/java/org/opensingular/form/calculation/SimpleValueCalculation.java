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

import static org.apache.commons.lang3.ObjectUtils.*;

import java.util.function.Predicate;

import org.opensingular.form.view.SView;

@FunctionalInterface
public interface SimpleValueCalculation<RESULT> {

    public RESULT calculate(CalculationContext context);

    static <RESULT> SimpleValueCalculation<RESULT> nil(Class<RESULT> type) {
        return c -> null;
    }
    default SimpleValueCalculation<RESULT> appendOn(Predicate<CalculationContext> condition, RESULT val) {
        return c -> {
            RESULT res = this.calculate(c);
            if (res != null)
                return res;
            return (condition.test(c)) ? val : null;
        };
    }
    default SimpleValueCalculation<RESULT> prependOn(Predicate<CalculationContext> condition, RESULT val) {
        return c -> condition.test(c) ? val : this.calculate(c);
    }
    default SimpleValueCalculation<RESULT> appendOnView(Class<? extends SView> viewClass, RESULT val) {
        return appendOn(c -> (c.instance().getType().getView() != null) && (viewClass.isAssignableFrom(c.instance().getType().getView().getClass())), val);
    }
    default SimpleValueCalculation<RESULT> prependOnView(Class<? extends SView> viewClass, RESULT val) {
        return prependOn(c -> (c.instance().getType().getView() != null) && (viewClass.isAssignableFrom(c.instance().getType().getView().getClass())), val);
    }
    default SimpleValueCalculation<RESULT> orElse(RESULT val) {
        return c -> defaultIfNull(this.calculate(c), val);
    }
}
