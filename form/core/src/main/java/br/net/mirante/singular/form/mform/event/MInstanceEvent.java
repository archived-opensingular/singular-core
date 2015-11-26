package br.net.mirante.singular.form.mform.event;

import br.net.mirante.singular.form.mform.MInstancia;

public abstract class MInstanceEvent {

    private final MInstancia source;

    protected MInstanceEvent(MInstancia source) {
        this.source = source;
    }

    public MInstancia getSource() {
        return source;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ": " + getSource();
    }
}
