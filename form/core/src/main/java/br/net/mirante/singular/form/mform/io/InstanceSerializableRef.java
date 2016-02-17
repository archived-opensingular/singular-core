package br.net.mirante.singular.form.mform.io;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.function.Supplier;

import br.net.mirante.singular.form.mform.SInstance;

/**
 * <p>
 * Referencia serializável para um instancia do form. Faz todos os controles
 * necessários para serialização e deserialização da instância.
 * </p>
 * <p>
 * Espera que
 * {@link br.net.mirante.singular.form.mform.SDictionary#setSerializableDictionarySelfReference(br.net.mirante.singular.form.mform.SDictionaryRef)}
 * tenha sido corretamente configurado e não seja null.
 * </p>
 *
 * @author Daniel C. Bordin
 */
public class InstanceSerializableRef<I extends SInstance> implements Externalizable, Supplier<I> {

    private transient I instance;

    public InstanceSerializableRef() {
        this(null);
    }

    public InstanceSerializableRef(I instance) {
        set(instance);
    }

    /**
     * Aceita valor null.
     */
    public void set(I instance) {
        this.instance = instance;
        if (instance != null) {
            FormSerializationUtil.verificarDicionaryRef(instance);
        }
    }

    @Override
    public I get() {
        return instance;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        FormSerialized fs = FormSerializationUtil.toSerializedObject(instance);
        out.writeObject(fs);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        FormSerialized fs = (FormSerialized) in.readObject();
        instance = (I) FormSerializationUtil.toInstance(fs);
    }
}
