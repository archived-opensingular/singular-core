package br.net.mirante.singular.form.wicket.model;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;

import br.net.mirante.singular.form.mform.MDicionarioResolver;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.io.FormSerializationUtil;
import br.net.mirante.singular.form.mform.io.FormSerializationUtil.FormSerialized;
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
public class MInstanceRootModel<I extends MInstancia> extends AbstractMInstanciaModel<I>implements Externalizable {

    private transient I object;

    private MDicionarioResolverSerializable dicionarioResolverSerializable;

    public MInstanceRootModel() {
    }

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
        return this.object;
    }

    @Override
    public void setObject(I object) {
        this.object = object;
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
