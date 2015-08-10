package br.net.mirante.singular.flow.util.vars;

import java.io.Serializable;

public interface VarService extends Serializable {

    VarService deserialize();

    VarDefinitionMap<?> newVarDefinitionMap();

    /* TODO Verifica se ficou em uso no final, senão apagar */
    VarInstance newVarInstance(VarDefinition def);

    VarDefinition newDefinition(String ref, String name, VarType type);

    VarDefinition newDefinitionString(String ref, String name, Integer tamanhoMaximo);

    VarDefinition newDefinitionMultiLineString(String ref, String name, Integer tamanhoMaximo);

    VarDefinition newDefinitionDate(String ref, String name);

    VarDefinition newDefinitionInteger(String ref, String name);

    VarDefinition newDefinitionBoolean(String ref, String name);

    VarDefinition newDefinitionDouble(String ref, String name);

    static VarService basic() {
        throw new UnsupportedOperationException("Falta implementar VarServiceBasic.class");
    }

    static VarService getVarService(VarServiceEnabled source) {
        VarService s = source.getVarService();
        if (s == null) {
            throw new UnsupportedOperationException("Falta implementar VarServiceBasic.class");
        }
        return s.deserialize();
    }

    VarService BASIC = new VarServiceBasic();

    final class VarServiceBasic implements VarService {

        VarServiceBasic() {
            /* CONSTRUTOR VAZIO */
        }

        @Override
        public VarService deserialize() {
            return BASIC;
        }

        @Override
        public VarDefinitionMap<?> newVarDefinitionMap() {
            return null;
        }

        @Override
        public VarInstance newVarInstance(VarDefinition def) {
            return null;
        }

        @Override
        public VarDefinition newDefinition(String ref, String name, VarType type) {
            return null;
        }

        @Override
        public VarDefinition newDefinitionString(String ref, String name, Integer tamanhoMaximo) {
            return null;
        }

        @Override
        public VarDefinition newDefinitionMultiLineString(String ref, String name, Integer tamanhoMaximo) {
            return null;
        }

        @Override
        public VarDefinition newDefinitionDate(String ref, String name) {
            return null;
        }

        @Override
        public VarDefinition newDefinitionInteger(String ref, String name) {
            return null;
        }

        @Override
        public VarDefinition newDefinitionBoolean(String ref, String name) {
            return null;
        }

        @Override
        public VarDefinition newDefinitionDouble(String ref, String name) {
            return null;
        }
    }
}
