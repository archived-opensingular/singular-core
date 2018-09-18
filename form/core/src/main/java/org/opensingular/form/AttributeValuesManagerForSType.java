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
import java.util.Optional;

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

    @Override
    void setEntryAsAttribute(@Nonnull SInstance entry, @Nonnull AttrInternalRef ref) {
        entry.setAsAttribute(ref, getOwner());
    }

    @Nonnull
    protected SInstance createNewAttribute(@Nonnull AttrInternalRef ref) {
        for (SType<?> current = getOwner(); current != null; current = current.getSuperType()) {
            SType<?> attrType = current.getAttributeDefinedLocally(ref);
            if (attrType != null) {
                return Objects.requireNonNull(set(ref, attrType.newAttributeInstanceFor(getOwner())));
            }
        }
        throw new SingularFormException(
                "Não existe o atributo '" + ref.getName() + "' definido em '" + getOwner().getName() +
                        "' ou nos tipos pai do mesmo", getOwner());
    }

    @Nullable
    @SuppressWarnings("unchecked")
    static <V> V getAttributeValueInTheContextOf(@Nonnull final SType<?> target, @Nullable SInstance contextInstance,
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
    static SType<?> getAttributeDefinedHierarchy(@Nonnull SType<?> type, @Nonnull AttrInternalRef ref) {
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
    static SInstance findAttributeInstance(@Nonnull SType<?> target, @Nonnull AttrInternalRef ref) {
        return findAttributeInstance(target, ref, null);
    }

    @Nullable
    private static SInstance findAttributeInstance(@Nonnull SType<?> target, @Nonnull AttrInternalRef ref,
            @Nullable SType<?> stopTypeNotIncluded) {
        SInstance instance = null;
        for (SType<?> type = target; instance == null && type != stopTypeNotIncluded && type != null; type = type.getSuperType()) {
            instance = type.getAttributeDirectly(ref);
            if (instance == null) {
                Optional<SType<?>> complementary = target.getComplementarySuperType();
                if (complementary.isPresent() && type.getSuperType() != null) {
                    SType<?> stop = SFormUtil.findCommonType(type.getSuperType(), complementary.get());
                    instance = findAttributeInstance(complementary.get(), ref, stop);
                }
            }
        }
        return instance;
    }

}
