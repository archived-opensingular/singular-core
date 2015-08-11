package br.net.mirante.singular.flow.core.entity.persistence;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import br.net.mirante.singular.flow.core.MTask;
import br.net.mirante.singular.flow.core.MUser;
import br.net.mirante.singular.flow.core.ProcessDefinition;
import br.net.mirante.singular.flow.core.ProcessInstance;
import br.net.mirante.singular.flow.core.TaskHistoricLog;
import br.net.mirante.singular.flow.core.TaskInstance;
import br.net.mirante.singular.flow.core.entity.IEntityByCod;
import br.net.mirante.singular.flow.core.entity.IEntityCategory;
import br.net.mirante.singular.flow.core.entity.IEntityProcess;
import br.net.mirante.singular.flow.core.entity.IEntityProcessInstance;
import br.net.mirante.singular.flow.core.entity.IEntityProcessRole;
import br.net.mirante.singular.flow.core.entity.IEntityRole;
import br.net.mirante.singular.flow.core.entity.IEntityTaskDefinition;
import br.net.mirante.singular.flow.core.entity.IEntityTaskInstance;
import br.net.mirante.singular.flow.core.entity.IEntityVariableInstance;
import br.net.mirante.singular.flow.util.vars.VarInstance;
import br.net.mirante.singular.flow.util.vars.VarInstanceMap;

public interface IPersistenceService<DEFINITION_CATEGORY extends IEntityCategory, PROCESS_DEFINITION extends IEntityProcess, PROCESS_INSTANCE extends IEntityProcessInstance, TASK extends IEntityTaskInstance, TASK_STATE extends IEntityTaskDefinition, INSTANCE_VARIABLE extends IEntityVariableInstance, ROLE extends IEntityProcessRole, ROLE_USER extends IEntityRole> {

	PROCESS_INSTANCE createProcessInstance(@NotNull PROCESS_DEFINITION processDefinition, @NotNull TASK_STATE initialState);

	PROCESS_INSTANCE saveProcessInstance(@NotNull PROCESS_INSTANCE instance);

	TASK addTask(@NotNull PROCESS_INSTANCE instance, @NotNull TASK_STATE state);

	void endTask(@NotNull TASK task, @Nullable String transitionName, @Nullable MUser responsibleUser);

	void setProcessInstanceParent(@NotNull PROCESS_INSTANCE instance, @NotNull PROCESS_INSTANCE instanceFather);

	ROLE_USER setInstanceUserRole(@NotNull PROCESS_INSTANCE instance, ROLE role, MUser user);

	void removeInstanceUserRole(@NotNull PROCESS_INSTANCE instance, ROLE_USER roleUser);

	Integer updateVariableValue(@NotNull ProcessInstance instance, @NotNull VarInstance varInstance, Integer dbVariableCod);

	void setParentTask(@NotNull PROCESS_INSTANCE childrenInstance, @NotNull TASK parentTask);

	void updateTask(@NotNull TASK task);

	DEFINITION_CATEGORY retrieveOrCreateCategoryWith(@NotNull String name);

	PROCESS_DEFINITION retrieveProcessDefinitionByCod(@NotNull Serializable cod);

	PROCESS_DEFINITION retrieveProcessDefinitionByAbbreviation(@NotNull String abbreviation);

	PROCESS_DEFINITION retrieveOrCreateProcessDefinitionFor(@NotNull ProcessDefinition<?> processDefinition);

	PROCESS_INSTANCE retrieveProcessInstanceByCod(@NotNull Serializable cod);

	TASK_STATE retrieveTaskStateByCod(@NotNull Serializable cod);

	TASK_STATE retrieveOrCreateStateFor(@NotNull PROCESS_DEFINITION processDefinition, @NotNull MTask<?> mTask);

	void updateProcessDefinition(@NotNull PROCESS_DEFINITION processDefinition);

	int deleteProcessInstancesWithStateOlderThan(@NotNull List<TASK_STATE> states, @NotNull Date olderThan);

	TaskHistoricLog saveTaskHistoricLog(@NotNull TASK task, String typeDescription, String detail, MUser allocatedUser, MUser responsibleUser, Date dateHour, PROCESS_INSTANCE generatedProcessInstance);

	void saveVariableHistoric(Date dateHour, PROCESS_INSTANCE instance, TASK originTask, TASK destinationTask, VarInstanceMap<?> instanceMap);

	default void saveVariableHistoric(Date dateHour, PROCESS_INSTANCE instance, TaskInstance originTask, TaskInstance destinationTask, VarInstanceMap<?> instanceMap){
        saveVariableHistoric(dateHour, instance, originTask != null ? originTask.getEntityTaskInstance() : null, destinationTask != null ? destinationTask.getEntityTaskInstance() : null, instanceMap);
	}

	List<? extends MUser> retrieveUsersByCod(Collection<Integer> cods);

	void refreshModel(IEntityByCod model);

	void flushSession();

	void commitTransaction();

	// Consultas
	List<PROCESS_INSTANCE> retrieveProcessInstancesWith(@NotNull Collection<? extends TASK_STATE> states);

	List<PROCESS_INSTANCE> retrieveProcessInstancesWith(@NotNull PROCESS_DEFINITION processDefinition, @Nullable Date minDataInicio, @Nullable Date maxDataInicio,
			@Nullable Collection<? extends TASK_STATE> states);

	List<PROCESS_INSTANCE> retrieveProcessInstancesWith(@NotNull PROCESS_DEFINITION processDefinition, @Nullable MUser creatingUser, @Nullable Boolean active);
}
