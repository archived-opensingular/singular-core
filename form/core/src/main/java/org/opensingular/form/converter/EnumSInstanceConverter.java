package org.opensingular.form.converter;

import org.opensingular.form.SInstance;

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
