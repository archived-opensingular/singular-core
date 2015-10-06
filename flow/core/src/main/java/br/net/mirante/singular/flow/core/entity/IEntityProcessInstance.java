package br.net.mirante.singular.flow.core.entity;

import java.util.Date;
import java.util.List;

import br.net.mirante.singular.flow.core.MUser;

public interface IEntityProcessInstance extends IEntityByCod {

    IEntityProcessVersion getProcess();

    String getDescription();

    void setDescription(String description);

    MUser getUserCreator();

    Date getBeginDate();

    Date getEndDate();

    IEntityTaskInstance getParentTask();

    List<? extends IEntityTaskInstance> getTasks();

    List<? extends IEntityVariableInstance> getVariables();

    List<? extends IEntityExecutionVariable> getHistoricalVariables();

    List<? extends IEntityRole> getRoles();


    default IEntityRole getRoleUserByAbbreviation(String roleAbbreviation) {
        for (IEntityRole dadosPapelInstancia : getRoles()) {
            if (roleAbbreviation.equalsIgnoreCase(dadosPapelInstancia.getRole().getAbbreviation())) {
                return dadosPapelInstancia;
            }
        }
        return null;
    }

    default IEntityVariableInstance getVariable(String ref) {
        return getVariables().stream().filter(var -> var.getName().equalsIgnoreCase(ref)).findAny().orElse(null);
    }
}
