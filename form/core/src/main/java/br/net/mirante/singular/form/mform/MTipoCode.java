package br.net.mirante.singular.form.mform;

import br.net.mirante.singular.form.mform.core.MPacoteCore;

@MInfoTipo(nome = "MTipoCode", pacote = MPacoteCore.class)
public class MTipoCode<T> extends MTipo<MICode<T>> {

    private Class<T> codeClass;

    public MTipoCode() {}

    @SuppressWarnings("unchecked")
    public MTipoCode(Class<T> codeClass) {
        super((Class<? extends MICode<T>>) MICode.class);
        this.codeClass = codeClass;
    }

    public Class<T> getCodeClass() {
        return codeClass;
    }
    @SuppressWarnings("unchecked")
    @Override
    public <C> C converter(Object valor, Class<C> classeDestino) {
        return (C) valor;
    }
}
