package br.net.mirante.singular.flow.util.vars;

public class VarInstanceMapImpl extends AbstractVarInstanceMap<VarInstance> {

    public VarInstanceMapImpl(VarService varService) {
        super(varService);
    }

    public VarInstanceMapImpl(VarInstanceMap<?> instances) {
        super(instances);
    }

    public VarInstanceMapImpl(VarDefinitionMap<?> definitions) {
        super(definitions);
    }

    @Override
    protected VarInstance newVarInstance(VarDefinition def) {
        return getVarService().newVarInstance(def);
    }
}
