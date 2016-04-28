package br.net.mirante.singular.form.mform.converter;

import br.net.mirante.singular.form.mform.SInstance;

public class EnumSInstanceConverter<T extends Enum<T>> implements SInstanceConverter<T, SInstance> {

    private final Class<T> enumClass;

    public EnumSInstanceConverter(Class<T> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public void fillInstance(SInstance ins, T obj) {
        ins.setValue(obj.name());
    }

    @Override
    public T toObject(SInstance ins) {
        return Enum.valueOf(enumClass, (String) ins.getValue());
    }

}
