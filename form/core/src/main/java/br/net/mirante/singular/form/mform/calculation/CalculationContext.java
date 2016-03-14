package br.net.mirante.singular.form.mform.calculation;

import java.util.Objects;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SingularFormException;

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
