package br.net.mirante.singular.flow.core.entity;

import java.util.Date;
import java.util.List;

import br.net.mirante.singular.flow.core.MUser;

public interface IEntityProcessInstance extends IEntityByCod {
    
    IEntityProcess getProcess();

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

    @Deprecated//Remover do CORE
    IEntityTask getSituacao();

    default IEntityRole getRoleUserByAbbreviation(String roleAbbreviation) {
        for (IEntityRole dadosPapelInstancia : getRoles()) {
            if (roleAbbreviation.equalsIgnoreCase(dadosPapelInstancia.getRole().getAbbreviation())) {
                return dadosPapelInstancia;
            }
        }
        return null;
    }

    default IEntityTaskInstance getTaskByAbbreviation(String taskAbbreviation) {
        return getTask(getProcess().getTask(taskAbbreviation));
    }

    default IEntityTaskInstance getTask(IEntityTask entityTask) {
        List<? extends IEntityTaskInstance> tarefas = getTasks();
        for (int i = tarefas.size() - 1; i > -1; i--) {
            if (tarefas.get(i).getTask().equals(entityTask)) {
                return tarefas.get(i);
            }
        }
        return null;
    }

    default IEntityTaskInstance getCurrentTask(IEntityTask entityTask) {
        List<? extends IEntityTaskInstance> lista = getTasks();
        for (int i = lista.size() - 1; i != -1; i--) {
            IEntityTaskInstance tarefa = lista.get(i);
            if (tarefa.getEndDate() == null && tarefa.getTask().equals(entityTask)) {
                return tarefa;
            }
        }
        return null;
    }

    default IEntityTaskInstance getCurrentTask() {
        List<? extends IEntityTaskInstance> lista = getTasks();
        for (int i = lista.size() - 1; i != -1; i--) {
            IEntityTaskInstance tarefa = lista.get(i);
            if (tarefa.getEndDate() == null) {
                return tarefa;
            }
        }
        return null;
    }
    
    default IEntityVariableInstance getVariable(String ref) {
        return getVariables().stream().filter(var -> var.getName().equalsIgnoreCase(ref)).findAny().orElse(null);
    }
}
