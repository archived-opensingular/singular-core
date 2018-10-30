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
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public interface ICompositeInstance {

    @Nonnull
    List<? extends SInstance> getChildren();

    @Nonnull
    default List<? extends SInstance> getAllChildren() {
        return getChildren();
    }

    @Nonnull
    Stream<? extends SInstance> stream();

    void setValue(String fieldPath, Object value);

    <T> T getValue(@Nonnull String fieldPath);

    <T> T getValue(String fieldPath, Class<T> resultClass);

    default Optional<Object> getValueOpt(String fieldPath) {
        return getValueOpt(fieldPath, null);
    }

    default <T> Optional<T> getValueOpt(String fieldPath, Class<T> resultClass) {
        return Optional.ofNullable(getValue(fieldPath, resultClass));
    }

    default boolean isFieldNull(String fieldPath) {
        // TODO (de Daniel) Esse metodo precisa ser repensado
        return getValue(fieldPath) == null;
    }

    /**
     * Retorna a instancia indicada pelo path fornecido. Não dispara exception
     * se o path não existir no tipo.
     */
    @Nonnull
    Optional<SInstance> getFieldOpt(String path);

    /**
     * Retorna a instancia indicada pelo path fornecido. Dispara exception se o
     * path não existir no tipo.
     */
    SInstance getField(String path);

    default <T extends SInstance> T getField(String path, Class<T> typeOfInstance) {
        SInstance instance = getField(path);
        if (instance == null) {
            return null;
        } else if (typeOfInstance.isInstance(instance)) {
            return typeOfInstance.cast(instance);
        }
        throw new SingularFormException("'" + path + "' + retornou uma instancia do tipo " + instance.getClass().getName()
                + ", que não é compatível com o tipo solicitado " + typeOfInstance.getName(), instance);
    }

    /**
     * Retorna um campo no path indicado com sendo uma registro composto .
     * Dispara uma exception se o path indicado não existir na estrutura de
     * dados ou se não for um registro composto.
     *
     * @return Null se o campo no path indicado não tiver sido instanciado
     *         ainda.
     */
    default SIComposite getFieldComposite(String path) {
        SInstance instance = getField(path);
        if (instance != null && !(instance instanceof SIComposite)) {
            throw new SingularFormException(
                    "'" + path + "' retornou um instancia da classe " + instance.getClass().getName() +
                            " referente ao tipo " + instance.getType().getName() + " em vez de " +
                            SIComposite.class.getName(), (SInstance) this);
        }
        return (SIComposite) instance;
    }

    /**
     * Retorna um campo no path indicado com sendo uma lista e cujo os elementos
     * da intancia são do tipo informando. Dispara uma exception se o path
     * indicado não existir na estrutura de dados ou se não for uma lista ou se
     * a lista não for da instância definida.
     *
     * @return Null se o campo no path indicado não tiver sido instanciado
     *         ainda.
     */
    @SuppressWarnings("unchecked")
    default <T extends SInstance> SIList<T> getFieldList(String path, Class<T> typeOfInstanceElements) {
        SIList<?> list = getFieldList(path);
        if (list == null) {
            return null;
        } else if (typeOfInstanceElements.isAssignableFrom(list.getElementsType().getInstanceClass())) {
            return (SIList<T>) list;
        }
        throw new SingularFormException("'" + path + "' + retornou uma lista cujos as instancia do tipo " +
                list.getElementsType().getInstanceClass().getName() + ", que não é compatível com o tipo solicitado " +
                typeOfInstanceElements.getName(), (SInstance) this);
    }

    /**
     * Retorna um campo no path indicado com sendo uma lista . Dispara uma
     * exception se o path indicado não existir na estrutura de dados ou se não
     * for uma lista.
     *
     * @return Null se o campo no path indicado não tiver sido instanciado
     *         ainda.
     */
    default SIList<?> getFieldList(String path) {
        SInstance instance = getField(path);
        if (instance != null && !(instance instanceof SIList)) {
            throw new SingularFormException(
                    "'" + path + "' retornou um instancia da classe " + instance.getClass().getName() +
                            " referente ao tipo " + instance.getType().getName() + " em vez de " +
                            SIList.class.getName(), (SInstance) this);
        }
        return (SIList<?>) instance;
    }

    default String getValueString(String fieldPath) {
        return getValue(fieldPath, String.class);
    }

    default Long getValueLong(String fieldPath) { return getValue(fieldPath, Long.class);}

    default Integer getValueInteger(String fieldPath) { return getValue(fieldPath, Integer.class);}

    default Boolean getValueBoolean(String fieldPath) { return getValue(fieldPath, Boolean.class);}

    default <T extends Enum<T>> T getValueEnum(String fieldPath, Class<T> enumType) {
        // TODO (de Daniel) Esse metodo precisa ser repensado
        String value = getValueString(fieldPath);
        if (value != null) {
            return Enum.valueOf(enumType, value);
        }
        return null;
    }

    default <D extends SInstance> D getDescendant(SType<D> descendantType) {
        return SInstances.getDescendant((SInstance) this, descendantType);
    }
    default <D extends SInstance> Optional<D> findDescendant(SType<D> descendantType) {
        return SInstances.findDescendant((SInstance) this, descendantType);
    }
    default <D extends SInstance> List<D> listDescendants(SType<D> descendantType) {
        return SInstances.listDescendants((SInstance) this, descendantType);
    }
    default <D extends SInstance, V> List<V> listDescendants(SType<?> descendantType, Function<D, V> function) {
        return SInstances.listDescendants((SInstance) this, descendantType, function);
    }
    @SuppressWarnings("unchecked")
    default <V> List<V> listDescendantValues(SType<?> descendantType, Class<V> valueType) {
        return SInstances.listDescendants((SInstance) this, descendantType, node -> (V) node.getValue());
    }
    default Stream<SInstance> streamDescendants(boolean includeRoot) {
        return SInstances.streamDescendants((SInstance) this, includeRoot);
    }
    default <D extends SInstance> Stream<D> streamDescendants(SType<D> descendantType, boolean includeRoot) {
        return SInstances.streamDescendants((SInstance) this, includeRoot, descendantType);
    }

}
