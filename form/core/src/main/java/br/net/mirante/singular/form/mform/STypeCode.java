package br.net.mirante.singular.form.mform;

import br.net.mirante.singular.form.mform.core.SPackageCore;

@SInfoType(name = "STypeCode", spackage = SPackageCore.class)
public class STypeCode<I extends SICode<V>, V> extends SType<I> {

    private Class<V> codeClass;

    public STypeCode() {}

    public STypeCode(Class<I> instanceClass, Class<V> valueClass) {
        super(instanceClass);
        this.codeClass = valueClass;
    }

    public Class<V> getCodeClass() {
        return codeClass;
    }
    @SuppressWarnings("unchecked")
    @Override
    public <C> C convert(Object valor, Class<C> classeDestino) {
        return (C) valor;
    }
}
