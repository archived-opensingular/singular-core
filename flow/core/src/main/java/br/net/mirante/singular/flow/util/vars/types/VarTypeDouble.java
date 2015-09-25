package br.net.mirante.singular.flow.util.vars.types;

import br.net.mirante.singular.flow.util.vars.VarDefinition;
import br.net.mirante.singular.flow.util.vars.VarInstance;
import br.net.mirante.singular.flow.util.vars.VarType;

import java.util.Locale;

public class VarTypeDouble implements VarType {

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
        return String.format(new Locale("pt","BR"), "%1$,.2f", valor);
    }

    @Override
    public String toPersistenceString(VarInstance varInstance) {
        return Double.toString((Double) varInstance.getValor());
    }
}
