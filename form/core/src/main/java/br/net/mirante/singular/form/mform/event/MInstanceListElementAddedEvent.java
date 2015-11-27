package br.net.mirante.singular.form.mform.event;

import br.net.mirante.singular.form.mform.MILista;
import br.net.mirante.singular.form.mform.MInstancia;

public class MInstanceListElementAddedEvent extends MInstanceStructureChangeEvent {

    private final MInstancia addedInstance;
    private final int        index;

    public MInstanceListElementAddedEvent(MILista<? extends MInstancia> source, MInstancia addedInstance, int index) {
        super(source);
        this.addedInstance = addedInstance;
        this.index = index;
    }

    public MInstancia getAddedInstance() {
        return addedInstance;
    }
    public int getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return super.toString() + "[" + getIndex() + "] += " + getAddedInstance();
    }
}
