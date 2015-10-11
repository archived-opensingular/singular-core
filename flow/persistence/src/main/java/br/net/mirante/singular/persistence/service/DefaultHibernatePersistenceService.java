package br.net.mirante.singular.persistence.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import br.net.mirante.singular.flow.core.MUser;
import br.net.mirante.singular.flow.core.entity.IEntityExecutionVariable;
import br.net.mirante.singular.flow.core.entity.IEntityTaskHistoricType;
import br.net.mirante.singular.flow.core.entity.IEntityTaskInstanceHistory;
import br.net.mirante.singular.flow.core.entity.IEntityVariableInstance;
import br.net.mirante.singular.flow.core.entity.IEntityVariableType;
import br.net.mirante.singular.persistence.dao.ExecutionVariableDAO;
import br.net.mirante.singular.persistence.dao.ProcessDAO;
import br.net.mirante.singular.persistence.dao.ProcessDefinitionDAO;
import br.net.mirante.singular.persistence.dao.ProcessInstanceDAO;
import br.net.mirante.singular.persistence.dao.RoleInstanceDAO;
import br.net.mirante.singular.persistence.dao.TaskDAO;
import br.net.mirante.singular.persistence.dao.TaskDefinitionDAO;
import br.net.mirante.singular.persistence.dao.TaskHistoryTypeDAO;
import br.net.mirante.singular.persistence.dao.TaskInstanceDAO;
import br.net.mirante.singular.persistence.dao.TaskInstanceHistoryDAO;
import br.net.mirante.singular.persistence.dao.TransitionDAO;
import br.net.mirante.singular.persistence.dao.VariableDAO;
import br.net.mirante.singular.persistence.dao.VariableTypeDAO;
import br.net.mirante.singular.persistence.entity.Actor;
import br.net.mirante.singular.persistence.entity.Category;
import br.net.mirante.singular.persistence.entity.ExecutionVariable;
import br.net.mirante.singular.persistence.entity.Process;
import br.net.mirante.singular.persistence.entity.ProcessDefinition;
import br.net.mirante.singular.persistence.entity.ProcessInstance;
import br.net.mirante.singular.persistence.entity.Role;
import br.net.mirante.singular.persistence.entity.RoleInstance;
import br.net.mirante.singular.persistence.entity.Task;
import br.net.mirante.singular.persistence.entity.TaskDefinition;
import br.net.mirante.singular.persistence.entity.TaskHistoryType;
import br.net.mirante.singular.persistence.entity.TaskInstance;
import br.net.mirante.singular.persistence.entity.TaskInstanceHistory;
import br.net.mirante.singular.persistence.entity.Variable;
import br.net.mirante.singular.persistence.entity.VariableType;
import br.net.mirante.singular.persistence.entity.util.SessionLocator;

