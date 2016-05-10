/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form;

import br.net.mirante.singular.form.builder.selection.SSelectionBuilder;
import br.net.mirante.singular.form.builder.selection.SelectionBuilder;
import br.net.mirante.singular.form.document.SDocument;
import br.net.mirante.singular.form.type.basic.SPackageBasic;
import br.net.mirante.singular.form.type.core.SPackageCore;
import br.net.mirante.singular.form.view.SMultiSelectionByPicklistView;
import br.net.mirante.singular.form.view.SMultiSelectionBySelectView;
import br.net.mirante.singular.form.view.SView;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
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

    private Integer minimumSize;

    private Integer maximumSize;

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
     * MILista&lt;MIString> lista1 = (MILista&lt;MIString>) tipoLista.novaInstancia();
     *
     * // já devolvendo lista no tipo certo e verificando se correto
     * MILista&lt;MIString> lista2 = tipoLista.novaInstancia(MIString.class);
     * </pre>
     */
    @SuppressWarnings("unchecked")
    public <T extends SInstance> SIList<T> newInstance(Class<T> classOfElements) {
        SIList<?> newList = newInstance();
        if (!classOfElements.isAssignableFrom(getElementsType().getInstanceClass())) {
            throw new RuntimeException("As instancias da lista são do tipo " + getElementsType().getInstanceClass().getName()
                    + ", que não é compatível com o solicitado " + classOfElements.getName());
        }
        return (SIList<T>) newList;
    }

    @Override
    SIList<I> newInstance(SDocument owner) {
        if (elementsType == null) {
            throw new RuntimeException("Não é possível instanciar o tipo '" + getName()
                    + "' pois o tipo da lista (o tipo de seus elementos) não foram definidos");
        }
        SIList<I> lista = new SIList<>();
        lista.setType(this);
        lista.setDocument(owner);
        return lista;
    }

    protected void setElementsType(E elementsType) {
        if (this.elementsType != null) {
            throw new RuntimeException("O tipo da lista já está definido");
        }
        this.elementsType = elementsType;
    }

    /**
     * Define que o tipo da lista sera um novo tipo record (tipo composto) com o
     * nome infomado. O novo tipo é criado sem campos, devendo ser estruturado
     * na sequencia.
     */
    @SuppressWarnings("unchecked")
    void setElementsTypeAsNewCompositeType(String simpleNameNewCompositeType) {
        STypeComposite<?> type = extendType(simpleNameNewCompositeType, STypeComposite.class);
        setElementsType((E) type);
    }

    /**
     * Retorna o tipo do elementos contido na lista.
     */
    public E getElementsType() {
        return elementsType;
    }

    public STypeList<E, I> withMiniumSizeOf(Integer size) {
        this.minimumSize = size;
        return this;
    }

    public STypeList<E, I> withMaximumSizeOf(Integer size) {
        this.maximumSize = size;
        return this;
    }

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);
        addInstanceValidator(validatable -> {
            final Integer minimumSize = validatable.getInstance().getType().minimumSize;
            if (minimumSize != null && validatable.getInstance().getValue().size() < minimumSize) {
                validatable.error("A Quantidade mínima de " + getLabel(validatable.getInstance()) + " é " + minimumSize);
            }
        });
        addInstanceValidator(validatable -> {
            final Integer maximumSize = validatable.getInstance().getType().maximumSize;
            if (maximumSize != null && validatable.getInstance().getValue().size() > maximumSize) {
                validatable.error("A Quantidade máxima " + getLabel(validatable.getInstance()) + " é " + maximumSize);
            }
        });
    }

    private String getLabel(SInstance ins) {
        return Optional.ofNullable(ins.getAttributeValue(SPackageBasic.ATR_LABEL)).orElse("valores");
    }

    public Integer getMinimumSize() {
        return minimumSize;
    }

    public Integer getMaximumSize() {
        return maximumSize;
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