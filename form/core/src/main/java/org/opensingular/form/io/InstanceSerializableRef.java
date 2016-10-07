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

package org.opensingular.form.io;

import org.opensingular.form.SInstance;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.function.Supplier;

/**
 * <p>
 * Referencia serializável para um instancia do form. Faz todos os controles
 * necessários para serialização e deserialização da instância.
 * </p>
 * <p>
 * Espera que a instância atenda aos critérios definidos em
 * {@link FormSerializationUtil#checkIfSerializable(SInstance)} ou dispara
 * exception.
 * </p>
 *
 * @author Daniel C. Bordin
 */
public class InstanceSerializableRef<I extends SInstance> implements Externalizable, Supplier<I> {

    private transient I instance;
    private transient FormSerialized fs;

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
            FormSerializationUtil.checkIfSerializable(instance);
        }
    }

    @Override
    public I get() {
        if (instance == null && fs != null) {
            instance = (I) FormSerializationUtil.toInstance(fs);
            fs = null;
        }
        return instance;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        FormSerialized fs = FormSerializationUtil.toSerializedObject(get());
        out.writeObject(fs);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        // Deixa para fazer a deserialização depois, para deixar carregar todas
        // as dependências serializadas, pois pode ocorrer de ter alguma
        // referencia indiretamente usada em FormSerialized que ainda não foi
        // carregada e daria NullPointerException

        fs = (FormSerialized) in.readObject();
    }
}
