package br.net.mirante.singular.form.mform.event;

import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import br.net.mirante.singular.form.mform.SIList;
import br.net.mirante.singular.form.mform.SInstance;

public class SInstanceListeners {

    private ListMultimap<SInstanceEventType, ISInstanceListener> instanceListeners;

    public void fireInstanceValueChanged(SInstance instance, Object oldValue, Object newValue) {
        if (hasListenersFor(SInstanceEventType.VALUE_CHANGED))
            fireInstanceEvent(SInstanceEventType.VALUE_CHANGED, new SInstanceValueChangeEvent(instance, oldValue, newValue));
    }
    public void fireInstanceAttributeChanged(SInstance instance, SInstance attributeInstance, Object oldValue, Object valor) {
        if (hasListenersFor(SInstanceEventType.ATTRIBUTE_CHANGED))
            fireInstanceEvent(SInstanceEventType.ATTRIBUTE_CHANGED, new SInstanceAttributeChangeEvent(instance, attributeInstance, oldValue, valor));
    }
    public void fireInstanceListElementAdded(SIList<?> listInstance, SInstance addedInstance, int index) {
        if (hasListenersFor(SInstanceEventType.LIST_ELEMENT_ADDED))
            fireInstanceEvent(SInstanceEventType.LIST_ELEMENT_ADDED, new SInstanceListElementAddedEvent(listInstance, addedInstance, index));
    }
    public void fireInstanceListElementRemoved(SIList<?> listInstance, SInstance removedInstance, int index) {
        if (hasListenersFor(SInstanceEventType.LIST_ELEMENT_REMOVED))
            fireInstanceEvent(SInstanceEventType.LIST_ELEMENT_REMOVED, new SInstanceListElementRemovedEvent(listInstance, removedInstance, index));
    }

    public boolean hasListenersFor(SInstanceEventType eventType) {
        return (instanceListeners != null) && (instanceListeners.containsKey(eventType));
    }

    public void add(SInstanceEventType eventType, ISInstanceListener listener) {
        if (listener == null)
            return;
        getListeners().put(eventType, listener);
    }
    public void add(SInstanceEventType[] eventTypes, ISInstanceListener listener) {
        if (listener == null)
            return;
        for (SInstanceEventType eventType : SInstanceEventType.values())
            getListeners().put(eventType, listener);
    }

    public void remove(SInstanceEventType eventType, ISInstanceListener listener) {
        if (listener == null)
            return;
        add(eventType, listener);
    }
    public void remove(SInstanceEventType[] eventTypes, ISInstanceListener listener) {
        if (listener == null)
            return;
        for (SInstanceEventType eventType : SInstanceEventType.values())
            remove(eventType, listener);
    }

    protected void fireInstanceEvent(SInstanceEventType eventType, SInstanceEvent evt) {
        List<ISInstanceListener> listeners = instanceListeners.get(eventType);
        for (int i = 0; i < listeners.size(); i++) {
            ISInstanceListener listener = listeners.get(i);
            listener.onInstanceEvent(evt);
        }
    }
    private ListMultimap<SInstanceEventType, ISInstanceListener> getListeners() {
        if (this.instanceListeners == null)
            this.instanceListeners = ArrayListMultimap.create(1, 1);
        return this.instanceListeners;
    }
}
