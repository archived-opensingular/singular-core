package br.net.mirante.singular.form.mform.event;

import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import br.net.mirante.singular.form.mform.MILista;
import br.net.mirante.singular.form.mform.MInstancia;

public class MInstanceListeners {

    private ListMultimap<MInstanceEventType, IMInstanceListener> instanceListeners;

    public void fireInstanceValueChanged(MInstancia instance, Object oldValue, Object newValue) {
        if (hasListenersFor(MInstanceEventType.VALUE_CHANGED))
            fireInstanceEvent(MInstanceEventType.VALUE_CHANGED, new MInstanceValueChangeEvent(instance, oldValue, newValue));
    }
    public void fireInstanceAttributeChanged(MInstancia instance, MInstancia attributeInstance, Object oldValue, Object valor) {
        if (hasListenersFor(MInstanceEventType.ATTRIBUTE_CHANGED))
            fireInstanceEvent(MInstanceEventType.ATTRIBUTE_CHANGED, new MInstanceAttributeChangeEvent(instance, attributeInstance, oldValue, valor));
    }
    public void fireInstanceListElementAdded(MILista<?> listInstance, MInstancia addedInstance, int index) {
        if (hasListenersFor(MInstanceEventType.LIST_ELEMENT_ADDED))
            fireInstanceEvent(MInstanceEventType.LIST_ELEMENT_ADDED, new MInstanceListElementAddedEvent(listInstance, addedInstance, index));
    }
    public void fireInstanceListElementRemoved(MILista<?> listInstance, MInstancia removedInstance, int index) {
        if (hasListenersFor(MInstanceEventType.LIST_ELEMENT_REMOVED))
            fireInstanceEvent(MInstanceEventType.LIST_ELEMENT_REMOVED, new MInstanceListElementRemovedEvent(listInstance, removedInstance, index));
    }

    public boolean hasListenersFor(MInstanceEventType eventType) {
        return (instanceListeners != null) && (instanceListeners.containsKey(eventType));
    }

    public void add(MInstanceEventType eventType, IMInstanceListener listener) {
        if (listener == null)
            return;
        getListeners().put(eventType, listener);
    }
    public void add(MInstanceEventType[] eventTypes, IMInstanceListener listener) {
        if (listener == null)
            return;
        for (MInstanceEventType eventType : MInstanceEventType.values())
            getListeners().put(eventType, listener);
    }

    public void remove(MInstanceEventType eventType, IMInstanceListener listener) {
        if (listener == null)
            return;
        add(eventType, listener);
    }
    public void remove(MInstanceEventType[] eventTypes, IMInstanceListener listener) {
        if (listener == null)
            return;
        for (MInstanceEventType eventType : MInstanceEventType.values())
            remove(eventType, listener);
    }

    protected void fireInstanceEvent(MInstanceEventType eventType, MInstanceEvent evt) {
        List<IMInstanceListener> listeners = instanceListeners.get(eventType);
        for (int i = 0; i < listeners.size(); i++) {
            IMInstanceListener listener = listeners.get(i);
            listener.onInstanceEvent(evt);
        }
    }
    private ListMultimap<MInstanceEventType, IMInstanceListener> getListeners() {
        if (this.instanceListeners == null)
            this.instanceListeners = ArrayListMultimap.create(1, 1);
        return this.instanceListeners;
    }
}
