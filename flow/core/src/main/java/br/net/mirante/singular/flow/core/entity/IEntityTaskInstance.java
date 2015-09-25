package br.net.mirante.singular.flow.core.entity;

import java.util.Date;
import java.util.List;

import br.net.mirante.singular.flow.core.MUser;

public interface IEntityTaskInstance extends IEntityByCod {

    IEntityProcessInstance getProcessInstance();

    IEntityTask getTask();

    Date getBeginDate();

    Date getEndDate();

    Date getTargetEndDate();

    void setTargetEndDate(Date targetEndDate);
    
    MUser getAllocatedUser();

    MUser getResponsibleUser();

    IEntityTaskTransition getExecutedTransition();
    
    List<? extends IEntityExecutionVariable> getInputVariables();

    List<? extends IEntityExecutionVariable> getOutputVariables();
    
    List<? extends IEntityTaskInstanceHistory> getTaskHistoric();

    List<? extends IEntityProcessInstance> getChildProcesses();

}
