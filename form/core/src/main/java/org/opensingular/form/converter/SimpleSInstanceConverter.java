package org.opensingular.form.converter;

import org.opensingular.form.SInstance;

import java.io.Serializable;

public class SimpleSInstanceConverter<T extends Serializable> implements SInstanceConverter<T, SInstance> {

    @Override
    public void fillInstance(SInstance ins, T obj) {
        ins.setValue(obj);
    }

    @Override
    public T toObject(SInstance ins) {
        return (T) ins.getValue();
    }

}
