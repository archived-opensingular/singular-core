package br.net.mirante.singular.form.validation.validator;

import java.util.Set;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.STypeSimple;
import br.net.mirante.singular.form.validation.IInstanceValidatable;
import br.net.mirante.singular.form.validation.IInstanceValidator;
import static java.util.stream.Collectors.toSet;

public enum AllOrNothingInstanceValidator implements IInstanceValidator<SIComposite> {
    INSTANCE;
    @Override
    public void validate(IInstanceValidatable<SIComposite> v) {
        Set<Boolean> nullValues = v.getInstance().streamDescendants(false)
            .filter(it -> it.getType() instanceof STypeSimple<?, ?>)
            .map(it -> it.getValue() == null)
            .collect(toSet());
        
        // os campos devem ser todos nulos ou todos preenchidos
        if (nullValues.size() != 1)
            v.error("Endereço incompleto");
    }
}