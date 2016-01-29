package br.net.mirante.singular.form.wicket.model;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import br.net.mirante.singular.form.mform.MDicionarioResolver;
import br.net.mirante.singular.form.mform.SInstance2;
import br.net.mirante.singular.form.mform.event.IMInstanceListener;
import br.net.mirante.singular.form.mform.event.SInstanceEvent;
import br.net.mirante.singular.form.mform.event.MInstanceEventType;
import br.net.mirante.singular.form.mform.event.MInstanceListeners;
import br.net.mirante.singular.form.mform.io.FormSerializationUtil;
import br.net.mirante.singular.form.mform.io.FormSerialized;
import br.net.mirante.singular.form.mform.io.MDicionarioResolverSerializable;

/**
 * <p>
 * Model para referência da MInstancia raiz da edição atual (não necessariamente
 * a raiz do Document) que já faz a correta serialização de MInstancia e
 * posterior resolução do respectivo Dicionário para viabilziar deserialziação.
 * </p>
 * <p>
 * Há duas formas de resolver a questão do dicionário usando esse model:
 * <ul>
 * <li>Setar o MDicionarioResolver default (singleton) em
 * {@link MDicionarioResolver#setDefault(MDicionarioResolver)}</li>
 * <li>Ao criar o model, passar um {@link MDicionarioResolverSerializable}
 * serializável para também ser serializado junto com os dados. Na volta
 * (deserialziação) usa esse resolver que foi serializado junto com os dados:
 * {@link FormSerializationUtil#toInstance(FormSerialized, MDicionarioResolverSerializable)}
 * </li>
 * </ul >
 * </p>
 *
 * @see {@link br.net.mirante.singular.form.mform.io.FormSerializationUtil}
 * @author Daniel C. Bordin
 */
public class MInstanceRootModel<I extends SInstance2> extends AbstractSInstanceModel<I>
    implements Externalizable,
    IMInstanceEventCollector<I> {

    private transient I                                 object;
    private transient IMInstanceListener.EventCollector instanceListener;

    private MDicionarioResolverSerializable dicionarioResolverSerializable;

    public MInstanceRootModel() {}

    public MInstanceRootModel(MDicionarioResolverSerializable dicionarioResolverSerializable) {
        this.dicionarioResolverSerializable = dicionarioResolverSerializable;
    }

    public MInstanceRootModel(I object) {
        setObject(object);
    }

    public MInstanceRootModel(I object, MDicionarioResolverSerializable dicionarioResolverSerializable) {
        setObject(object);
        this.dicionarioResolverSerializable = dicionarioResolverSerializable;
    }

    @Override
    public I getObject() {
        if (this.object != null && this.instanceListener == null) {
            this.instanceListener = new IMInstanceListener.EventCollector();
            MInstanceListeners listeners = this.object.getDocument().getInstanceListeners();
            listeners.add(MInstanceEventType.VALUE_CHANGED, this.instanceListener);
            listeners.add(MInstanceEventType.LIST_ELEMENT_ADDED, this.instanceListener);
            listeners.add(MInstanceEventType.LIST_ELEMENT_REMOVED, this.instanceListener);
        }
        return this.object;
    }

    @Override
    public void setObject(I object) {
        detachListener();
        this.object = object;
    }

    @Override
    public void detach() {
        super.detach();
        detachListener();
    }

    protected void detachListener() {
        if (this.object != null && this.instanceListener != null) {
            this.object.getDocument().getInstanceListeners().remove(MInstanceEventType.values(), this.instanceListener);
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
        result = prime * result + ((object == null) ? 0 : object.hashCode());
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
        return Objects.equals(object, other.object);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        FormSerialized fs = FormSerializationUtil.toSerializedObject(object, dicionarioResolverSerializable);
        out.writeObject(fs);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        FormSerialized fs = (FormSerialized) in.readObject();
        object = (I) FormSerializationUtil.toInstance(fs);
    }

}
