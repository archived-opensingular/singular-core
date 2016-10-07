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
import org.opensingular.form.SingularFormException;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

/**
 * <p>
 * Recuperador de tipo com base no ID do mesmo. É provida pela aplicação host de
 * modo a permitir o recuperação do da definições tipo para o ID solicitado.
 * Tipicamente é utilziado no processo de deserialziação, recuperação de
 * instancias persistidas ou mesmo criação de uma nova versão.
 * </p>
 *
 * @author Daniel C. Bordin
 */
public abstract class TypeLoader<TYPE_KEY extends Serializable> {

    /**
     * Retorna a referência ao tipo solicitado se possível.
     *
     * @param typeId
     *            Identificador do tipo a ser carregado.
     */
    public final Optional<RefType> loadRefType(TYPE_KEY typeId) {
        return loadRefTypeImpl(Objects.requireNonNull(typeId));
    }

    /**
     * Implementa a efetiva recuperação da referência do tipo.
     *
     * @param typeId
     *            Identificador do tipo a ser carregado.
     */
    protected abstract Optional<RefType> loadRefTypeImpl(TYPE_KEY typeId);

    /** Recupera o tipo solicitado se possível. */
    public final Optional<SType<?>> loadType(TYPE_KEY typeId) {
        return loadTypeImpl(typeId);
    }

    /**
     * Implementa a efetiva recuperação do tipo.
     *
     * @param typeId
     *            Identificador do tipo a ser carregado.
     */
    protected abstract Optional<SType<?>> loadTypeImpl(TYPE_KEY typeId);

    /**
     * Recupera a referência ao tipo solicitado se possível ou dispara exception
     * se não encontrar.
     *
     * @exception SingularFormException
     *                Senão encontrar o tipo.
     */
    public final RefType loadRefTypeOrException(TYPE_KEY typeId) throws SingularFormException {
        return loadRefType(typeId).orElseThrow(() -> new SingularFormException("Não foi encontrado o tipo para o id=" + typeId));
    }

    /**
     * Recupera o tipo solicitado se possível ou dispara exception se não
     * encontrar.
     *
     * @exception SingularFormException
     *                Senão encontrar o tipo.
     */
    public final SType<?> loadTypeOrException(TYPE_KEY typeId) throws SingularFormException {
        return loadType(typeId).orElseThrow(() -> new SingularFormException("Não foi encontrado o tipo para o id=" + typeId));
    }
}
