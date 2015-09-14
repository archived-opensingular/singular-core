package br.net.mirante.singular.flow.util.vars;

import br.net.mirante.singular.flow.util.vars.types.VarTypeBoolean;
import br.net.mirante.singular.flow.util.vars.types.VarTypeDate;
import br.net.mirante.singular.flow.util.vars.types.VarTypeDouble;
import br.net.mirante.singular.flow.util.vars.types.VarTypeString;

public class DefaultVarService implements VarService {

    public static DefaultVarService DEFAULT_VAR_SERVICE = new DefaultVarService();

    @Override
    public VarService deserialize() {
        return DEFAULT_VAR_SERVICE;
    }

    @Override
    public VarDefinitionMap<?> newVarDefinitionMap() {
        return new DefaultVarDefinitionMap(this);
    }

    @Override
    public VarInstance newVarInstance(VarDefinition def) {
        return new AbstractVarInstance(def) {

            Object value;

            @Override
            public VarInstance setValor(Object valor) {
                this.value = valor;
                return this;
            }

            @Override
            public Object getValor() {
                return value;
            }
        };
    }

    @Override
    public VarDefinition newDefinition(String ref, String name, VarType type) {
        return null;
    }

    @Deprecated
    @Override
    public VarDefinition newDefinitionString(String ref, String name, @Deprecated Integer tamanhoMaximo) {
        return new VarDefinitionImpl(ref, name, new VarTypeString(), false);
    }

    public VarDefinition newDefinitionString(String ref, String name) {
        return new VarDefinitionImpl(ref, name, new VarTypeString(), false);
    }

    /**
     *
     * @param ref
     * @param name
     * @param tamanhoMaximo
     * @return
     * @deprecated não utilizar pois mistura apresentacao com definicao do fluxo
     */
    @Deprecated
    @Override
    public VarDefinition newDefinitionMultiLineString(String ref, String name, Integer tamanhoMaximo) {
        return newDefinitionString(ref, name, tamanhoMaximo);
    }

    @Override
    public VarDefinition newDefinitionDate(String ref, String name) {
        return new VarDefinitionImpl(ref, name, new VarTypeDate(), false);
    }

    @Override
    public VarDefinition newDefinitionInteger(String ref, String name) {
        return null;
//        return new VarDefinitionImpl(ref, name, new VarTypeInteger(), false);
    }

    @Override
    public VarDefinition newDefinitionBoolean(String ref, String name) {
        return new VarDefinitionImpl(ref, name, new VarTypeBoolean(), false);
    }

    @Override
    public VarDefinition newDefinitionDouble(String ref, String name) {
        return new VarDefinitionImpl(ref, name, new VarTypeDouble(), false);
    }
}
