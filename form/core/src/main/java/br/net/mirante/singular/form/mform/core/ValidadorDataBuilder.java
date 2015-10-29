package br.net.mirante.singular.form.mform.core;

import java.time.LocalDate;
import java.time.Period;

import org.apache.commons.lang3.NotImplementedException;

import br.net.mirante.singular.form.validation.IValidatable;
import br.net.mirante.singular.form.validation.IValidator;

public class ValidadorDataBuilder {

    private Period limiteInferior;
    private Period limiteSuperior;

    public ValidadorDataBuilder entre(Period limiteInferior, Period limiteSuperior) {
        this.limiteInferior = limiteInferior;
        this.limiteSuperior = limiteSuperior;
        return this;
    }

    public IValidator<LocalDate> build() {
        return new IValidator<LocalDate>() {
            @Override
            public void validate(IValidatable<LocalDate> validatable) {
                // TODO implementar
                throw new NotImplementedException("TODO implementar");
            }
        };
    }
}
