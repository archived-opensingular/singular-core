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

import org.opensingular.form.builder.selection.SSelectionBuilder;
import org.opensingular.form.builder.selection.SelectionBuilder;
import org.opensingular.form.calculation.SimpleValueCalculation;
import org.opensingular.form.document.SDocument;
import org.opensingular.form.type.basic.SPackageBasic;
import org.opensingular.form.type.core.SPackageCore;
import org.opensingular.form.view.SMultiSelectionByPicklistView;
import org.opensingular.form.view.SMultiSelectionBySelectView;
import org.opensingular.form.view.SView;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

/**
 * Representa um tipo lista, o qual deve ter um tipo definido para todos os seus
 * elementos. Basicamente representa um array.
 *
 * @author Daniel C. Bordin
 */
@SInfoType(name = "STypeList", spackage = SPackageCore.class)
public class STypeList<E extends SType<I>, I extends SInstance> extends SType<SIList<I>> implements ICompositeType {

    private E elementsType;

    @SuppressWarnings("unchecked")
    public STypeList() {
        // O cast na linha abaixo parece redundante, mas é necessário para
        // contornar um erro de compilação do JDK 8.0.60. Talvez no futuro
        // possa ser retirada
        super((Class<? extends SIList<I>>) (Class<? extends SInstance>) SIList.class);
    }

    /** Return the super type (parent type) of the current type. */
    @Override
    @Nonnull
    public final SType<SIList<I>> getSuperType() {
        return Objects.requireNonNull(super.getSuperType());
    }

    @Override
    public Collection<SType<?>> getContainedTypes() {
        return Collections.singleton(getElementsType());
    }

    /**
     * Cria a nova instância de lista com o cast para o generic tipo de
     * instancia informado. Se o tipo do conteudo não for compatível dispara
     * exception.
     * <p>
     * <p>
     * <pre>
     * MTipoLista&lt;MTipoString> tipoLista = ...
     *
     * // metodo simples e não dispara exception se tipo errado
     * MILista&lt;MIString> lista1 = (MILista&lt;MIString>) tipoLista.newInstance();
     *
     * // já devolvendo lista no tipo certo e verificando se correto
     * MILista&lt;MIString> lista2 = tipoLista.newInstance(MIString.class);
     * </pre>
     */
    @SuppressWarnings("unchecked")
    public <T extends SInstance> SIList<T> newInstance(Class<T> classOfElements) {
        SIList<?> newList = newInstance();
        if (!classOfElements.isAssignableFrom(getElementsType().getInstanceClass())) {
            throw new SingularFormException("As instancias da lista são do tipo " + getElementsType().getInstanceClass().getName()
                    + ", que não é compatível com o solicitado " + classOfElements.getName(), this);
        }
        return (SIList<T>) newList;
    }

    @Override
    SIList<I> newInstance(SDocument owner) {
        if (elementsType == null) {
            throw new SingularFormException("Não é possível instanciar o tipo '" + getName()
                    + "' pois o tipo da lista (o tipo de seus elementos) está null", this);
        }
        SIList<I> list = new SIList<>();
        list.setType(this);
        list.setDocument(owner);
        return list;
    }

    @Nonnull
    protected final E setElementsType(@Nonnull Class<E> elementsTypeClass) {
        return setElementsTypeInternal(null, resolveType(elementsTypeClass), null);
    }

    @Nonnull
    protected final E setElementsType(@Nonnull E elementsType) {
        return setElementsTypeInternal(null, elementsType, null);
    }

    @Nonnull
    protected final E setElementsType(@Nullable String simpleNameNewType, @Nonnull Class<E> elementsTypeClass) {
        return setElementsTypeInternal(simpleNameNewType, resolveType(elementsTypeClass), null);
    }

    @Nonnull
    protected final E setElementsType(@Nullable String simpleNameNewType, @Nonnull E elementsType) {
        return setElementsTypeInternal(simpleNameNewType, elementsType, null);
    }

    @Nonnull
    private E setElementsTypeInternal(@Nullable String simpleNameNewType, @Nonnull E elementsType,
            @Nullable SType<?> complementarySuperType) {
        if (this.elementsType != null) {
            throw new SingularFormException("O tipo da lista já está definido", this);
        }
        if (complementarySuperType == null) {
            this.elementsType = extendType(simpleNameNewType, elementsType);
        } else {
            this.elementsType = extendMultipleTypes(simpleNameNewType, elementsType, complementarySuperType);
        }
        return this.elementsType;
    }

