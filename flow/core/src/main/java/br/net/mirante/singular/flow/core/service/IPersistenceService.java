package br.net.mirante.singular.flow.core.service;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import br.net.mirante.singular.flow.core.MUser;
import br.net.mirante.singular.flow.core.TaskHistoricLog;
import br.net.mirante.singular.flow.core.TaskInstance;
import br.net.mirante.singular.flow.core.entity.IEntityByCod;
import br.net.mirante.singular.flow.core.entity.IEntityCategory;
import br.net.mirante.singular.flow.core.entity.IEntityProcess;
import br.net.mirante.singular.flow.core.entity.IEntityProcessDefinition;
import br.net.mirante.singular.flow.core.entity.IEntityProcessInstance;
import br.net.mirante.singular.flow.core.entity.IEntityProcessRole;
import br.net.mirante.singular.flow.core.entity.IEntityRole;
import br.net.mirante.singular.flow.core.entity.IEntityTask;
import br.net.mirante.singular.flow.core.entity.IEntityTaskDefinition;
import br.net.mirante.singular.flow.core.entity.IEntityTaskInstance;
import br.net.mirante.singular.flow.core.entity.IEntityTaskTransition;
import br.net.mirante.singular.flow.core.entity.IEntityVariableInstance;
import br.net.mirante.singular.flow.core.entity.IEntityVariableType;
import br.net.mirante.singular.flow.util.vars.VarInstance;
import br.net.mirante.singular.flow.util.vars.VarInstanceMap;
import br.net.mirante.singular.flow.util.vars.VarType;

public interface IPersistenceService<DEFINITION_CATEGORY extends IEntityCategory, PROCESS_DEFINITION extends IEntityProcess, PROCESS_INSTANCE extends IEntityProcessInstance, TASK_INSTANCE extends IEntityTaskInstance, TASK_DEF extends IEntityTaskDefinition, TASK extends IEntityTask, INSTANCE_VARIABLE extends IEntityVariableInstance, ROLE extends IEntityProcessRole, ROLE_USER extends IEntityRole> {

    PROCESS_INSTANCE createProcessInstance(@NotNull PROCESS_DEFINITION processDefinition, @NotNull TASK initialState);

    PROCESS_INSTANCE saveProcessInstance(@NotNull PROCESS_INSTANCE instance);

    TASK_INSTANCE addTask(@NotNull PROCESS_INSTANCE instance, @NotNull TASK state);

    void completeTask(@NotNull TASK_INSTANCE task, @Nullable String transitionName, @Nullable MUser responsibleUser);

    void setProcessInstanceParent(@NotNull PROCESS_INSTANCE instance, @NotNull PROCESS_INSTANCE parentTask);

    ROLE_USER setInstanceUserRole(@NotNull PROCESS_INSTANCE instance, ROLE role, MUser user);

    void removeInstanceUserRole(@NotNull PROCESS_INSTANCE instance, ROLE_USER roleUser);

    Integer updateVariableValue(@NotNull PROCESS_INSTANCE instance, @NotNull VarInstance varInstance, Integer dbVariableCod);

    void setParentTask(@NotNull PROCESS_INSTANCE childrenInstance, @NotNull TASK_INSTANCE parentTask);

    void updateTask(@NotNull TASK_INSTANCE task);

    PROCESS_DEFINITION retrieveProcessDefinitionByCod(@NotNull Integer cod);

    PROCESS_DEFINITION retrieveProcessDefinitionByAbbreviation(@NotNull String abbreviation);

    PROCESS_INSTANCE retrieveProcessInstanceByCod(@NotNull Integer cod);

    TASK_INSTANCE retrieveTaskInstanceByCod(@NotNull Integer cod);

    TaskHistoricLog saveTaskHistoricLog(@NotNull TASK_INSTANCE task, String typeDescription, String detail, MUser allocatedUser, MUser responsibleUser, Date dateHour, PROCESS_INSTANCE generatedProcessInstance);

    void saveVariableHistoric(Date dateHour, PROCESS_INSTANCE instance, TASK_INSTANCE originTask, TASK_INSTANCE destinationTask, VarInstanceMap<?> instanceMap);

    default void saveVariableHistoric(Date dateHour, PROCESS_INSTANCE instance, TaskInstance originTask, TaskInstance destinationTask, VarInstanceMap<?> instanceMap) {
        saveVariableHistoric(dateHour, instance, originTask != null ? originTask.<TASK_INSTANCE> getEntityTaskInstance() : null, destinationTask != null ? destinationTask.<TASK_INSTANCE> getEntityTaskInstance() : null, instanceMap);
    }

    List<? extends MUser> retrieveUsersByCod(Collection<Integer> cods);

    /**
     * Must persist: {@link IEntityProcessDefinition}, {@link IEntityProcess},
     * {@link IEntityTaskDefinition}, {@link IEntityTask},
     * {@link IEntityTaskTransition}
     *
     * @param entityProcess
     * @return
     */
    PROCESS_DEFINITION saveOrUpdateProcessDefinition(PROCESS_DEFINITION entityProcess);

    IEntityVariableType retrieveOrCreateEntityVariableType(VarType varType);

    void relocateTask(TASK_INSTANCE taskInstance, MUser user);

    void updateTargetEndDate(TASK_INSTANCE taskInstance, Date targetEndDate);

    void refreshModel(IEntityByCod model);

    void flushSession();

    void commitTransaction();

    // Consultas
    List<PROCESS_INSTANCE> retrieveProcessInstancesWith(@NotNull PROCESS_DEFINITION processDefinition, @Nullable Date minDataInicio, @Nullable Date maxDataInicio,
        @Nullable Collection<? extends TASK_DEF> states);

    List<PROCESS_INSTANCE> retrieveProcessInstancesWith(@NotNull PROCESS_DEFINITION processDefinition, @Nullable MUser creatingUser, @Nullable Boolean active);
}
