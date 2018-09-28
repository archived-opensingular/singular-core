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
import java.util.Objects;

/**
 * Contém informações referente a instância de um atributo.
 *
 * @author Daniel C. Bordin
 */
public final class AttributeInstanceInfo {

    private final SInstance instanceOwner;

    private final SType<?> typeOwner;

    private final AttrInternalRef ref;

    /** Cira um instância de atributo que está associado a um SInstance. */
    AttributeInstanceInfo(@Nonnull AttrInternalRef ref, @Nonnull SInstance instanceOwner) {
        this.instanceOwner = Objects.requireNonNull(instanceOwner);
        this.typeOwner = null;
        this.ref = Objects.requireNonNull(ref);
    }

    /** Cira um instância de atributo que está associado a um SType. */
    AttributeInstanceInfo(@Nonnull AttrInternalRef ref, @Nonnull SType<?> typeOwner) {
        this.instanceOwner = null;
        this.typeOwner = Objects.requireNonNull(typeOwner);
        this.ref = Objects.requireNonNull(ref);
    }

    /** Nome completo do atributo. */
    @Nonnull
    public String getName() {
        return ref.getName();
    }

    /** Retorna a instância a qual o atributo associa um valor (pode ser null, se o atributo for de um tipo). */
    @Nullable
    public SInstance getInstanceOwner() {
        return instanceOwner;
    }

    /** Retorna o tipo ao qual o atributo associa um valor (pode ser null, se o atributo for de instância). */
    @Nullable
    public SType<?> getTypeOwner() {
        return typeOwner;
    }

    @Nonnull
    AttrInternalRef getRef() {
        return ref;
    }
}
