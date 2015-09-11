package br.net.mirante.singular.flow.core;

import java.util.List;
import java.util.Objects;

import br.net.mirante.singular.flow.core.entity.IEntityProcessInstance;
import br.net.mirante.singular.flow.core.entity.IEntityVariableInstance;
import br.net.mirante.singular.flow.util.props.MetaDataRef;
import br.net.mirante.singular.flow.util.vars.VarInstance;
import br.net.mirante.singular.flow.util.vars.VarInstanceMapImpl;

public class VarInstanceTableProcess extends VarInstanceMapImpl {

    private static final MetaDataRef<Long> PROP_DB_COD = new MetaDataRef<>("persitence.dbCod", Long.class);

    // TODO transformar o valor abaixo em RefProcessInstance (igual a
    // RefProcessDefinition)
    private ProcessInstance instancia;

    VarInstanceTableProcess(ProcessDefinition<?> definition) {
        super(definition.getVariables());
    }

    VarInstanceTableProcess(ProcessInstance instancia) {
        this(instancia.<ProcessDefinition<?>>getProcessDefinition());
        bind(instancia.getEntity());
        this.instancia = instancia;
    }

    private void bind(IEntityProcessInstance iModelProcessInstance) {
        List<? extends IEntityVariableInstance> variaveis_ = iModelProcessInstance.getVariables();
        if (variaveis_ != null) {
            for (IEntityVariableInstance dadosVariavel : variaveis_) {
                VarInstance v = getVariavel(dadosVariavel.getName());
                if (v == null) {
                    v = addDefinicao(getVarService().newDefinitionString(dadosVariavel.getName(), dadosVariavel.getName(), null));
                }
                v.setValor(dadosVariavel.getValue());
                v.getMetaData().set(PROP_DB_COD, dadosVariavel.getCod());
            }
        }
    }

    boolean isBinded() {
        return instancia != null;
    }

    @Override
    protected boolean wantToKnowAboutChanges() {
        return true;
    }

    @Override
    public void onValueChanged(VarInstance changedVar) {
        if (isBinded()) {
            Long dbCod = changedVar.getMetaData().get(PROP_DB_COD);
            Long dbCod2 = instancia.getPersistenceService().updateVariableValue(instancia.getInternalEntity(), changedVar, dbCod);
            if (!Objects.equals(dbCod, dbCod2)) {
                changedVar.getMetaData().set(PROP_DB_COD, dbCod2);
            }
        }
    }
}
