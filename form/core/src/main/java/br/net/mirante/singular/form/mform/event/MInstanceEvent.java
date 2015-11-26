package br.net.mirante.singular.form.mform.event;

import br.net.mirante.singular.form.mform.MInstancia;

public abstract class MInstanceEvent {

    private final MInstancia instance;

    protected MInstanceEvent(MInstancia instance) {
        this.instance = instance;
    }

    public MInstancia getInstance() {
        return instance;
    }

    @Override
    public String toString() {
        return "MInstanceEvent: " + getInstance();
    }
}
