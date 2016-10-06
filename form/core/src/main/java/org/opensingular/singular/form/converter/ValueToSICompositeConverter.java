package org.opensingular.singular.form.converter;

import org.opensingular.singular.form.SIComposite;
import org.opensingular.singular.form.SInstance;
import org.opensingular.singular.form.SingularFormException;

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