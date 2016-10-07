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

/**
 * Contém informações referente a definição de um atributo.
 *
 * @author Daniel C. Bordin
 */
final class AttributeDefinitionInfo {

    private final SType<?> owner;

    private final boolean selfReference;

    AttributeDefinitionInfo() {
        this(null, false);
    }

    AttributeDefinitionInfo(SType<?> owner) {
        this(owner, false);
    }

    AttributeDefinitionInfo(SType<?> owner, boolean selfReference) {
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
}
