package br.net.mirante.singular.persistence.service;

import br.net.mirante.singular.flow.core.MBPM;
import br.net.mirante.singular.flow.core.MUser;
import br.net.mirante.singular.flow.core.TaskHistoricLog;
import br.net.mirante.singular.flow.core.entity.IEntityByCod;
import br.net.mirante.singular.flow.core.entity.IEntityVariableType;
import br.net.mirante.singular.flow.core.service.IPersistenceService;
import br.net.mirante.singular.flow.util.vars.VarInstance;
import br.net.mirante.singular.flow.util.vars.VarInstanceMap;
import br.net.mirante.singular.flow.util.vars.VarType;
import br.net.mirante.singular.persistence.dao.ProcessDAO;
import br.net.mirante.singular.persistence.dao.ProcessDefinitionDAO;
import br.net.mirante.singular.persistence.dao.ProcessInstanceDAO;
import br.net.mirante.singular.persistence.dao.TaskDAO;
import br.net.mirante.singular.persistence.dao.TaskDefinitionDAO;
import br.net.mirante.singular.persistence.dao.TaskInstanceDAO;
import br.net.mirante.singular.persistence.dao.TransitionDAO;
import br.net.mirante.singular.persistence.entity.Actor;
import br.net.mirante.singular.persistence.entity.Category;
import br.net.mirante.singular.persistence.entity.Process;
import br.net.mirante.singular.persistence.entity.ProcessInstance;
import br.net.mirante.singular.persistence.entity.Role;
import br.net.mirante.singular.persistence.entity.RoleInstance;
import br.net.mirante.singular.persistence.entity.Task;
import br.net.mirante.singular.persistence.entity.TaskDefinition;
import br.net.mirante.singular.persistence.entity.TaskInstance;
import br.net.mirante.singular.persistence.entity.Transition;
import br.net.mirante.singular.persistence.entity.Variable;
import br.net.mirante.singular.persistence.entity.util.SessionLocator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultHibernatePersistenceService extends AbstractHibernateService implements
        IPersistenceService<Category,
                Process,
                ProcessInstance,
                TaskInstance,
                TaskDefinition,
                Task,
                Variable,
                Role,
                RoleInstance> {

    private final ProcessDAO processDAO = new ProcessDAO(getSessionLocator());
    private final ProcessDefinitionDAO processDefinitionDAO = new ProcessDefinitionDAO(getSessionLocator());
    private final ProcessInstanceDAO processInstanceDAO = new ProcessInstanceDAO(getSessionLocator());
    private final TaskInstanceDAO taskInstanceDAO = new TaskInstanceDAO(getSessionLocator());
    private final TaskDAO taskDAO = new TaskDAO(getSessionLocator());
    private final TaskDefinitionDAO taskDefinitionDAO = new TaskDefinitionDAO(getSessionLocator());
    private final TransitionDAO transitionDAO = new TransitionDAO(getSessionLocator());

    public DefaultHibernatePersistenceService(SessionLocator sessionLocator) {
        super(sessionLocator);
    }

    @Override
    public ProcessInstance createProcessInstance(Process process, Task initialState) {
        final Date agora = new Date();
        ProcessInstance processInstance = new ProcessInstance();
        processInstance.setBeginDate(agora);
        processInstance.setProcess(process);

        return processInstance;
    }

    @Override
    public ProcessInstance saveProcessInstance(ProcessInstance instance) {
        processInstanceDAO.save(instance);
        return instance;
    }

    @Override
    public TaskInstance addTask(ProcessInstance instance, Task state) {

        final TaskInstance taskInstance = new TaskInstance();
        taskInstance.setProcessInstance(instance);
        taskInstance.setBeginDate(new Date());
        taskInstance.setTask(state);

        instance.addTask(taskInstance);
        taskInstanceDAO.save(taskInstance);
        return taskInstance;

    }

    @Override
    public void completeTask(TaskInstance task, String transitionName, MUser responsibleUser) {
        task.setEndDate(new Date());
        task.setExecutedTransition((Transition) task.getTask().getTransition(transitionName));

        if (responsibleUser != null) {
            task.setResponsibleUser((Actor) responsibleUser);
        }
        taskInstanceDAO.update(task);
    }

    @Override
    public void setProcessInstanceParent(ProcessInstance instance, ProcessInstance instanceFather) {
        throw new UnsupportedOperationException("Método não implementado");
    }

    @Override
    public RoleInstance setInstanceUserRole(ProcessInstance instance, Role role, MUser user) {
        throw new UnsupportedOperationException("Método não implementado");
    }

    @Override
    public void removeInstanceUserRole(ProcessInstance instance, RoleInstance roleInstance) {
        throw new UnsupportedOperationException("Método não implementado");
    }

    @Override
    public Long updateVariableValue(ProcessInstance instance, VarInstance varInstance, Serializable dbVariableCod) {
        throw new UnsupportedOperationException("Método não implementado");
    }

    @Override
    public void setParentTask(ProcessInstance childrenInstance, TaskInstance parentTask) {
        throw new UnsupportedOperationException("Método não implementado");
    }

    @Override
    public void updateTask(TaskInstance task) {
        throw new UnsupportedOperationException("Método não implementado");
    }

    @Override
    public Process retrieveProcessDefinitionByCod(Serializable cod) {
        return processDAO.retrieveById(cod);
    }

    @Override
    public Process retrieveProcessDefinitionByAbbreviation(String abbreviation) {
        return processDAO.retrieveByUniqueProperty(Process.class, "processDefinition.abbreviation", abbreviation);
    }

    @Override
    public ProcessInstance retrieveProcessInstanceByCod(Serializable cod) {
        return processInstanceDAO.retrieveById(cod);
    }

    @Override
    public TaskInstance retrieveTaskInstanceByCod(Serializable cod) {
        return taskInstanceDAO.retrieveById(cod);
    }

    @Override
    public TaskHistoricLog saveTaskHistoricLog(TaskInstance task, String typeDescription, String detail, MUser allocatedUser, MUser responsibleUser, Date dateHour, ProcessInstance generatedProcessInstance) {
        throw new UnsupportedOperationException("Método não implementado");
    }

    @Override
    public void saveVariableHistoric(Date dateHour, ProcessInstance instance, TaskInstance originTask, TaskInstance destinationTask, VarInstanceMap<?> instanceMap) {
        throw new UnsupportedOperationException("Método não implementado");
    }

    @Override
    public void saveVariableHistoric(Date dateHour, ProcessInstance instance, br.net.mirante.singular.flow.core.TaskInstance originTask, br.net.mirante.singular.flow.core.TaskInstance destinationTask, VarInstanceMap<?> instanceMap) {
//        throw new UnsupportedOperationException("Método não implementado");
    }

    @Override
    public List<? extends MUser> retrieveUsersByCod(Collection<Serializable> cods) {
        throw new UnsupportedOperationException("Método não implementado");
    }

    @Override
    public Process saveOrUpdateProcessDefinition(Process entityProcess) {
        processDefinitionDAO.save(entityProcess.getProcessDefinition());
        processDAO.save(entityProcess);
        taskDefinitionDAO.save(entityProcess.getTasks().stream().map(Task::getTaskDefinition).collect(Collectors.toList()));
        taskDAO.save(entityProcess.getTasks());
        transitionDAO.save(entityProcess.getTasks().stream().flatMap(task -> task.getTransitions().stream()).collect(Collectors.toList()));
        return entityProcess;
    }

    @Override
    public IEntityVariableType retrieveOrCreateEntityVariableType(VarType varType) {
        throw new UnsupportedOperationException("Método não implementado");
    }

    @Override
    public void relocateTask(TaskInstance taskInstance, MUser user) {
        throw new UnsupportedOperationException("Método não implementado");
    }

    @Override
    public void updateTargetEndDate(TaskInstance taskInstance, Date targetEndDate) {
        throw new UnsupportedOperationException("Método não implementado");
    }

    @Override
    public void refreshModel(IEntityByCod model) {
        throw new UnsupportedOperationException("Método não implementado");
    }

    @Override
    public void flushSession() {

    }

    @Override
    public void commitTransaction() {

    }

    @Override
    public List<ProcessInstance> retrieveProcessInstancesWith(Process process, Date minDataInicio, Date maxDataInicio, Collection<? extends TaskDefinition> states) {
        throw new UnsupportedOperationException("Método não implementado");
    }

    @Override
    public List<ProcessInstance> retrieveProcessInstancesWith(Process process, MUser creatingUser, Boolean active) {
        throw new UnsupportedOperationException("Método não implementado");
    }
}
