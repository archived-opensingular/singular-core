/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import org.opensingular.singular.form.builder.selection.SSelectionBuilder;
import org.opensingular.singular.form.builder.selection.SelectionBuilder;
import org.opensingular.singular.form.document.SDocument;
import org.opensingular.singular.form.type.basic.SPackageBasic;
import org.opensingular.singular.form.type.core.SPackageCore;
import org.opensingular.singular.form.view.SMultiSelectionByPicklistView;
import org.opensingular.singular.form.view.SMultiSelectionBySelectView;
import org.opensingular.singular.form.view.SView;

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

    protected final E setElementsType(Class<E> elementsTypeClass) {
        return setElementsType(null, resolveType(elementsTypeClass));
    }

    protected final E setElementsType(E elementsType) {
        return setElementsType(null, elementsType);
    }

    protected final E setElementsType(String simpleNameNewType, Class<E> elementsTypeClass) {
        return setElementsType(simpleNameNewType, resolveType(elementsTypeClass));
    }

    protected final E setElementsType(String simpleNameNewType, E elementsType) {
        if (this.elementsType != null) {
            throw new SingularFormException("O tipo da lista já está definido", this);
        }
        this.elementsType = extendType(simpleNameNewType, elementsType);
        return this.elementsType;
    }

    @Override
    protected void extendSubReference() {
        if (getSuperType() instanceof STypeList) {
            E type = (E) ((STypeList) getSuperType()).elementsType;
            if (type != null) {
                setElementsType(type);
            }
        }
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

    public STypeList<E, I> withMaximumSizeOf(Integer size) {
        this.asAtr().setAttributeValue(SPackageBasic.ATR_MAXIMUM_SIZE, size);
        return this;
    }

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);
        addInstanceValidator(validatable -> {
            final Integer minimumSize = validatable.getInstance().getType().asAtr().getAttributeValue(SPackageBasic.ATR_MINIMUM_SIZE);
            if (minimumSize != null && validatable.getInstance().getValue().size() < minimumSize) {
                validatable.error("A Quantidade mínima de " + getLabel(validatable.getInstance()) + " é " + minimumSize);
            }
        });
        addInstanceValidator(validatable -> {
            final Integer maximumSize = validatable.getInstance().getType().asAtr().getAttributeValue(SPackageBasic.ATR_MAXIMUM_SIZE);
            if (maximumSize != null && validatable.getInstance().getValue().size() > maximumSize) {
                validatable.error("A Quantidade máxima " + getLabel(validatable.getInstance()) + " é " + maximumSize);
            }
        });
    }

    private String getLabel(SInstance ins) {
        return Optional.ofNullable(ins.getAttributeValue(SPackageBasic.ATR_LABEL)).orElse("valores");
    }

    public Integer getMinimumSize() {
        return asAtr().getAttributeValue(SPackageBasic.ATR_MINIMUM_SIZE);
    }

    public Integer getMaximumSize() {
        return asAtr().getAttributeValue(SPackageBasic.ATR_MAXIMUM_SIZE);
    }

    public <T extends Serializable> SelectionBuilder<T, SIList<I>, I> selectionOf(Class<T> clazz, SView view) {
        this.setView(() -> view);
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

    public SSelectionBuilder selection() {
        this.setView(SMultiSelectionBySelectView::new);
        return new SSelectionBuilder(this);
    }

    public SSelectionBuilder autocomplete() {
        this.setView(SMultiSelectionByPicklistView::new);
        return new SSelectionBuilder(this);
    }

}