package br.net.mirante.singular.form.mform.core;

import org.apache.commons.lang3.NotImplementedException;
import org.joda.time.Period;

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

    public IValidator<MIData> build() {
        return new IValidator<MIData>() {
            @Override
            public void validate(IValidatable<MIData> validatable) {
                // TODO implementar
                throw new NotImplementedException("TODO implementar");
            }
        };
    }

}
