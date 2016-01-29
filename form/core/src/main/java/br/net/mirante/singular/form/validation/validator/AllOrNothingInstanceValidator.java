package br.net.mirante.singular.form.validation.validator;

import static java.util.stream.Collectors.*;

import java.util.Set;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.STypeSimples;
import br.net.mirante.singular.form.validation.IInstanceValidatable;
import br.net.mirante.singular.form.validation.IInstanceValidator;

public enum AllOrNothingInstanceValidator implements IInstanceValidator<SIComposite> {
    INSTANCE;
    @Override
    public void validate(IInstanceValidatable<SIComposite> v) {
        Set<Boolean> nullValues = v.getInstance().streamDescendants(false)
            .filter(it -> it.getMTipo() instanceof STypeSimples<?, ?>)
            .map(it -> it.getValor() == null)
            .collect(toSet());
        
        // os campos devem ser todos nulos ou todos preenchidos
        if (nullValues.size() != 1)
            v.error("Endereço incompleto");
    }
}