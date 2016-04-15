package br.net.mirante.singular.form.wicket.model;

public abstract class BaseIMInstanceAwareModel<T> implements IMInstanciaAwareModel<T> {

    @Override
    public void setObject(T object) {
    }

    @Override
    public void detach() {
    }
}
