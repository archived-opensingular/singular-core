/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.model;

import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.event.ISInstanceListener;
import br.net.mirante.singular.form.event.SInstanceEvent;
import br.net.mirante.singular.form.event.SInstanceEventType;
import br.net.mirante.singular.form.event.SInstanceListeners;
import br.net.mirante.singular.form.io.InstanceSerializableRef;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * Model para referência da MInstancia raiz da edição atual (não necessariamente
 * a raiz do Document) que já faz a correta serialização de MInstancia e
 * posterior resolução do respectivo Dicionário para viabilziar deserialziação.
 * </p>
 *
 * @see {@link InstanceSerializableRef}
 * @author Daniel C. Bordin
 */
public class MInstanceRootModel<I extends SInstance> extends AbstractSInstanceModel<I> implements IMInstanceEventCollector<I> {

    private final InstanceSerializableRef<I> instanceRef;

    private transient ISInstanceListener.EventCollector instanceListener;

    public MInstanceRootModel() {
        instanceRef = new InstanceSerializableRef<I>();
    }

    public MInstanceRootModel(I object) {
        instanceRef = new InstanceSerializableRef<I>(object);
    }

    @Override
    public I getObject() {
        if (instanceRef.get() != null && this.instanceListener == null) {
            this.instanceListener = new ISInstanceListener.EventCollector();
            SInstanceListeners listeners = instanceRef.get().getDocument().getInstanceListeners();
            listeners.add(SInstanceEventType.VALUE_CHANGED, this.instanceListener);
            listeners.add(SInstanceEventType.LIST_ELEMENT_ADDED, this.instanceListener);
            listeners.add(SInstanceEventType.LIST_ELEMENT_REMOVED, this.instanceListener);
        }
        return instanceRef.get();
    }

    @Override
    public void setObject(I object) {
        detachListener();
        instanceRef.set(object);
    }

    @Override
    public void detach() {
        super.detach();
        detachListener();
    }

    protected void detachListener() {
        if (instanceRef.get() != null && this.instanceListener != null) {
            instanceRef.get().getDocument().getInstanceListeners().remove(SInstanceEventType.values(), this.instanceListener);
        }
        this.instanceListener = null;
    }

    @Override
    public List<SInstanceEvent> getInstanceEvents() {
        return (instanceListener == null) ? Collections.emptyList() : instanceListener.getEvents();
    }
    @Override
    public void clearInstanceEvents() {
        if (instanceListener != null)
            instanceListener.clear();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((instanceRef.get() == null) ? 0 : instanceRef.get().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MInstanceRootModel<?> other = (MInstanceRootModel<?>) obj;
        return Objects.equals(instanceRef.get(), other.instanceRef.get());
    }
}
