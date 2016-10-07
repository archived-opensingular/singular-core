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

package org.opensingular.form.document;

import org.opensingular.form.SType;
import org.opensingular.form.internal.util.SerializableReference;

/**
 * É uma referência serializável a um tipo, o que permite o uso em contexto que
 * necessitam serialização/deserialização do mesmo, tipicamente durante a
 * edição. O método {@link #retrieve()} deve ser implementado de forma que
 * quando deserializado a referência, o mesmo seja capaz de localizar (ou
 * recriar) o tipo novamente.
 *
 * @author Daniel C. Bordin
 */
public abstract class RefType extends SerializableReference<SType<?>> {

    public RefType() {
    }

    public RefType(SType<?> type) {
        super(type);
    }

    /**
     * Cria uma nova refência que utilizará o mesmo dicionário do RefType atual
     * para localizar o tipo informado.
     */
    public <T extends SType<?>> RefType createSubReference(Class<T> typeClass) {
        return new RefType() {
            @Override
            protected SType<?> retrieve() {
                return RefType.this.get().getDictionary().getType(typeClass);
            }
        };
    }
}
