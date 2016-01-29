package br.net.mirante.singular.form.mform.event;

import br.net.mirante.singular.form.mform.SInstance2;

public class SInstanceAttributeChangeEvent extends SInstanceEvent {

    private final SInstance2 attributeInstance;
    private final Object     oldValue;
    private final Object     newValue;

    public SInstanceAttributeChangeEvent(SInstance2 instance, SInstance2 attributeInstance, Object oldValue, Object newValue) {
        super(instance);
        this.attributeInstance = attributeInstance;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public SInstance2 getAttributeInstance() {
        return attributeInstance;
    }
    public Object getOldValue() {
        return oldValue;
    }
    public Object getNewValue() {
        return newValue;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + getAttributeInstance() + "]: " + getSource() + " = " + getOldValue() + " => " + getNewValue();
    }
}
