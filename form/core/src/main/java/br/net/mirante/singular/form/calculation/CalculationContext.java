package br.net.mirante.singular.form.calculation;

import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.SingularFormException;

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
