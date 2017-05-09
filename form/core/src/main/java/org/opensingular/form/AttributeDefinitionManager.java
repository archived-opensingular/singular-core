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
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * Representa uma mapa do atributos definidos para um {@link SType} em particular.
 *
 * @author Daniel C. Bordin
 */
final class AttributeDefinitionManager implements Iterable<SType<?>> {

    private SType<?>[] attributes;

    /** Adiciona uma nova definição de atributo ao tipo. */
    void add(@Nonnull SType<?> targetOwner, @Nonnull SType<?> attributeDef) {
        AttrInternalRef ref = attributeDef.checkIfIsAttribute();
        SType<?> currentOwner = ref.getOwner();
        if (currentOwner != null && currentOwner != targetOwner) {
            throw new SingularFormException(
                    "O Atributo '" + attributeDef.getName() + "' pertence excelusivamente ao tipo '" +
                            currentOwner.getName() + "'. Assim não pode ser reassociado a classe '" +
                            targetOwner.getName(), targetOwner);
        }
        if (get(ref) != null) {
            throw new SingularFormException(
                    "Já existe um atributo '" + ref.getName() + "' definido em " + targetOwner.getName());
        }
        attributes = AtrUtil.arraySet(attributes, ref.getIndex(), attributeDef, SType.class, ref.getMax());
    }

    @Nullable
    public SType<?> get(@Nonnull AttrInternalRef ref) {
        return AtrUtil.arrayGet(attributes, ref.getIndex());
    }

    @Nonnull
    public Collection<SType<?>> getAttributes() {
        return AtrUtil.arrayAsCollection(attributes);
    }

    @Override
    @Nonnull
    public Iterator<SType<?>> iterator() {
        return AtrUtil.arrayAsIterator(attributes);
    }

    @Nullable
    public static SType<?> staticGetAttributeDefinedLocally(@Nullable AttributeDefinitionManager attributesDefined,
            @Nonnull AttrInternalRef ref) {
        return attributesDefined == null ? null : attributesDefined.get(ref);
    }

    @Nonnull
    public static Collection<SType<?>> getStaticAttributes(@Nullable AttributeDefinitionManager attributesDefined) {
        return attributesDefined == null ? Collections.emptyList() : attributesDefined.getAttributes();
    }
}
