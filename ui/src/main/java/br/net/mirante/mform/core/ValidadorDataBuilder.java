package br.net.mirante.mform.core;

import org.apache.commons.lang.NotImplementedException;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.joda.time.Period;

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
                throw new NotImplementedException();
            }
        };
    }

}
