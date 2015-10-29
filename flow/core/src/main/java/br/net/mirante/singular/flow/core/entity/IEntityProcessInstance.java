package br.net.mirante.singular.flow.core.entity;

import java.util.Date;
import java.util.List;

import br.net.mirante.singular.flow.core.MUser;

public interface IEntityProcessInstance extends IEntityByCod {

    IEntityProcessVersion getProcessVersion();

    String getDescription();

    void setDescription(String description);

    MUser getUserCreator();

    Date getBeginDate();

    void setBeginDate(Date beginDate);

    Date getEndDate();

    void setEndDate(Date end);

    IEntityTaskInstance getParentTask();

    void setParentTask(IEntityTaskInstance parent);

    void addTask(IEntityTaskInstance taskInstance);

    List<? extends IEntityTaskInstance> getTasks();

    List<? extends IEntityVariableInstance> getVariables();

    List<? extends IEntityExecutionVariable> getHistoricalVariables();

    List<? extends IEntityRoleInstance> getRoles();

    default IEntityRoleInstance getRoleUserByAbbreviation(String roleAbbreviation) {
        for (IEntityRoleInstance dadosPapelInstancia : getRoles()) {
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
