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

import org.opensingular.form.calculation.SimpleValueCalculation;
import org.opensingular.form.internal.PathReader;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.internal.form.util.ArrUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

/**
 * Representa um mapa de valores de atributos pertencente a um {@link SType} ou a um {@link SInstance}, indexados de
 * acordo com o registro de atributos do dicionário. Tenta fazer a criação das estruturas internas da forma mais lazy
 * possível.
 */
abstract class AttributeValuesManager<OWNER extends SAttributeEnabled> {

    /** A quem os atributos se refere. */
    private final OWNER owner;

    /** Atributos associados ao owner. */
    private SInstance[] attributes;

    AttributeValuesManager(@Nonnull OWNER owner) {
        this.owner = Objects.requireNonNull(owner);
    }

    /** Retorna o objeto ao qual pertence este mapa de atributos. */
    @Nonnull
    public OWNER getOwner() {
        return owner;
    }

    /** Resolve o valor do atributo solicitado. */
    @Nullable
    public abstract <V> V getAttributeValue(@Nonnull AttrInternalRef ref, @Nullable Class<V> resultClass);

    /** Resolve o valor do atributo solicitado. */
    @Nullable
    public <V> V getAttributeValue(@Nonnull String attributeFullName, @Nullable Class<V> resultClass) {
        return getAttributeValue(getAttributeReferenceOrException(attributeFullName), resultClass);
    }

    @Nullable
    protected final SInstance set(@Nonnull AttrInternalRef ref, @Nullable SInstance attrInstance) {
        attributes = ArrUtil.arraySet(attributes, ref.getIndex(), attrInstance, SInstance.class, ref.getMax());
        return attrInstance;
    }

    /** Lista todos os atributos definidos. */
    @Nonnull
    public final Collection<SInstance> getAttributes() {
        return ArrUtil.arrayAsCollection(attributes);
    }

    /** Retorna o atributo se existir associado no mapa atual de atributos. */
    @Nullable
    public SInstance get(@Nonnull AttrInternalRef ref) {
        SInstance instance = ArrUtil.arrayGet(attributes, ref.getIndex());
        if (instance != null && instance.isAttributeShouldMigrate() && ref.isResolved()) {
            //Precida migra o atributo de String para o tipo definitivo, pois a carga do valor foi lazy
            SInstance newAttr = createNewAttribute(ref);
            newAttr.setValue(instance.getValue());
            return set(ref, newAttr);
        }
        return instance;
    }

    @Nonnull
    protected abstract SInstance createNewAttribute(@Nonnull AttrInternalRef ref);

    /**
     * Cria um atributo temporariamente sem a sua definição de tipo definitiva. Em quanto isso, será considerado como
     * String. Provavelmente utilizado para guardar valores em quanto o real registro do atributo não é feito no
     * dicionário.
     */
    @Nonnull
    protected final SInstance createTemporaryAttribute() {
        SType<?> attrType = getOwner().getDictionary().getType(STypeString.class);
        SInstance attr = attrType.newInstance(getOwner().getDictionary().getInternalDicionaryDocument());
        attr.setAttributeShouldMigrate();
        return attr;
    }

    public final <V> void setAttributeValue(@Nonnull AtrRef<?, ?, V> atr, @Nullable V value) {
        setAttributeValue(getAttributeReferenceOrException(atr), null, value);
    }

    public void setAttributeValue(@Nonnull String attributeFullName, @Nullable String subPath, @Nullable Object value) {
        setAttributeValue(getAttributeReferenceOrException(attributeFullName), subPath, value);
    }

    public void setAttributeValue(@Nonnull AttrInternalRef ref, @Nullable String subPath, @Nullable Object value) {
        SInstance instanceAtr = getCreating(ref);
        if (subPath != null) {
            instanceAtr.setValue(new PathReader(subPath), value);
        } else {
            instanceAtr.setValue(value);
        }
    }

    @Nonnull
    protected abstract SInstance getCreating(@Nonnull AttrInternalRef ref);

    public <V> void setAttributeCalculation(@Nonnull AtrRef<?, ?, V> atr, @Nullable SimpleValueCalculation<V> value) {
        setAttributeCalculation(getAttributeReferenceOrException(atr), null, value);
    }

    public <V> void setAttributeCalculation(@Nonnull String attributeFullName, @Nullable String subPath,
            @Nullable SimpleValueCalculation<V> valueCalculation) {
        setAttributeCalculation(getAttributeReferenceOrException(attributeFullName), subPath, valueCalculation);
    }

    public <V> void setAttributeCalculation(@Nonnull AttrInternalRef ref, @Nullable String subPath,
            @Nullable SimpleValueCalculation<V> valueCalculation) {
        SInstance instanceAtr = getCreating(ref);
        if (subPath != null) {
            instanceAtr = instanceAtr.getField(new PathReader(subPath));
        }
        if (!(instanceAtr instanceof SISimple)) {
            throw new SingularFormException(
                    "O atributo " + instanceAtr.getPathFull() + " não é do tipo " + SISimple.class.getName(),
                    instanceAtr);
        }
        ((SISimple) instanceAtr).setValueCalculation(valueCalculation);
    }

    @Nonnull
    protected final AttrInternalRef getAttributeReferenceOrException(@Nonnull String attributeFullName) {
        return getOwner().getDictionary().getAttributeReferenceOrException(attributeFullName);
    }

    @Nonnull
    protected final AttrInternalRef getAttributeReferenceOrException(@Nonnull AtrRef<?, ?, ?> atr) {
        return getOwner().getDictionary().getAttributeReferenceOrException(atr);
    }

    /** Retorna o atributo se houver um associada diretamente ao objeto alvo. */
    @Nonnull
    public static Optional<SInstance> staticGetAttributeDirectly(@Nonnull SAttributeEnabled target,
            @Nullable AttributeValuesManager attributes, @Nonnull String fullName) {
        if (attributes == null) {
            return Optional.empty();
        }
        AttrInternalRef ref = target.getDictionary().getAttributeReference(fullName);
        return ref == null ? Optional.empty() : Optional.ofNullable(attributes.get(ref));
    }

    /** Retorna o atributo se houver um associada diretamente ao objeto alvo. */
    @Nullable
    public static SInstance staticGetAttributeDirectly(@Nullable AttributeValuesManager attributes,
            @Nonnull AttrInternalRef ref) {
        return attributes == null ? null : attributes.get(ref);
    }

    @Nonnull
    public static Collection<SInstance> staticGetAttributes(@Nullable AttributeValuesManager attributes) {
        return attributes == null ? Collections.emptyList() : ArrUtil.arrayAsCollection(attributes.attributes);
    }
}
