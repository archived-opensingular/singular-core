package br.net.mirante.singular.form.mform.converter;

import br.net.mirante.singular.form.mform.SInstance;

public class SimpleSInstanceConverter<T> implements SInstanceConverter<T> {

    @Override
    public void toInstance(SInstance ins, T obj) {
        ins.setValue(obj);
    }

    @Override
    public T toObject(SInstance ins) {
        return (T) ins.getValue();
    }

}
