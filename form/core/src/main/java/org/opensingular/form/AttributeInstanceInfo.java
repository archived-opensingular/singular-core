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
 * Contém informações referente a instância de um atributo.
 *
 * @author Daniel C. Bordin
 */
public final class AttributeInstanceInfo {

    private final SInstance instanceOwner;

    private final SType<?> typeOwner;

    private final AttrInternalRef ref;

    /** Cira um instância de atributo que está associado a um SIntance. */
    AttributeInstanceInfo(AttrInternalRef ref, SInstance instanceOwner) {
        this.instanceOwner = instanceOwner;
        this.typeOwner = null;
        this.ref = ref;
    }

    /** Cira um instância de atributo que está associado a um SType. */
    AttributeInstanceInfo(AttrInternalRef ref, SType<?> typeOwner) {
        this.instanceOwner = null;
        this.typeOwner = typeOwner;
        this.ref = ref;
    }

    /** Nome completo do atributo. */
    public String getName() {
        return ref.getName();
    }

    /** Retorna a instância a qual o atributo associa um valor (pode ser null, se o atributo for de um tipo). */
    public SInstance getInstanceOwner() {
        return instanceOwner;
    }

    /** Retorna o tipo ao qual o atributo associa um valor (pode ser null, se o atributo for de instância). */
    public SType<?> getTypeOwner() {
        return typeOwner;
    }

    AttrInternalRef getRef() {
        return ref;
    }
}
