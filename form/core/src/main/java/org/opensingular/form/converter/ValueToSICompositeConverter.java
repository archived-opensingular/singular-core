package org.opensingular.form.converter;

import org.opensingular.form.SInstance;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SingularFormException;

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