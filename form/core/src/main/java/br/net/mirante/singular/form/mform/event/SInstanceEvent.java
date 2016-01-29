package br.net.mirante.singular.form.mform.event;

import br.net.mirante.singular.form.mform.SInstance2;

public abstract class SInstanceEvent {

    private final SInstance2 source;

    protected SInstanceEvent(SInstance2 source) {
        this.source = source;
    }

    public SInstance2 getSource() {
        return source;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ": " + getSource();
    }
}
