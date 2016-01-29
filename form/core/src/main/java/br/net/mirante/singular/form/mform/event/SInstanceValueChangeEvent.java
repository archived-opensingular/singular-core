package br.net.mirante.singular.form.mform.event;

import br.net.mirante.singular.form.mform.SInstance2;

public class SInstanceValueChangeEvent extends SInstanceEvent {

    private final Object oldValue;
    private final Object newValue;

    public SInstanceValueChangeEvent(SInstance2 instance, Object oldValue, Object newValue) {
        super(instance);
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public Object getOldValue() {
        return oldValue;
    }
    public Object getNewValue() {
        return newValue;
    }

    @Override
    public String toString() {
        return super.toString() + " = " + getOldValue() + " => " + getNewValue();
    }
}
