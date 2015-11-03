package br.net.mirante.singular.form.mform.core;

import java.time.LocalDate;
import java.time.Period;

import org.apache.commons.lang3.NotImplementedException;

import br.net.mirante.singular.form.validation.IValueValidatable;
import br.net.mirante.singular.form.validation.IValueValidator;

public class ValidadorDataBuilder {

    private Period limiteInferior;
    private Period limiteSuperior;

    public ValidadorDataBuilder entre(Period limiteInferior, Period limiteSuperior) {
        this.limiteInferior = limiteInferior;
        this.limiteSuperior = limiteSuperior;
        return this;
    }

    public IValueValidator<LocalDate> build() {
        return new IValueValidator<LocalDate>() {
            @Override
            public void validate(IValueValidatable<LocalDate> validatable) {
                // TODO implementar
                throw new NotImplementedException("TODO implementar");
            }
        };
    }
}
