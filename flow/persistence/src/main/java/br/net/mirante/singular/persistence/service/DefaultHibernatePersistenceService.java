package br.net.mirante.singular.persistence.service;

import br.net.mirante.singular.flow.core.MUser;
import br.net.mirante.singular.flow.core.TaskHistoricLog;
import br.net.mirante.singular.flow.core.entity.IEntityByCod;
import br.net.mirante.singular.flow.core.entity.IEntityVariableType;
import br.net.mirante.singular.flow.core.service.IPersistenceService;
import br.net.mirante.singular.flow.util.vars.VarInstance;
import br.net.mirante.singular.flow.util.vars.VarInstanceMap;
import br.net.mirante.singular.flow.util.vars.VarType;
import br.net.mirante.singular.persistence.entity.Category;
import br.net.mirante.singular.persistence.entity.TaskDefinition;
import br.net.mirante.singular.persistence.entity.RoleInstance;
import br.net.mirante.singular.persistence.entity.ProcessInstance;
import br.net.mirante.singular.persistence.entity.Role;
import br.net.mirante.singular.persistence.entity.Task;
import br.net.mirante.singular.persistence.entity.TaskInstance;
import br.net.mirante.singular.persistence.entity.Process;
import br.net.mirante.singular.persistence.entity.Variable;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class DefaultHibernatePersistenceService implements
        IPersistenceService<Category,
                Process,
                ProcessInstance,
                TaskInstance,
                TaskDefinition,
                Task,
                Variable,
                Role,
                RoleInstance> {

    @Override
    public ProcessInstance createProcessInstance(Process process, Task initialState) {
        return null;
    }

    @Override
    public ProcessInstance saveProcessInstance(ProcessInstance instance) {
        return null;
    }

    @Override
    public TaskInstance addTask(ProcessInstance instance, Task state) {
        return null;
    }

    @Override
    public void completeTask(TaskInstance task, String transitionName, MUser responsibleUser) {

    }

    @Override
    public void setProcessInstanceParent(ProcessInstance instance, ProcessInstance instanceFather) {

    }

    @Override
    public RoleInstance setInstanceUserRole(ProcessInstance instance, Role role, MUser user) {
        return null;
    }

    @Override
    public void removeInstanceUserRole(ProcessInstance instance, RoleInstance roleInstance) {

    }

    @Override
    public Long updateVariableValue(ProcessInstance instance, VarInstance varInstance, Serializable dbVariableCod) {
        return null;
    }

    @Override
    public void setParentTask(ProcessInstance childrenInstance, TaskInstance parentTask) {

    }

    @Override
    public void updateTask(TaskInstance task) {

    }

    @Override
    public Process retrieveProcessDefinitionByCod(Serializable cod) {
        return null;
    }

    @Override
    public Process retrieveProcessDefinitionByAbbreviation(String abbreviation) {
        return null;
    }

    @Override
    public ProcessInstance retrieveProcessInstanceByCod(Serializable cod) {
        return null;
    }

    @Override
    public TaskInstance retrieveTaskInstanceByCod(Serializable cod) {
        return null;
    }

    @Override
    public TaskHistoricLog saveTaskHistoricLog(TaskInstance task, String typeDescription, String detail, MUser allocatedUser, MUser responsibleUser, Date dateHour, ProcessInstance generatedProcessInstance) {
        return null;
    }

    @Override
    public void saveVariableHistoric(Date dateHour, ProcessInstance instance, TaskInstance originTask, TaskInstance destinationTask, VarInstanceMap<?> instanceMap) {

    }

    @Override
    public void saveVariableHistoric(Date dateHour, ProcessInstance instance, br.net.mirante.singular.flow.core.TaskInstance originTask, br.net.mirante.singular.flow.core.TaskInstance destinationTask, VarInstanceMap<?> instanceMap) {

    }

    @Override
    public List<? extends MUser> retrieveUsersByCod(Collection<Serializable> cods) {
        return null;
    }

    @Override
    public Process saveOrUpdateProcessDefinition(Process entityProcess) {
        return null;
    }

    @Override
    public IEntityVariableType retrieveOrCreateEntityVariableType(VarType varType) {
        return null;
    }

    @Override
    public void relocateTask(TaskInstance taskInstance, MUser user) {

    }

    @Override
    public void updateTargetEndDate(TaskInstance taskInstance, Date targetEndDate) {

    }

    @Override
    public void refreshModel(IEntityByCod model) {

    }

    @Override
    public void flushSession() {

    }

    @Override
    public void commitTransaction() {

    }

    @Override
    public List<ProcessInstance> retrieveProcessInstancesWith(Process process, Date minDataInicio, Date maxDataInicio, Collection<? extends TaskDefinition> states) {
        return null;
    }

    @Override
    public List<ProcessInstance> retrieveProcessInstancesWith(Process process, MUser creatingUser, Boolean active) {
        return null;
    }
}
