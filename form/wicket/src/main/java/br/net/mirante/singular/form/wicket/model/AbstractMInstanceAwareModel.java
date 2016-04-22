package br.net.mirante.singular.form.wicket.model;

public abstract class AbstractMInstanceAwareModel<T> implements IMInstanciaAwareModel<T> {

    private static final long serialVersionUID = -3298808175720009389L;

    @Override
    public void setObject(T object) {
    }

    @Override
    public void detach() {
    }

}