public class DefaultHibernatePersistenceService extends
        AbstractHibernatePersistenceService<Category, ProcessDefinition,
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
    private final RoleInstanceDAO roleInstanceDAO = new RoleInstanceDAO(getSessionLocator());
    private final VariableDAO variableDAO = new VariableDAO(getSessionLocator());
    private final VariableTypeDAO variableTypeDAO = new VariableTypeDAO(getSessionLocator());
    private final TaskHistoryTypeDAO taskHistoryTypeDAO = new TaskHistoryTypeDAO(getSessionLocator());
    private final TaskInstanceHistoryDAO taskInstanceHistoryDAO = new TaskInstanceHistoryDAO(getSessionLocator());
    private final ExecutionVariableDAO executionVariableDAO = new ExecutionVariableDAO(getSessionLocator());

    public DefaultHibernatePersistenceService(SessionLocator sessionLocator) {
        super(sessionLocator);
    }

    // -------------------------------------------------------
    // ProcessIntance
    // -------------------------------------------------------

    @Override
    public ProcessInstance retrieveProcessInstanceByCod(Integer cod) {
        return getSession().retrieve(ProcessInstance.class, cod);
    }

    @Override
    protected ProcessInstance newProcessInstance(Process processVersion) {
        ProcessInstance processInstance = new ProcessInstance();
        processInstance.setProcess(processVersion);
        processInstance.setRoles(new ArrayList<>());
        return processInstance;
    }

    @Override
    protected RoleInstance newEntityRole(ProcessInstance instance, Role role, MUser user, MUser allocator) {
        final RoleInstance entityRole = new RoleInstance();
        entityRole.setProcessInstance(instance);
        entityRole.setActor((Actor) user);
        entityRole.setRole(role);
        entityRole.setAllocatorUser((Actor) allocator);
        entityRole.setCreateDate(new Date());
        return entityRole;
    }

    // -------------------------------------------------------
    // Task
    // -------------------------------------------------------

    @Override
    public TaskInstance retrieveTaskInstanceByCod(Integer cod) {
        return getSession().retrieve(TaskInstance.class, cod);
    }

    @Override
    protected TaskInstance newTaskInstance(ProcessInstance processInstance, Task taskVersion) {
        TaskInstance taskInstance = new TaskInstance();
        taskInstance.setProcessInstance(processInstance);
        taskInstance.setTask(taskVersion);
        return taskInstance;
    }

    @Override
    public TaskInstance addTask(ProcessInstance processInstance, Task taskVersion) {
        processInstance.setCurrentTaskDefinition(taskVersion.getTaskDefinition());
        return super.addTask(processInstance, taskVersion);
    }

    @Override
    protected IEntityTaskInstanceHistory newTaskInstanceHistory(TaskInstance task, IEntityTaskHistoricType taskHistoryType,
            MUser allocatedUser, MUser responsibleUser) {

        TaskInstanceHistory history = new TaskInstanceHistory();
        history.setTaskInstance(task);
        history.setTaskHistoryType((TaskHistoryType) taskHistoryType);
        history.setAllocatedUser((Actor) allocatedUser);
        history.setAllocatorUser((Actor) responsibleUser);
        return history;
    }

    @Override
    protected Class<? extends TaskHistoryType> getClassEntityTaskHistoricType() {
        return TaskHistoryType.class;
    }

    // -------------------------------------------------------
    // Process Definition e Version
    // -------------------------------------------------------

    @Override
    public Process retrieveProcessVersionByCod(Integer cod) {
        return processDAO.retrieveById(cod);
    }

    // -------------------------------------------------------
    // Variable
    // -------------------------------------------------------

    @Override
    protected Variable retrieveVariableInstanceByCod(Integer cod) {
        return getSession().retrieve(Variable.class, cod);
    }

    @Override
    protected Variable newVariableInstance(ProcessInstance processInstance, String name) {
        Variable variable = new Variable();
        variable.setProcessInstance(processInstance);
        variable.setName(name);
        return variable;
    }


    @Override
    protected IEntityExecutionVariable newExecutionVariable(ProcessInstance instance, IEntityVariableInstance processInstanceVar,
            TaskInstance originTask, TaskInstance destinationTask, IEntityVariableType type) {
        ExecutionVariable novo = new ExecutionVariable();
        novo.setVariable((Variable) processInstanceVar);
        novo.setProcessInstance(instance);
        novo.setOriginTask(originTask);
        novo.setDestinationTask(destinationTask);
        novo.setVariableType((VariableType) type);
        return novo;
    }

    @Override
    protected Class<? extends IEntityVariableType> getClassEntityVariableType() {
        return VariableType.class;
    }

    // -------------------------------------------------------
    // Listagens
    // -------------------------------------------------------

    @Override
    public List<? extends MUser> retrieveUsersByCod(Collection<Integer> cods) {
        throw new UnsupportedOperationException("Método não implementado");
    }

    @Override
    public List<ProcessInstance> retrieveProcessInstancesWith(ProcessDefinition process, Date minDataInicio, Date maxDataInicio,
            Collection<? extends TaskDefinition> states) {
        Objects.requireNonNull(process);
        return processDAO.retrivePorEstado(minDataInicio, maxDataInicio, process, states);
    }

    @Override
    public List<ProcessInstance> retrieveProcessInstancesWith(ProcessDefinition process, MUser creatingUser, Boolean active) {
        throw new UnsupportedOperationException("Método não implementado");
    }
}
