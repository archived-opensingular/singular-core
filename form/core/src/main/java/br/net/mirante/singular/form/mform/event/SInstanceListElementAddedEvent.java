package br.net.mirante.singular.form.mform.event;

import br.net.mirante.singular.form.mform.SList;
import br.net.mirante.singular.form.mform.SInstance2;

public class SInstanceListElementAddedEvent extends SInstanceStructureChangeEvent {

    private final SInstance2 addedInstance;
    private final int        index;

    public SInstanceListElementAddedEvent(SList<? extends SInstance2> source, SInstance2 addedInstance, int index) {
        super(source);
        this.addedInstance = addedInstance;
        this.index = index;
    }

    public SInstance2 getAddedInstance() {
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
