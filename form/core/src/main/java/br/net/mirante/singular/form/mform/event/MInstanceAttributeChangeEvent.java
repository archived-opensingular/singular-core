package br.net.mirante.singular.form.mform.event;

import br.net.mirante.singular.form.mform.MInstancia;

public class MInstanceAttributeChangeEvent extends MInstanceEvent {

    private final MInstancia attributeInstance;
    private final Object     oldValue;
    private final Object     newValue;

    public MInstanceAttributeChangeEvent(MInstancia instance, MInstancia attributeInstance, Object oldValue, Object newValue) {
        super(instance);
        this.attributeInstance = attributeInstance;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public MInstancia getAttributeInstance() {
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
