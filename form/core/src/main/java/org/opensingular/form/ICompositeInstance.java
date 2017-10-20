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

    public List<? extends SInstance> getChildren();

    public default List<? extends SInstance> getAllChildren() {
        return getChildren();
    }

    public Stream<? extends SInstance> stream();

    public void setValue(String pathCampo, Object value);

    public abstract <T> T getValue(@Nonnull String fieldPath);

    public <T> T getValue(String fieldPath, Class<T> resultClass);

    public default Optional<Object> getValueOpt(String fieldPath) {
        return getValueOpt(fieldPath, null);
    }

    public default <T> Optional<T> getValueOpt(String fieldPath, Class<T> resultClass) {
        return Optional.ofNullable(getValue(fieldPath, resultClass));
    }

    public default boolean isFieldNull(String fieldPath) {
        // TODO (de Daniel) Esse metodo precisa ser repensado
        return getValue(fieldPath) == null;
    }

    /**
     * Retorna a instancia indicada pelo path fornecido. Não dispara exception
     * se o path não existir no tipo.
     */
    public Optional<SInstance> getFieldOpt(String path);

    /**
     * Retorna a instancia indicada pelo path fornecido. Dispara exception se o
     * path não existir no tipo.
     */
    public SInstance getField(String path);

    public default <T extends SInstance> T getField(String path, Class<T> typeOfInstance) {
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
    public default SIComposite getFieldComposite(String path) {
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
    public default <T extends SInstance> SIList<T> getFieldList(String path, Class<T> typeOfInstanceElements) {
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
    public default SIList<?> getFieldList(String path) {
        SInstance instance = getField(path);
        if (instance != null && !(instance instanceof SIList)) {
            throw new SingularFormException(
                    "'" + path + "' retornou um instancia da classe " + instance.getClass().getName() +
                            " referente ao tipo " + instance.getType().getName() + " em vez de " +
                            SIList.class.getName(), (SInstance) this);
        }
        return (SIList<?>) instance;
    }

    public default String getValueString(String fieldPath) {
        return getValue(fieldPath, String.class);
    }

    public default Long getValueLong(String fieldPath) { return getValue(fieldPath, Long.class);}

    public default Integer getValueInteger(String fieldPath) { return getValue(fieldPath, Integer.class);}

    public default Boolean getValueBoolean(String fieldPath) { return getValue(fieldPath, Boolean.class);}

    public default <T extends Enum<T>> T getValueEnum(String fieldPath, Class<T> enumType) {
        // TODO (de Daniel) Esse metodo precisa ser repensado
        String value = getValueString(fieldPath);
        if (value != null) {
            return Enum.valueOf(enumType, value);
        }
        return null;
    }

    public default <D extends SInstance> D getDescendant(SType<D> descendantType) {
        return SInstances.getDescendant((SInstance) this, descendantType);
    }
    public default <D extends SInstance> Optional<D> findDescendant(SType<D> descendantType) {
        return SInstances.findDescendant((SInstance) this, descendantType);
    }
    public default <D extends SInstance> List<D> listDescendants(SType<D> descendantType) {
        return SInstances.listDescendants((SInstance) this, descendantType);
    }
    public default <D extends SInstance, V> List<V> listDescendants(SType<?> descendantType, Function<D, V> function) {
        return SInstances.listDescendants((SInstance) this, descendantType, function);
    }
    @SuppressWarnings("unchecked")
    public default <V> List<V> listDescendantValues(SType<?> descendantType, Class<V> valueType) {
        return SInstances.listDescendants((SInstance) this, descendantType, node -> (V) node.getValue());
    }
    public default Stream<SInstance> streamDescendants(boolean includeRoot) {
        return SInstances.streamDescendants((SInstance) this, includeRoot);
    }
    public default <D extends SInstance> Stream<D> streamDescendants(SType<D> descendantType, boolean includeRoot) {
        return SInstances.streamDescendants((SInstance) this, includeRoot, descendantType);
    }

}
