package br.net.mirante.singular.flow.core.variable;

import java.io.Serializable;

import br.net.mirante.singular.flow.core.property.MetaData;

/**
 * @deprecated traduzir o nome dos métodos para ingles
 */
@Deprecated
//TODO marcar a variável quando esta for utilizada. Essa interface deve obrigar a implementacao de um metodo para essa verificacao
public interface VarInstance extends Serializable {

    VarInstance setValor(Object valor);

    VarDefinition getDefinicao();

    Object getValor();

    String getStringDisplay();

    String getStringPersistencia();

    MetaData getMetaData();

    @SuppressWarnings("unchecked")
    default <T> T getValor(T defaultValue) {
        T v = (T) getValor();
        return (v == null) ? defaultValue : v;
    }

    default String getRef() {
        return getDefinicao().getRef();
    }

    default String getNome() {
        return getDefinicao().getName();
    }

    default boolean isObrigatorio() {
        return getDefinicao().isRequired();
    }

    default VarType getTipo() {
        return getDefinicao().getType();
    }

    void setChangeListner(VarInstanceMap<?> changeListener);
}
