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
 * Representa um mapa de valores de atributos associados a um {@link SType}.
 *
 * @author Daniel C. Bordin on 01/05/2017.
 */
final class AttributeValuesManagerForSType extends AttributeValuesManager<SType<?>> {

    AttributeValuesManagerForSType(@Nonnull SType<?> owner) {
        super(owner);
    }

    @Override
    @Nullable
    public <V> V getAttributeValue(@Nonnull AttrInternalRef ref, @Nullable Class<V> resultClass) {
        return SAttributeUtil.getAttributeValueInTheContextOf(getOwner(), null, ref, resultClass);
    }

    @Override
    void setEntryAsAttribute(@Nonnull SInstance entry, @Nonnull AttrInternalRef ref) {
        entry.setAsAttribute(ref, getOwner());
    }

    @Nonnull
    protected SInstance createNewAttribute(@Nonnull AttrInternalRef ref) {
        SType<?> attrType =  SAttributeUtil.getAttributeDefinitionInHierarchy(getOwner(), ref);
        return Objects.requireNonNull(set(ref, attrType.newAttributeInstanceFor(getOwner())));
    }
}
