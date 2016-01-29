package br.net.mirante.singular.form.mform.event;

import br.net.mirante.singular.form.mform.SList;
import br.net.mirante.singular.form.mform.SInstance2;

public class SInstanceListElementRemovedEvent extends SInstanceStructureChangeEvent {

    private final SInstance2 removedInstance;
    private final int        index;

    public SInstanceListElementRemovedEvent(SList<? extends SInstance2> source, SInstance2 removedInstance, int index) {
        super(source);
        this.removedInstance = removedInstance;
        this.index = index;
    }

    public SInstance2 getRemovedInstance() {
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
