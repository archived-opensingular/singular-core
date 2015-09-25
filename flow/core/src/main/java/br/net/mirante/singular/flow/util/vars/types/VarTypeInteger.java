package br.net.mirante.singular.flow.util.vars.types;

import br.net.mirante.singular.flow.util.vars.VarDefinition;
import br.net.mirante.singular.flow.util.vars.VarInstance;
import br.net.mirante.singular.flow.util.vars.VarType;

public class VarTypeInteger implements VarType {

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public String toDisplayString(VarInstance varInstance) {
        return toDisplayString(varInstance.getValor(), varInstance.getDefinicao());
    }

    @Override
    public String toDisplayString(Object valor, VarDefinition varDefinition) {
        return Integer.toString((Integer) valor);
    }

    @Override
    public String toPersistenceString(VarInstance varInstance) {
        return Integer.toString((Integer) varInstance.getValor());
    }
}
