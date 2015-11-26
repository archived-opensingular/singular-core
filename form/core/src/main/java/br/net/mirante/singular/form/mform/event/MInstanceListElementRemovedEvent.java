package br.net.mirante.singular.form.mform.event;

import br.net.mirante.singular.form.mform.MILista;
import br.net.mirante.singular.form.mform.MInstancia;

public class MInstanceListElementRemovedEvent extends MInstanceStructureChangeEvent {

    private final MInstancia removedInstance;
    private final int        index;

    public MInstanceListElementRemovedEvent(MILista<? extends MInstancia> source, MInstancia removedInstance, int index) {
        super(source);
        this.removedInstance = removedInstance;
        this.index = index;
    }

    public MInstancia getRemovedInstance() {
        return removedInstance;
    }
    public int getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return super.toString() + "[" + getIndex() + "] -= " + getRemovedInstance();
    }
}
