package br.net.mirante.singular.flow.util.vars.types;

import br.net.mirante.singular.flow.util.vars.VarDefinition;
import br.net.mirante.singular.flow.util.vars.VarInstance;
import br.net.mirante.singular.flow.util.vars.VarType;

import java.math.BigDecimal;

public class VarTypeDecimal implements VarType {

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
        return ((BigDecimal) valor).toPlainString();
    }

    @Override
    public String toPersistenceString(VarInstance varInstance) {
        BigDecimal  valor = (BigDecimal) varInstance.getValor();
        if (valor == null){
            return null;
        }
        return valor.toPlainString();
    }

}
