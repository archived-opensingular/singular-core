package br.net.mirante.singular.form.mform.event;

import br.net.mirante.singular.form.mform.SIList;
import br.net.mirante.singular.form.mform.SInstance;

public class SInstanceListElementAddedEvent extends SInstanceStructureChangeEvent {

    private final SInstance addedInstance;
    private final int        index;

    public SInstanceListElementAddedEvent(SIList<? extends SInstance> source, SInstance addedInstance, int index) {
        super(source);
        this.addedInstance = addedInstance;
        this.index = index;
    }

    public SInstance getAddedInstance() {
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
