package br.net.mirante.singular.flow.core.entity;

import java.util.Date;
import java.util.List;

import br.net.mirante.singular.flow.core.MUser;

public interface IEntityTaskInstance extends IEntityByCod {

    IEntityProcessInstance getProcessInstance();

    IEntityTaskVersion getTask();

    Date getBeginDate();

    void setBeginDate(Date begin);

    Date getEndDate();

    void setEndDate(Date end);

    Date getTargetEndDate();

    void setTargetEndDate(Date targetEndDate);

    void setAllocatedUser(MUser allocatedUser);

    MUser getAllocatedUser();

    void setResponsibleUser(MUser responsibleUser);

    MUser getResponsibleUser();

    void setSuspensionTargetDate(Date suspensionTargetDate);

    IEntityTaskTransitionVersion getExecutedTransition();

    void setExecutedTransition(IEntityTaskTransitionVersion transition);

    List<? extends IEntityExecutionVariable> getInputVariables();

    List<? extends IEntityExecutionVariable> getOutputVariables();

    List<? extends IEntityTaskInstanceHistory> getTaskHistoric();

    List<? extends IEntityProcessInstance> getChildProcesses();

    default boolean isActive() {
        return getEndDate() == null;
    }

    default boolean isFinished() {
        return getEndDate() != null;
    }
}
