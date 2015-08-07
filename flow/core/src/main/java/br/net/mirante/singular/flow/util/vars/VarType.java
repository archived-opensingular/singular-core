package br.net.mirante.singular.flow.util.vars;

public interface VarType {

    public String getName();

    public String toDisplayString(VarInstance varInstance);

    public String toDisplayString(Object valor, VarDefinition varDefinition);

    public String toPersistenceString(VarInstance varInstance);
}
