package br.net.mirante.singular.form.mform.io;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.function.Supplier;

import br.net.mirante.singular.form.mform.SInstance;

/**
 * Referencia serializável para um instancia do form. Faz todos os controles
 * necessários para serialização e deserialização da instância.
 *
 * @author Daniel C. Bordin
 */
public class InstanceSerializableRef<I extends SInstance> implements Externalizable, Supplier<I> {

    private transient I instance;
    private final MDicionarioResolverSerializable dicionarioResolverSerializable;

    public InstanceSerializableRef() {
        this(null, null);
    }

    public InstanceSerializableRef(I instance) {
        this(instance, null);
    }

    public InstanceSerializableRef(I instance, MDicionarioResolverSerializable dicionarioResolverSerializable) {
        set(instance);
        this.dicionarioResolverSerializable = dicionarioResolverSerializable;
    }

    /**
     * Aceita valor null.
     */
    public void set(I instance) {
        this.instance = instance;
    }

    @Override
    public I get() {
        return instance;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        FormSerialized fs = FormSerializationUtil.toSerializedObject(instance, dicionarioResolverSerializable);
        out.writeObject(fs);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        FormSerialized fs = (FormSerialized) in.readObject();
        instance = (I) FormSerializationUtil.toInstance(fs);
    }
}
