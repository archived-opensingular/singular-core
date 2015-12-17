package br.net.mirante.singular.form.validation.validator;

import static java.util.stream.Collectors.*;

import java.util.Set;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MTipoSimples;
import br.net.mirante.singular.form.validation.IInstanceValidatable;
import br.net.mirante.singular.form.validation.IInstanceValidator;

public final class AllOrNothingInstanceValidator implements IInstanceValidator<MIComposto> {
    @Override
    public void validate(IInstanceValidatable<MIComposto> v) {
        Set<Boolean> nullValues = v.getInstance().streamDescendants(false)
            .filter(it -> it.getMTipo() instanceof MTipoSimples<?, ?>)
            .map(it -> it.getValor() == null)
            .collect(toSet());
        
        // os campos devem ser todos nulos ou todos preenchidos
        if (nullValues.size() != 1)
            v.error("Endereço incompleto");
    }
}