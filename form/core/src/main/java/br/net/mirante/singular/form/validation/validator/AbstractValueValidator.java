package br.net.mirante.singular.form.validation.validator;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.validation.IInstanceValidatable;
import br.net.mirante.singular.form.validation.IInstanceValidator;

public abstract class AbstractValueValidator<I extends MInstancia, V> implements IInstanceValidator<I> {

    protected AbstractValueValidator() {}

    @Override
    @SuppressWarnings("unchecked")
    public final void validate(IInstanceValidatable<I> validatable) {
        V value = (V) validatable.getInstance().getValor();
        if (value == null)
            return;
        validate(validatable, value);
    }

    public abstract void validate(IInstanceValidatable<I> validatable, V value);
}
