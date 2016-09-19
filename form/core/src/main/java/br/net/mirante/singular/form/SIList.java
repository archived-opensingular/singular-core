/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SIList<E extends SInstance> extends SInstance implements Iterable<E>, ICompositeInstance {

    private List<E> values;

    private SType<E> elementsType;

    public SIList() {
    }

    @SuppressWarnings("unchecked")
    static <I extends SInstance> SIList<I> of(SType<I> elementsType) {
        //        MILista<I> lista = new MILista<>();
        SIList<I> lista = (SIList<I>) elementsType.getDictionary().getType(STypeList.class).newInstance();
        lista.setType(elementsType.getDictionary().getType(STypeList.class));
        lista.elementsType = elementsType;
        return lista;
    }

    @Override
    public STypeList<?, ?> getType() {
        return (STypeList<?, ?>) super.getType();
    }

    @SuppressWarnings("unchecked")
    public SType<E> getElementsType() {
        if (elementsType == null) {
            elementsType = (SType<E>) getType().getElementsType();
        }
        return elementsType;
    }

    @Override
    public List<Object> getValue() {
        if (values == null) {
            return Collections.emptyList();
        }
        return values.stream().map(SInstance::getValue).collect(Collectors.toList());
    }

    @Override
    public void clearInstance() {
        if (values != null) {
            values.forEach(SInstance::clearInstance);
            //TODO - Deveria notificar os listeners de quem depende dele, e não dele próprio.
            if (asAtr().getUpdateListener() != null) {
                asAtr().getUpdateListener().accept(this);
            }
        }
    }
    
    public void removeChildren() {
        if (values != null) {
            values.forEach(SInstance::internalOnRemove);
            values.clear();
        }
    }
    
    @Override
    public final <T> T getValue(String fieldPath, Class<T> resultClass) {
        return getValue(new PathReader(fieldPath), resultClass);
    }

    @Override
    public boolean isEmptyOfData() {
        return isEmpty() || values.stream().allMatch(SInstance::isEmptyOfData);
    }

    public E addNew() {
        E instance = getElementsType().newInstance(getDocument());
        return addInternal(instance, true, -1);
    }

    public E addNew(Consumer<E> consumer) {
        E instance = addNew();
        consumer.accept(instance);
        return instance;
    }

    @SuppressWarnings("unchecked")
    public E addElement(E e) {
        E instance = e;
        instance.setDocument(getDocument());
        return addInternal(instance, true, -1);
    }

    public E addElementAt(int index, E e) {
        E instance = e;
        instance.setDocument(getDocument());
        return addInternal(instance, false, index);
    }

    public E addNewAt(int index) {
        E instance = getElementsType().newInstance(getDocument());
        return addInternal(instance, false, index);
    }

    public E addValue(Object value) {
        E instance = addNew();
        try {
            instance.setValue(value);
        } catch (RuntimeException e) {
            //Senão conseguiu converter o valor, então desfaz a inclusão
            values.remove(values.size() - 1);
            throw e;
        }
        return instance;
    }

    public SIList<E> addValues(Collection<?> values) {
        for (Object valor : values)
            addValue(valor);
        return this;
    }

    private E addInternal(E instance, boolean atEnd, int index) {
        if (values == null) {
            values = new ArrayList<>();
        }
        if (atEnd) {
            values.add(instance);
        } else {
            values.add(index, instance);
        }
        instance.setParent(this);
        instance.init();
        return instance;
    }

    public E get(int index) {
        return getChecking(index, null);
    }

    @Override
    public SInstance getField(String path) {
        return getField(new PathReader(path));
    }

    @Override
    public Optional<SInstance> getFieldOpt(String path) {
        return getFieldOpt(new PathReader(path));
    }

    @Override
    final SInstance getFieldLocal(PathReader pathReader) {
        SInstance instance = getChecking(pathReader);
        if (instance == null) {
            SFormUtil.resolveFieldType(getType(), pathReader);
        }
        return instance;
    }

    @Override
    Optional<SInstance> getFieldLocalOpt(PathReader pathReader) {
        int index = resolveIndex(pathReader);
        if (values != null && index < values.size()) {
            return Optional.ofNullable(values.get(index));
        }
        return Optional.empty();
    }

    @Override
    final SInstance getFieldLocalWithoutCreating(PathReader pathReader) {
        return getChecking(pathReader);
    }

    private E getChecking(PathReader pathReader) {
        return getChecking(resolveIndex(pathReader), pathReader);
    }

    private E getChecking(int index, PathReader pathReader) {
        if (index < 0 || index + 1 > size()) {
            String msg = "índice inválido: " + index + ((index < 0) ? " < 0" : " > que a lista (size= " + size() + ")");
            if (pathReader == null) {
                throw new SingularFormException(msg, this);
            }
            throw new SingularFormException(pathReader.getErroMsg(this, msg));
        }
        return values.get(index);
    }

    private int resolveIndex(PathReader pathReader) {
        if (!pathReader.isIndex()) {
            throw new SingularFormException(pathReader.getErroMsg(this, "Era esperado um indice do elemento (exemplo field[1]), mas em vez disso foi solicitado '" + pathReader.getTrecho() + "'"));
        }
        int index = pathReader.getIndex();
        if (index < 0) {
            throw new SingularFormException(pathReader.getErroMsg(this, index + " é um valor inválido de índice"));
        }
        return index;
    }

    @Override
    public void setValue(Object obj) {
        if (obj instanceof SIList<?>) {
            @SuppressWarnings("unchecked")
            SIList<E> list = (SIList<E>) obj;
            clearInstance();
            Iterator<E> it = list.iterator();
            while (it.hasNext()){
                E o = it.next();
                it.remove();
                addElement(o);
            }
            elementsType = list.getElementsType();
            list.getValue().clear();
        } else if (obj instanceof List) {
            clearInstance();
            for (Object o : (List)obj){
                addValue(o);
            }
        } else {
            throw new SingularFormException("SList só suporta valores de mesmo tipo da lista", this);
        }
    }

    @Override
    public final void setValue(String fieldPath, Object value) {
        setValue(new PathReader(fieldPath), value);
    }

    @Override
    void setValue(PathReader pathReader, Object value) {
        SInstance instance = getChecking(pathReader);
        if (pathReader.isLast()) {
            instance.setValue(value);
        } else {
            instance.setValue(pathReader.next(), value);
        }
    }

    /**
     * Remove o elemento da lista e dispara o
     * pós processamento do elemento da lista (listeners e desassociação do pai)
     * @param index
     * @return
     */
    public E remove(int index) {
        E e = getChecking(index, null);
        values.remove(index);
        return internalRemove(e);
    }

    /**
     * Processa a instancia com as rotinas necessárias após a desassociação
     * do elemento da lista.
     * @param e
     * Instancia cuja remoção deve ser processada
     * @return
     */
    private E internalRemove(E e){
        e.internalOnRemove();
        //TODO - Deveria notificar os listeners de quem depende dele, e não dele próprio.
        if (asAtr().getUpdateListener() != null) {
            asAtr().getUpdateListener().accept(this);
        }
        return e;
    }

    public Object getValueAt(int index) {
        return get(index).getValue();
    }

    /**
     * Retornar o índice da instancia dentro da lista. Utiliza identidade (==)
     * em vez de equals().
     *
     * @param supposedChild
     * @return -1 senão encontrou
     */
    public int indexOf(SInstance supposedChild) {
        for (int i = size() - 1; i != -1; i--) {
            if (values.get(i) == supposedChild) {
                return i;
            }
        }
        return -1;
    }

    public int size() {
        return (values == null) ? 0 : values.size();
    }

    public boolean isEmpty() {
        return (values == null) || values.isEmpty();
    }

    public List<E> getValues() {
        return (values == null) ? Collections.emptyList() : values;
    }

    @Override
    public List<E> getChildren() {
        return getValues();
    }

    @Override
    public Iterator<E> iterator() {
        return (values == null) ? Collections.emptyIterator() : new Iterator<E>() {

            Iterator<E> it = values.iterator();
            E current;

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public E next() {
                return current = it.next();
            }

            @Override
            public void remove() {
                it.remove();
                SIList.this.internalRemove(current);
            }
        };
    }

    @Override
    public Stream<E> stream() {
        return getValues().stream();
    }

    public String toDebug() {
        return stream().map(SInstance::toStringDisplay).collect(Collectors.joining("; "));
    }

    @Override
    public int hashCode() {
        final int prime  = 31;
        int       result = 1;
        result = prime * result + ((elementsType == null) ? 0 : elementsType.hashCode());
        for (E e : this)
            result = prime * result + (e == null ? 0 : e.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SIList<?> other = (SIList<?>) obj;
        if (size() != other.size()) {
            return false;
        } else if (!getType().equals(other.getType())) {
            return false;
        } else if (!Objects.equals(getElementsType(), other.getElementsType()))
            return false;
        for (int i = size() - 1; i != -1; i--) {
            if (!Objects.equals(get(i), other.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return String.format("%s(%s)", getClass().getSimpleName(), getAllChildren());
    }

    public E first() {
        if (hasValues())
            return values.get(0);
        return null;
    }

    public boolean hasValues() {
        return values != null && !values.isEmpty();
    }

    public E last() {
        if (hasValues())
            return values.get(values.size() - 1);
        return null;
    }

    @SuppressWarnings("unchecked")
    public E remove(E e) {
        return (E) remove(values.indexOf(e));
    }
}
