package br.net.mirante.singular.flow.core;

import java.util.Date;

import br.net.mirante.singular.flow.util.vars.VarDefinitionMap;
import br.net.mirante.singular.flow.util.vars.VarInstance;
import br.net.mirante.singular.flow.util.vars.VarInstanceMap;

public abstract class VariableWrapper {

    private VarInstanceMap<?> variables;

    protected abstract void configVariables(VarDefinitionMap<?> variableDefinitions);

    final void setVariables(VarInstanceMap<?> variables) {
        this.variables = variables;
    }

    final VarInstanceMap<?> getVariables() {
        return variables;
    }

    protected VarInstance getVariable(String ref) {
        return variables.getVariavel(ref);
    }

    protected void setValue(String ref, Object value) {
        variables.setValor(ref, value);
    }

    protected final Date getValorVariavelData(String nomeVariavel) {
        return variables.getValorData(nomeVariavel);
    }

    protected final Boolean getValorVariavelBoolean(String nomeVariavel) {
        return variables.getValorBoolean(nomeVariavel);
    }

    protected final String getValorVariavelString(String nomeVariavel) {
        return variables.getValorString(nomeVariavel);
    }

    protected final Integer getValorVariavelInteger(String nomeVariavel) {
        return variables.getValorInteger(nomeVariavel);
    }

    protected final <T> T getValorVariavelTipo(String nomeVariavel, Class<T> classeTipo) {
        return variables.getValorTipo(nomeVariavel, classeTipo);
    }

    protected final <T> T getValorVariavel(String nomeVariavel) {
        return variables.getValor(nomeVariavel);
    }

    protected final <T> T getValorVariavel(String nomeVariavel, T valorDefault) {
        return variables.getValor(nomeVariavel, valorDefault);
    }
}
