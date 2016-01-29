package br.net.mirante.singular.form.mform;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public interface ICompositeInstance {

    public Collection<? extends SInstance2> getChildren();
    public default Collection<? extends SInstance2> getAllChildren() {
        return getChildren();
    }

    public Stream<? extends SInstance2> stream();

    public void setValor(String pathCampo, Object valor);

    public default Object getValor(String pathCampo) {
        return getValor(pathCampo, null);
    }

    public <T extends Object> T getValor(String pathCampo, Class<T> classeDestino);

    public default Optional<Object> getValorOpt(String pathCampo) {
        return getValorOpt(pathCampo, null);
    }

    public default <T extends Object> Optional<T> getValorOpt(String pathCampo, Class<T> classeDestino) {
        return Optional.ofNullable(getValor(pathCampo, classeDestino));
    }

    public default boolean isCampoNull(String pathCampo) {
        // TODO (de Daniel) Esse metodo precisa ser repensado
        return getValor(pathCampo) == null;
    }

    public SInstance2 getCampo(String path);

    public default <T extends SInstance2> T getField(String path, Class<T> typeOfInstance) {
        SInstance2 instancia = getCampo(path);
        if (instancia == null) {
            return null;
        } else if (typeOfInstance.isInstance(instancia)) {
            return typeOfInstance.cast(instancia);
        }
        throw new RuntimeException("'" + path + "' + retornou uma instancia do tipo " + instancia.getClass().getName()
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
        SInstance2 instancia = getCampo(path);
        if (instancia != null && !(instancia instanceof SIComposite)) {
            throw new RuntimeException("'" + path + "' retornou um instancia da classe " + instancia.getClass().getName()
                + " referente ao tipo " + instancia.getMTipo().getNome() + " em vez de " + SIComposite.class.getName());
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
    public default <T extends SInstance2> SList<T> getFieldList(String path, Class<T> typeOfInstanceElements) {
        SList<?> lista = getFieldList(path);
        if (lista == null) {
            return null;
        } else if (typeOfInstanceElements.isAssignableFrom(lista.getTipoElementos().getClasseInstancia())) {
            return (SList<T>) lista;
        }
        throw new RuntimeException(
            "'" + path + "' + retornou uma lista cujos as instancia do tipo " + lista.getTipoElementos().getClasseInstancia().getName()
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
    public default SList<?> getFieldList(String path) {
        SInstance2 instancia = getCampo(path);
        if (instancia != null && !(instancia instanceof SList)) {
            throw new RuntimeException("'" + path + "' retornou um instancia da classe " + instancia.getClass().getName()
                + " referente ao tipo " + instancia.getMTipo().getNome() + " em vez de " + SList.class.getName());
        }
        return (SList<?>) instancia;
    }

    public default String getValorString(String pathCampo) {
        return getValor(pathCampo, String.class);
    }

    public default Integer getValorInteger(String pathCampo) { return getValor(pathCampo, Integer.class);}

    public default Boolean getValorBoolean(String pathCampo) { return getValor(pathCampo, Boolean.class);}

    public default <T extends Enum<T>> T getValorEnum(String pathCampo, Class<T> enumType) {
        // TODO (de Daniel) Esse metodo precisa ser repensado
        String valor = getValorString(pathCampo);
        if (valor != null) {
            return Enum.valueOf(enumType, valor);
        }
        return null;
    }

    public default <D extends SInstance2> D getDescendant(SType<D> descendantType) {
        return MInstances.getDescendant((SInstance2) this, descendantType);
    }
    public default <D extends SInstance2> Optional<D> findDescendant(SType<D> descendantType) {
        return MInstances.findDescendant((SInstance2) this, descendantType);
    }
    public default <D extends SInstance2> List<D> listDescendants(SType<D> descendantType) {
        return MInstances.listDescendants((SInstance2) this, descendantType);
    }
    public default <D extends SInstance2, V> List<V> listDescendants(SType<?> descendantType, Function<D, V> function) {
        return MInstances.listDescendants((SInstance2) this, descendantType, function);
    }
    @SuppressWarnings("unchecked")
    public default <V> List<V> listDescendantValues(SType<?> descendantType, Class<V> valueType) {
        return MInstances.listDescendants((SInstance2) this, descendantType, node -> (V) node.getValor());
    }
    public default Stream<SInstance2> streamDescendants(boolean includeRoot) {
        return MInstances.streamDescendants((SInstance2) this, includeRoot);
    }
    public default <D extends SInstance2> Stream<D> streamDescendants(SType<D> descendantType, boolean includeRoot) {
        return MInstances.streamDescendants((SInstance2) this, includeRoot, descendantType);
    }

}
