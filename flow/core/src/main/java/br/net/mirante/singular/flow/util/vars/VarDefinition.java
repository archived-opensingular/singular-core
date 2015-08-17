package br.net.mirante.singular.flow.util.vars;

import java.io.Serializable;

public interface VarDefinition extends Serializable{

    public String getRef();

    public String getName();

    public VarType getType();

    public void setRequired(boolean value);

    public VarDefinition required();

    public boolean isRequired();

    public VarDefinition copy();

    public default String toDisplayString(VarInstance varInstance) {
        return getType().toDisplayString(varInstance);
    }

    public default String toDisplayString(Object valor) {
        return getType().toDisplayString(valor, this);
    }

    public default String toPersistenceString(VarInstance varInstance) {
        return getType().toPersistenceString(varInstance);
    }
}
