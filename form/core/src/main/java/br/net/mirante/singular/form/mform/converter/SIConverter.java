package br.net.mirante.singular.form.mform.converter;

import br.net.mirante.singular.form.mform.SInstance;

public class SIConverter<C extends ValueToSInstanceConverter<T>, T> extends SInstance {

    private C converter;

    @Override
    public Object getValue() {
        return converter;
    }

    @Override
    public void clearInstance() {
        converter = null;
    }

    @Override
    public boolean isEmptyOfData() {
        return converter != null;
    }

    @Override
    public void setValue(Object value) {
        this.converter = (C) value;
    }
}