    @Override
    protected void extendSubReference() {
        if (getSuperType().isList()) {
            @SuppressWarnings("unchecked")
            E type = (E) extractListType(getSuperType());
            if (type != null) {
                SType<?> complementarySuper = getComplementarySuperType().map(STypeList::extractListType).orElse(null);
                setElementsTypeInternal(null, type, complementarySuper);
            }
        }
    }

    @Nullable
    private static SType<?> extractListType(@Nonnull SType<?> listCandidate) {
        return ((STypeList) listCandidate).elementsType;
    }

    /**
     * Retorna o tipo do elementos contido na lista.
     */
    public E getElementsType() {
        return elementsType;
    }

    public STypeList<E, I> withMiniumSizeOf(Integer size) {
        this.asAtr().setAttributeValue(SPackageBasic.ATR_MINIMUM_SIZE, size);
        return this;
    }

    public STypeList<E, I> withMiniumSizeOf(SimpleValueCalculation<Integer> valueCalculation) {
        setAttributeCalculation(SPackageBasic.ATR_MINIMUM_SIZE, valueCalculation);
        return this;
    }

    public STypeList<E, I> withMaximumSizeOf(Integer size) {
        this.asAtr().setAttributeValue(SPackageBasic.ATR_MAXIMUM_SIZE, size);
        return this;
    }

    @Override
    protected void onLoadType(TypeBuilder tb) {
        addInstanceValidator(validatable -> {
            final Integer minimumSize = validatable.getInstance().asAtr().getAttributeValue(SPackageBasic.ATR_MINIMUM_SIZE);
            if (minimumSize != null && validatable.getInstance().getValue().size() < minimumSize) {
                validatable.error("A quantidade mínima de " + getLabel(validatable.getInstance()) + " é " + minimumSize);
            }
        });
        addInstanceValidator(validatable -> {
            final Integer maximumSize = validatable.getInstance().asAtr().getAttributeValue(SPackageBasic.ATR_MAXIMUM_SIZE);
            if (maximumSize != null && validatable.getInstance().getValue().size() > maximumSize) {
                validatable.error("A quantidade máxima " + getLabel(validatable.getInstance()) + " é " + maximumSize);
            }
        });
    }

    private String getLabel(SInstance ins) {
        return Optional.ofNullable(ins.getAttributeValue(SPackageBasic.ATR_LABEL)).orElse("valores");
    }

    @Nullable
    public Integer getMinimumSize() {
        return asAtr().getAttributeValue(SPackageBasic.ATR_MINIMUM_SIZE);
    }

    @Nullable
    public Integer getMaximumSize() {
        return asAtr().getAttributeValue(SPackageBasic.ATR_MAXIMUM_SIZE);
    }

    public <T extends Serializable> SelectionBuilder<T, SIList<I>, I> selectionOf(Class<T> clazz, SView view) {
        this.withView(() -> view);
        return new SelectionBuilder<>(this);
    }

    public <T extends Serializable> SelectionBuilder<T, SIList<I>, I> selectionOf(Class<T> clazz) {
        return selectionOf(clazz, new SMultiSelectionBySelectView());
    }

    public STypeList<E, I> selectionOf(Serializable... os) {
        new SelectionBuilder<>(this)
                .selfIdAndDisplay()
                .simpleProviderOf((Serializable[]) os);
        return this;
    }

    public <T extends Enum<T>> SType selectionOfEnum(Class<T> enumType) {
        this.selectionOf(Enum.class)
                .id(Enum::name)
                .display(Enum::toString)
                .enumConverter(enumType)
                .simpleProvider(ins -> Arrays.asList(enumType.getEnumConstants()));
        return this;
    }

    public SSelectionBuilder selection() {
        this.withView(SMultiSelectionBySelectView::new);
        return new SSelectionBuilder(this);
    }

    public SSelectionBuilder autocomplete() {
        this.withView(SMultiSelectionByPicklistView::new);
        return new SSelectionBuilder(this);
    }

}