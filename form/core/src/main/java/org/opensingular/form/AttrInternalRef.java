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

package org.opensingular.form;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Contém informações referente a definição de um atributo dentro do dicionário. Pode se referir a um atributo que foi
 * lido temporariamente e ainda não teve a sua definição criada no dicionário.
 *
 * @author Daniel C. Bordin
 */
final class AttrInternalRef {

    private final SDictionary dictionary;

    private final String name;

    private final Integer index;

    private SType<?> type;

    private SType<?> owner;

    private boolean selfReference;

    AttrInternalRef(@Nonnull SDictionary dictionary, @Nonnull String name, @Nonnull Integer index) {
        this.dictionary = dictionary;
        this.name = name;
        this.index = index;
    }

    /** Resolve a referência para apontar para o tipo informado. */
    final void resolve(@Nonnull SType<?> type) {
        if (this.type != null) {
            throw new SingularFormException("Internal Erro: should have called this method twice");
        }
        this.type = type;
        type.setAttrInternalRef(this);
    }

    /** Resolve a referência para apontar para o tipo informado e podendo definir o atribudo como sendo auto
     * referência. */
    final void resolve(@Nonnull SType<?> type, @Nullable SType<?> owner, boolean selfReference) {
        resolve(type);
        this.owner = owner;
        this.selfReference = selfReference;
    }

    /**
     * Retorna o tipo dono do atributo (onde o atributo está definido). Pode ser null se o atributo estiver apenas
     * criado no pacote mas não associnado ainda em um tipo específico.
     */
    public SType<?> getOwner() {
        return owner;
    }

    /** Indica se o tipo de retorno do atributo deve ser do mesmo tipo do tipo que o contêm. */
    public boolean isSelfReference() {
        return selfReference;
    }

    /** Retorna um índice único (sequencial) do atributo denttro do dicionário atual. */
    @Nonnull
    public Integer getIndex() {
        return index;
    }

    /** Retorna o nome completo do atributo. */
    @Nonnull
    public String getName() {
        return name;
    }

    /** A quantidade total de atributo definidos até o momento no dicionário. */
    final int getMax() {
        return dictionary.getAttributesArrayInicialSize();
    }

    /** Verifica se o atributo ja teve o seu tipo associado (registrado) no dicionário. */
    public boolean isResolved() {
        return type != null;
    }

    /** Retorna o tipo associado a essa referência, se a mesma ja tiver sido marcada como resolvida. */
    @Nullable
    public SType<?> getType() {
        return type;
    }
}
