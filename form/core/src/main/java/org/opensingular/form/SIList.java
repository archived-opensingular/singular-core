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

import org.opensingular.form.internal.PathReader;
import org.opensingular.form.util.transformer.Value;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
        SDictionary dictionary = elementsType.getDictionary();
        STypeList<?, ?> type = dictionary.getType(STypeList.class);
        SIList<I> list = (SIList<I>) type.newInstance();
        list.elementsType = elementsType;
        return list;
    }

    @Override
    public STypeList<?, ?> getType() {
        return (STypeList<?, ?>) super.getType();
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public SType<E> getElementsType() {
        if (elementsType == null) {
            elementsType = (SType<E>) getType().getElementsType();
            if (elementsType == null) {
                throw new SingularFormException(
                        "Internal Erro: the list doesn't have the type of its elements definided", this);
            }
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
            values.forEach(SInstance::internalOnRemove);
            values.clear();
            invokeUpdateListeners();
        }
    }

    @Override
    public void removeChildren() {
        clearInstance();
    }

    @Override
    public boolean isEmptyOfData() {
        return isEmpty() || values.stream().allMatch(SInstance::isEmptyOfData);
    }

    /**
     * Add a new element instance to the list of type definided in {@link STypeList#getElementsType()}.
     */
    @Nonnull
    public E addNew() {
        return addNewInternal(null, true, -1);
    }

    /**
     * Add a new element instance to the list of the specific {@link SType}. The type informed must be compatible with
     * the list's element type, i.e., it must be the same or derived of {@link STypeList#getElementsType()}.
     */
    @Nonnull
    public <T extends SType<I>, I extends E> I addNew(@Nonnull Class<T> derivedElementTypeClass) {
        T derivedElementType = null;
        if (getElementsType().getClass() != derivedElementTypeClass) {

            derivedElementType = getType().findOrCreateExtendedType(derivedElementTypeClass, getElementsType());
        }
        return (I) addNewInternal(derivedElementType, true, -1);
    }

    @Nonnull
    public E addNewAt(int index) {
        return addNewInternal(null, false, index);
    }

    @Nonnull
    private <T extends SType<I>, I extends E> E addNewInternal(@Nullable T derivedElementType, boolean atEnd,
            int index) {
        E instance;
        if (derivedElementType == null) {
            instance = getElementsType().newInstance(getDocument());
        } else {
            instance = derivedElementType.newInstance(getDocument());
        }

        addInternal(instance, atEnd, index);
        instance.init();
        return instance;
    }

    @Nonnull
    public E addNew(@Nonnull Consumer<E> consumer) {
        E instance = addNew();
        consumer.accept(instance);
        return instance;
    }

    /**
     * Adiciona a instância informada. Se a mesma não for do mesmo documento da lista, faz uma copia dos valores. Se for
     * do mesmo documento da lista, então move.
     */
    @Nonnull
    public E addElement(@Nonnull E e) {
        return addElementInternal(e, true, -1);
    }

    /**
     * Adiciona a instância informada na posição indicada. Se a mesma não for do mesmo documento da lista, faz uma copia
     * dos valores. Se for
     * do mesmo documento da lista, então move.
     */
    @Nonnull
    public E addElementAt(int index, @Nonnull E e) {
        return addElementInternal(e, false, index);
    }

    @Nonnull
    private E addElementInternal(@Nonnull E instance, boolean atEnd, int index) {
        if (instance.getDocument() == getDocument()) {
            return addInternal(instance, atEnd, index);
        } else {
            E copy = getElementsType().newInstance(getDocument());
            Value.copyValues(instance, copy);
            return addInternal(copy, atEnd, index);
        }
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
        values.forEach(v -> addValue(v));
        return this;
    }

    @Nonnull
    private E addInternal(@Nonnull E instance, boolean atEnd, int index) {
        if (values == null) {
            values = new ArrayList<>();
        }
        if (atEnd) {
            values.add(instance);
        } else {
            values.add(index, instance);
        }
        instance.setParent(this);
        invokeUpdateListeners();
        return instance;
    }

    public E get(int index) {
        return getChecking(index, null);
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
            throw new SingularFormException(pathReader.getErrorMsg(this, msg), this);
        }
        return values.get(index);
    }

    private int resolveIndex(PathReader pathReader) {
        if (!pathReader.isIndex()) {
            throw new SingularFormException(pathReader.getErrorMsg(this, "Era esperado um indice do elemento (exemplo field[1]), mas em vez disso foi solicitado '" + pathReader.getToken() + "'"), this);
        }
        int index = pathReader.getIndex();
        if (index < 0) {
            throw new SingularFormException(pathReader.getErrorMsg(this, index + " é um valor inválido de índice"), this);
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
            ((List<?>) obj).stream().forEach(o -> addValue(o));
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
     */
    public E remove(int index) {
        E e = getChecking(index, null);
        values.remove(index);
        return internalRemove(e);
    }

    /**
     * Processa a instancia com as rotinas necessárias após a desassociação
     * do elemento da lista.
     * @param e Instancia cuja remoção deve ser processada
     */
    private E internalRemove(E e){
        e.internalOnRemove();
        invokeUpdateListeners();
        return e;
    }

    private void invokeUpdateListeners(){
        if (!getDocument().isRestoreMode()) {
            for (SType<?> type : this.getType().getDependentTypes()) {
                SInstance dependentInstance = (SInstance) this.findNearest(type).orElse(null);
                if (dependentInstance != null && dependentInstance.asAtr().getUpdateListener() != null) {
                    dependentInstance.asAtr().getUpdateListener().accept(dependentInstance);
                }
            }
        }
    }

    public Object getValueAt(int index) {
        return get(index).getValue();
    }

    /**
     * Retornar o índice da instancia dentro da lista. Utiliza identidade (==)
     * em vez de equals().
     *
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
    public void forEach(@Nonnull Consumer<? super E> action) {
        getChildren().forEach(action);
    }

    @Override
    public Iterator<E> iterator() {
        return (values == null) ? Collections.emptyIterator() : new Iterator<E>() {

            final Iterator<E> it = values.iterator();
            E current;

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public E next() {
                current = it.next();
                return current;
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
        if (obj == null || getClass() != obj.getClass())
            return false;
        SIList<?> other = (SIList<?>) obj;
        if (size() != other.size() || !getType().equals(other.getType()) || !Objects.equals(getElementsType(),
                other.getElementsType())) {
            return false;
        }
        for (int i = size() - 1; i != -1; i--) {
            if (!Objects.equals(get(i), other.get(i))) {
                return false;
            }
        }
        return true;
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

    public E remove(E e) {
        return remove(values.indexOf(e));
    }


    @Override
    public String toStringDisplayDefault() {
        return Optional
                .ofNullable(this.values)
                .orElse(new ArrayList<>())
                .stream()
                .map(SInstance::toStringDisplay)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(", "));
    }
}
