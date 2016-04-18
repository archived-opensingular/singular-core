package br.net.mirante.singular.form.mform.converter;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SingularFormException;

@FunctionalInterface
public interface ValueToSInstanceConverter<T> extends SInstanceConverter<T> {

    @Override
    default T toObject(SInstance ins) {
        throw new SingularFormException(ValueToSInstanceConverter.class.getName() + " não é capaz de converter para objeto");
    }

}