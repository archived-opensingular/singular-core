package br.net.mirante.singular.flow.util.vars;

import java.io.Serializable;

public interface VarService extends Serializable {

    public VarService deserialize();

    public VarDefinitionMap<?> newVarDefinitionMap();

    // TODO Verifica se ficou em uso no final, senão apagar
    public VarInstance newVarInstance(VarDefinition def);

    public VarDefinition newDefinition(String ref, String name, VarType type);

    public VarDefinition newDefinitionString(String ref, String name, Integer tamanhoMaximo);

    public VarDefinition newDefinitionMultiLineString(String ref, String name, Integer tamanhoMaximo);

    public VarDefinition newDefinitionDate(String ref, String name);

    public VarDefinition newDefinitionInteger(String ref, String name);

    public VarDefinition newDefinitionBoolean(String ref, String name);

    public VarDefinition newDefinitionDouble(String ref, String name);

    public static VarService basic() {
        if (1 == 1)
            throw new RuntimeException("Falta implementar VarServiceBasic.class");
        return BASIC;
    }

    public static VarService getVarService(VarServiceEnabled source) {
        VarService s = source.getVarService();
        if (s == null) {
            if (1 == 1)
                throw new RuntimeException("Falta implementar VarServiceBasic.class");
            return BASIC;
        }
        return s.deserialize();
    }

    public static final VarService BASIC = new VarServiceBasic();

    public static final class VarServiceBasic implements VarService {

        VarServiceBasic() {
        }

        @Override
        public VarService deserialize() {
            return BASIC;
        }

        @Override
        public VarDefinitionMap<?> newVarDefinitionMap() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public VarInstance newVarInstance(VarDefinition def) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public VarDefinition newDefinition(String ref, String name, VarType type) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public VarDefinition newDefinitionString(String ref, String name, Integer tamanhoMaximo) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public VarDefinition newDefinitionMultiLineString(String ref, String name, Integer tamanhoMaximo) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public VarDefinition newDefinitionDate(String ref, String name) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public VarDefinition newDefinitionInteger(String ref, String name) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public VarDefinition newDefinitionBoolean(String ref, String name) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public VarDefinition newDefinitionDouble(String ref, String name) {
            // TODO Auto-generated method stub
            return null;
        }

    }
}
