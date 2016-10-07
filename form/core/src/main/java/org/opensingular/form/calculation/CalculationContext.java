package org.opensingular.form.calculation;

import org.opensingular.form.SInstance;
import org.opensingular.form.SingularFormException;

import java.util.Objects;

public class CalculationContext {

    private SInstance instanceContext;

    public CalculationContext(SInstance instanceContext) {
        this.instanceContext = Objects.requireNonNull(instanceContext);
    }

    public SInstance instance() {
        if (instanceContext == null) {
            throw new SingularFormException("Esse contexto não é baseado em instância");
        }
        return instanceContext;
    }
}
