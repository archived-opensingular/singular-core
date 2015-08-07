package br.net.mirante.singular.flow.util.vars;

import java.io.Serializable;

import br.net.mirante.singular.flow.util.props.Props;

public interface VarInstance extends Serializable {

    public VarInstance setValor(Object valor);

    public VarDefinition getDefinicao();

    public Object getValor();

    public String getStringDisplay();

    public String getStringPersistencia();

    public Props props();

    public default <T> T getValor(T defaultValue) {
        T v = (T) getValor();
        return (v == null) ? defaultValue : v;
    }

    public default String getRef() {
        return getDefinicao().getRef();
    }

    public default String getNome() {
        return getDefinicao().getName();
    }

    public default boolean isObrigatorio() {
        return getDefinicao().isRequired();
    }

    public default VarType getTipo() {
        return getDefinicao().getType();
    }

    public void setChangeListner(VarInstanceMap<?> changeListener);

}
