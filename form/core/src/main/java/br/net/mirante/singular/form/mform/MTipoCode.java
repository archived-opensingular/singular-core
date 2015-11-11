package br.net.mirante.singular.form.mform;

import br.net.mirante.singular.form.mform.core.MPacoteCore;

@MInfoTipo(nome = "MTipoCode", pacote = MPacoteCore.class)
public class MTipoCode<I extends MICode<V>, V> extends MTipo<I> {

    private Class<V> codeClass;

    public MTipoCode() {}

    @SuppressWarnings("unchecked")
    public MTipoCode(Class<I> instanceClass, Class<V> valueClass) {
        super((Class<? extends I>) MICode.class);
        this.codeClass = valueClass;
    }

    public Class<V> getCodeClass() {
        return codeClass;
    }
    @SuppressWarnings("unchecked")
    @Override
    public <C> C converter(Object valor, Class<C> classeDestino) {
        return (C) valor;
    }
}
