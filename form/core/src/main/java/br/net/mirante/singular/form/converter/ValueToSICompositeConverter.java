package br.net.mirante.singular.form.converter;

import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.SingularFormException;

import java.io.Serializable;

@FunctionalInterface
public interface ValueToSICompositeConverter<T extends Serializable> extends SInstanceConverter<T, SInstance> {

    @Override
    default void fillInstance(SInstance ins, T obj) {
        toInstance((SIComposite) ins, obj);
    }

    void toInstance(SIComposite ins, T obj);

    @Override
    default T toObject(SInstance ins) {
        throw new SingularFormException(ValueToSICompositeConverter.class.getName() + " não é capaz de converter para objeto");
    }

}