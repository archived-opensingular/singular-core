package br.net.mirante.singular.form.mform;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public interface ICompositeInstance {

    public Collection<? extends MInstancia> getChildren();
    public default Collection<? extends MInstancia> getAllChildren() {
        return getChildren();
    }

    public Stream<? extends MInstancia> stream();

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

    public MInstancia getCampo(String path);

    public default <T extends MInstancia> T getField(String path, Class<T> typeOfInstance) {
        MInstancia instancia = getCampo(path);
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
    public default MIComposto getFieldRecord(String path) {
        MInstancia instancia = getCampo(path);
        if (instancia != null && !(instancia instanceof MIComposto)) {
            throw new RuntimeException("'" + path + "' retornou um instancia da classe " + instancia.getClass().getName()
                + " referente ao tipo " + instancia.getMTipo().getNome() + " em vez de " + MIComposto.class.getName());
        }
        return (MIComposto) instancia;
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
    public default <T extends MInstancia> MILista<T> getFieldList(String path, Class<T> typeOfInstanceElements) {
        MILista<?> lista = getFieldList(path);
        if (lista == null) {
            return null;
        } else if (typeOfInstanceElements.isAssignableFrom(lista.getTipoElementos().getClasseInstancia())) {
            return (MILista<T>) lista;
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
    public default MILista<?> getFieldList(String path) {
        MInstancia instancia = getCampo(path);
        if (instancia != null && !(instancia instanceof MILista)) {
            throw new RuntimeException("'" + path + "' retornou um instancia da classe " + instancia.getClass().getName()
                + " referente ao tipo " + instancia.getMTipo().getNome() + " em vez de " + MILista.class.getName());
        }
        return (MILista<?>) instancia;
    }

    public default String getValorString(String pathCampo) {
        return getValor(pathCampo, String.class);
    }

    public default Integer getValorInteger(String pathCampo) {
        return getValor(pathCampo, Integer.class);
    }

    public default <T extends Enum<T>> T getValorEnum(String pathCampo, Class<T> enumType) {
        // TODO (de Daniel) Esse metodo precisa ser repensado
        String valor = getValorString(pathCampo);
        if (valor != null) {
            return Enum.valueOf(enumType, valor);
        }
        return null;
    }

    public default <D extends MInstancia> D getDescendant(MTipo<D> descendantType) {
        return MInstances.getDescendant((MInstancia) this, descendantType);
    }
    public default <D extends MInstancia> Optional<D> findDescendant(MTipo<D> descendantType) {
        return MInstances.findDescendant((MInstancia) this, descendantType);
    }
    public default <D extends MInstancia> List<D> listDescendants(MTipo<D> descendantType) {
        return MInstances.listDescendants((MInstancia) this, descendantType);
    }
    public default <D extends MInstancia, V> List<V> listDescendants(MTipo<?> descendantType, Function<D, V> function) {
        return MInstances.listDescendants((MInstancia) this, descendantType, function);
    }
    @SuppressWarnings("unchecked")
    public default <V> List<V> listDescendantValues(MTipo<?> descendantType, Class<V> valueType) {
        return MInstances.listDescendants((MInstancia) this, descendantType, node -> (V) node.getValor());
    }
    public default Stream<MInstancia> streamDescendants(boolean includeRoot) {
        return MInstances.streamDescendants((MInstancia) this, includeRoot);
    }
    public default <D extends MInstancia> Stream<D> streamDescendants(MTipo<D> descendantType, boolean includeRoot) {
        return MInstances.streamDescendants((MInstancia) this, includeRoot, descendantType);
    }

}
