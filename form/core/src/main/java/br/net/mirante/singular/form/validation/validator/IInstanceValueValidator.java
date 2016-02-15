package br.net.mirante.singular.form.validation.validator;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.validation.IInstanceValidatable;
import br.net.mirante.singular.form.validation.IInstanceValidator;

public interface IInstanceValueValidator<I extends SInstance, V> extends IInstanceValidator<I> {

    @Override
    @SuppressWarnings("unchecked")
    default void validate(IInstanceValidatable<I> validatable) {
        V value = (V) validatable.getInstance().getValue();
        if (value == null)
            return;
        validate(validatable, value);
    }

    void validate(IInstanceValidatable<I> validatable, V value);
}
