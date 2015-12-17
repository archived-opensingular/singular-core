package br.net.mirante.singular.flow.core.variable;

import br.net.mirante.singular.flow.core.SingularFlowException;

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

    @Override
    public void onValueChanged(VarInstance changedVar) {
        throw new SingularFlowException("Método não suportado");
    }
}
