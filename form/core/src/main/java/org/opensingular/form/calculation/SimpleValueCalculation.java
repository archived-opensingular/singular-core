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
