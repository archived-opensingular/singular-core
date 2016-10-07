package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.validators;

import br.net.mirante.singular.form.type.core.SIBigDecimal;
import br.net.mirante.singular.form.type.core.STypeDecimal;
import br.net.mirante.singular.form.util.transformer.Value;
import br.net.mirante.singular.form.validation.IInstanceValidatable;
import br.net.mirante.singular.form.validation.IInstanceValidator;

import java.math.BigDecimal;

public class ResiduoValidator implements IInstanceValidator<SIBigDecimal> {

    private STypeDecimal loq;

    public ResiduoValidator(STypeDecimal loq) {
        this.loq = loq;
    }

    @Override
    public void validate(IInstanceValidatable<SIBigDecimal> validatable) {
        SIBigDecimal instance = validatable.getInstance();
        BigDecimal residuoValue = Value.of(instance);
        BigDecimal loqValue = Value.of(instance, loq);
        if (
                (residuoValue == null || loqValue == null)
                        ||
                (!residuoValue.equals(BigDecimal.ZERO) && residuoValue.compareTo(loqValue) < 0)
                ) {
            validatable.error("Valor deve ser maior que LoQ ou Zero.");
        }
    }
}
