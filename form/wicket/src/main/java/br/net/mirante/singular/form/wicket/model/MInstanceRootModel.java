package br.net.mirante.singular.form.wicket.model;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.event.IMInstanceListener;
import br.net.mirante.singular.form.mform.event.MInstanceEventType;
import br.net.mirante.singular.form.mform.event.MInstanceListeners;
import br.net.mirante.singular.form.mform.event.SInstanceEvent;
import br.net.mirante.singular.form.mform.io.InstanceSerializableRef;

/**
 * <p>
 * Model para referência da MInstancia raiz da edição atual (não necessariamente
 * a raiz do Document) que já faz a correta serialização de MInstancia e
 * posterior resolução do respectivo Dicionário para viabilziar deserialziação.
 * </p>
 *
 * @see {@link br.net.mirante.singular.form.mform.io.InstanceSerializableRef}
 * @author Daniel C. Bordin
 */
public class MInstanceRootModel<I extends SInstance> extends AbstractSInstanceModel<I> implements IMInstanceEventCollector<I> {

    private final InstanceSerializableRef<I> instanceRef;

    private transient IMInstanceListener.EventCollector instanceListener;

    public MInstanceRootModel() {
        instanceRef = new InstanceSerializableRef<I>();
    }

    public MInstanceRootModel(I object) {
        instanceRef = new InstanceSerializableRef<I>(object);
    }

    @Override
    public I getObject() {
        if (instanceRef.get() != null && this.instanceListener == null) {
            this.instanceListener = new IMInstanceListener.EventCollector();
            MInstanceListeners listeners = instanceRef.get().getDocument().getInstanceListeners();
            listeners.add(MInstanceEventType.VALUE_CHANGED, this.instanceListener);
            listeners.add(MInstanceEventType.LIST_ELEMENT_ADDED, this.instanceListener);
            listeners.add(MInstanceEventType.LIST_ELEMENT_REMOVED, this.instanceListener);
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
            instanceRef.get().getDocument().getInstanceListeners().remove(MInstanceEventType.values(), this.instanceListener);
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
