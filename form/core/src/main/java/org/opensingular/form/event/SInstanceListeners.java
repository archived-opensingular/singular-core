/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.form.event;

import org.opensingular.form.SInstance;
import org.opensingular.form.SIList;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import java.util.List;

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
