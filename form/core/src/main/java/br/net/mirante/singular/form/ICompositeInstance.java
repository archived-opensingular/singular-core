/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form;

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

    public void setValue(String pathCampo, Object valor);

    public default Object getValue(String fieldPath) {
        return getValue(fieldPath, null);
    }

    public <T extends Object> T getValue(String fieldPath, Class<T> resultClass);

    public default Optional<Object> getValueOpt(String fieldPath) {
        return getValueOpt(fieldPath, null);
    }

    public default <T extends Object> Optional<T> getValueOpt(String fieldPath, Class<T> resultClass) {
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
        SInstance instancia = getField(path);
        if (instancia == null) {
            return null;
        } else if (typeOfInstance.isInstance(instancia)) {
            return typeOfInstance.cast(instancia);
        }
        throw new SingularFormException("'" + path + "' + retornou uma instancia do tipo " + instancia.getClass().getName()
                + ", que não é compatível com o tipo solicitado " + typeOfInstance.getName());
    }

    /**
     * Retorna um campo no path indicado com sendo uma registro composto .
     * Dispara uma exception se o path indicado não existir na estrutura de
     * dados ou se não for um registro composto.
     *
     * @return Null se o campo no path indicado não tiver sido instanciado
     *         ainda.
     */
    public default SIComposite getFieldRecord(String path) {
        SInstance instancia = getField(path);
        if (instancia != null && !(instancia instanceof SIComposite)) {
            throw new RuntimeException("'" + path + "' retornou um instancia da classe " + instancia.getClass().getName()
                + " referente ao tipo " + instancia.getType().getName() + " em vez de " + SIComposite.class.getName());
        }
        return (SIComposite) instancia;
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
        SIList<?> lista = getFieldList(path);
        if (lista == null) {
            return null;
        } else if (typeOfInstanceElements.isAssignableFrom(lista.getElementsType().getInstanceClass())) {
            return (SIList<T>) lista;
        }
        throw new RuntimeException(
            "'" + path + "' + retornou uma lista cujos as instancia do tipo " + lista.getElementsType().getInstanceClass().getName()
                        + ", que não é compatível com o tipo solicitado " + typeOfInstanceElements.getName());
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
        SInstance instancia = getField(path);
        if (instancia != null && !(instancia instanceof SIList)) {
            throw new RuntimeException("'" + path + "' retornou um instancia da classe " + instancia.getClass().getName()
                + " referente ao tipo " + instancia.getType().getName() + " em vez de " + SIList.class.getName());
        }
        return (SIList<?>) instancia;
    }

    public default String getValueString(String fieldPath) {
        return getValue(fieldPath, String.class);
    }

    public default Integer getValueInteger(String fieldPath) { return getValue(fieldPath, Integer.class);}

    public default Boolean getValueBoolean(String fieldPath) { return getValue(fieldPath, Boolean.class);}

    public default <T extends Enum<T>> T getValueEnum(String fieldPath, Class<T> enumType) {
        // TODO (de Daniel) Esse metodo precisa ser repensado
        String valor = getValueString(fieldPath);
        if (valor != null) {
            return Enum.valueOf(enumType, valor);
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
