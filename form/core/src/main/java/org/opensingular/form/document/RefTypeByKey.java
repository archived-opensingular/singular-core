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

import org.opensingular.form.SingularFormException;
import org.opensingular.form.SType;

import java.io.Serializable;
import java.util.Objects;

/**
 * É um recuperador de referência ao tipo baseado em um chave de identificação.
 * É necessário apenas implementar ({@link #retrieveByKey()}).
 *
 * @author Daniel C. Bordin
 */
public abstract class RefTypeByKey<KEY extends Serializable> extends RefType {

    private final KEY typeId;

    public RefTypeByKey(KEY typeId) {
        this.typeId = Objects.requireNonNull(typeId);
    }

    public RefTypeByKey(KEY typeId, SType<?> type) {
        super(type);
        this.typeId = Objects.requireNonNull(typeId);
    }

    /**
     * Implementando baseado em {@link #retrieveByKey()}.
     */
    @Override
    public final SType<?> retrieve() {
        SType<?> type = retrieveByKey(typeId);
        if (type == null) {
            throw new SingularFormException(getClass().getName() + ".retrieveByKey(KEY) retornou null");
        }
        return type;
    }

    /** Deve localizar o tipo para o id informado. Não deve retornar null. */
    public abstract SType<?> retrieveByKey(KEY typeId);

}
