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
        return getAttributeValueInTheContextOf(getOwner(), null, ref, resultClass);
    }

    @Nonnull
    public SInstance getCreating(@Nonnull AttrInternalRef ref) {
        SInstance entry = get(ref);
        if (entry == null) {
            if (ref.isResolved()) {
                entry = createNewAttribute(ref);
            } else {
                entry = createTemporaryAttribute();
                entry.setAsAttribute(ref, getOwner());
            }
            set(ref, entry);
        }
        return entry;
    }

    @Nonnull
    protected SInstance createNewAttribute(@Nonnull AttrInternalRef ref) {
        for (SType<?> current = getOwner(); current != null; current = current.getSuperType()) {
            SType<?> attrType = current.getAttributeDefinedLocally(ref);
            if (attrType != null) {
                return set(ref, attrType.newAttributeInstanceFor(getOwner()));
            }
        }
        throw new SingularFormException(
                "Não existe o atributo '" + ref.getName() + "' definido em '" + getOwner().getName() +
                        "' ou nos tipos pai do mesmo", getOwner());
    }

    @Nullable
    final static <V> V getAttributeValueInTheContextOf(@Nonnull final SType<?> target, @Nullable SInstance contextInstance,
            @Nonnull AttrInternalRef ref, @Nullable Class<V> resultClass) {
        Objects.requireNonNull(target);
        Objects.requireNonNull(ref);
        SInstance instance = findAttributeInstance(target, ref);
        if (instance != null) {
            if (contextInstance != null) {
                return instance.getValueInTheContextOf(contextInstance, resultClass);
            } else if (resultClass == null) {
                return (V) instance.getValue();
            }
            return instance.getValueWithDefault(resultClass);
        }
        SType<?> atr = getAttributeDefinedHierarchy(target, ref);
        if (resultClass == null) {
            return (V) atr.getAttributeValueOrDefaultValueIfNull();
        }
        return atr.getAttributeValueOrDefaultValueIfNull(resultClass);
    }

    @Nonnull
    final static SType<?> getAttributeDefinedHierarchy(@Nonnull SType<?> type, @Nonnull AttrInternalRef ref) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(ref);
        for (SType<?> current = type; current != null; current = current.getSuperType()) {
            SType<?> att = current.getAttributeDefinedLocally(ref);
            if (att != null) {
                return att;
            }
        }
        throw new SingularFormException("Não existe atributo '" + ref.getName() + "' em " + type.getName(), type);
    }

    @Nullable
    final static SInstance findAttributeInstance(@Nonnull SType<?> target, @Nonnull AttrInternalRef ref) {
        for (SType<?> current = target; current != null; current = current.getSuperType()) {
            SInstance instance = current.getAttributeDirectly(ref);
            if (instance != null) {
                return instance;
            }
        }
        return null;
    }

}