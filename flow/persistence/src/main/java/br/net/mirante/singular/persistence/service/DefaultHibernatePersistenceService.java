package br.net.mirante.singular.persistence.service;

import br.net.mirante.singular.flow.core.MUser;
import br.net.mirante.singular.flow.core.TaskHistoricLog;
import br.net.mirante.singular.flow.core.entity.IEntityByCod;
import br.net.mirante.singular.flow.core.service.IPersistenceService;
import br.net.mirante.singular.flow.util.vars.VarInstance;
import br.net.mirante.singular.flow.util.vars.VarInstanceMap;
import br.net.mirante.singular.flow.util.vars.VarType;
import br.net.mirante.singular.persistence.dao.ExecutionVariableDAO;
import br.net.mirante.singular.persistence.dao.ProcessDAO;
import br.net.mirante.singular.persistence.dao.ProcessDefinitionDAO;
import br.net.mirante.singular.persistence.dao.ProcessInstanceDAO;
import br.net.mirante.singular.persistence.dao.RoleInstanceDAO;
import br.net.mirante.singular.persistence.dao.TaskDAO;
import br.net.mirante.singular.persistence.dao.TaskDefinitionDAO;
import br.net.mirante.singular.persistence.dao.TaskInstanceHistoryDAO;
import br.net.mirante.singular.persistence.dao.TaskHistoryTypeDAO;
import br.net.mirante.singular.persistence.dao.TaskInstanceDAO;
import br.net.mirante.singular.persistence.dao.TransitionDAO;
import br.net.mirante.singular.persistence.dao.VariableDAO;
import br.net.mirante.singular.persistence.dao.VariableTypeDAO;
import br.net.mirante.singular.persistence.entity.Actor;
import br.net.mirante.singular.persistence.entity.Category;
import br.net.mirante.singular.persistence.entity.ExecutionVariable;
import br.net.mirante.singular.persistence.entity.Process;
import br.net.mirante.singular.persistence.entity.ProcessInstance;
import br.net.mirante.singular.persistence.entity.Role;
import br.net.mirante.singular.persistence.entity.RoleInstance;
import br.net.mirante.singular.persistence.entity.Task;
import br.net.mirante.singular.persistence.entity.TaskDefinition;
import br.net.mirante.singular.persistence.entity.TaskHistoryType;
import br.net.mirante.singular.persistence.entity.TaskInstance;
import br.net.mirante.singular.persistence.entity.TaskInstanceHistory;
import br.net.mirante.singular.persistence.entity.Transition;
import br.net.mirante.singular.persistence.entity.Variable;
import br.net.mirante.singular.persistence.entity.VariableType;
import br.net.mirante.singular.persistence.entity.util.SessionLocator;
import com.google.common.base.Objects;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

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
    private final RoleInstanceDAO roleInstanceDAO = new RoleInstanceDAO(getSessionLocator());
    private final VariableDAO variableDAO = new VariableDAO(getSessionLocator());
    private final VariableTypeDAO variableTypeDAO = new VariableTypeDAO(getSessionLocator());
    private final TaskHistoryTypeDAO taskHistoryTypeDAO = new TaskHistoryTypeDAO(getSessionLocator());
    private final TaskInstanceHistoryDAO taskInstanceHistoryDAO = new TaskInstanceHistoryDAO(getSessionLocator());
    private final ExecutionVariableDAO executionVariableDAO = new ExecutionVariableDAO(getSessionLocator());

    public DefaultHibernatePersistenceService(SessionLocator sessionLocator) {
        super(sessionLocator);
    }

    @Override
    public ProcessInstance createProcessInstance(Process process, Task initialState) {
        final Date agora = new Date();
        ProcessInstance processInstance = new ProcessInstance();
        processInstance.setBeginDate(agora);
        processInstance.setProcess(process);
        processInstance.setRoles(new ArrayList<>());
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
        instance.setCurrentTaskDefinition(state.getTaskDefinition());
        taskInstanceDAO.save(taskInstance);
        processInstanceDAO.update(instance);
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
    public void setProcessInstanceParent(ProcessInstance instance, ProcessInstance parentTask) {
        throw new UnsupportedOperationException("Apenas o AlocPro tem suporte a instância pai.");
    }

    @Override
    public RoleInstance setInstanceUserRole(ProcessInstance instance, Role role, MUser user) {
        final RoleInstance roleInstance = new RoleInstance();
        roleInstance.setProcessInstance(instance);
        roleInstance.setActor((Actor) user);
        roleInstance.setRole(role);
        //TODO recuperar ator que esta executando acao
//        roleInstance.setAllocatorActor();
        roleInstance.setCreateDate(new Date());

        roleInstanceDAO.save(roleInstance);
        instance.addRole(roleInstance);
        return roleInstance;
    }

    @Override
    public void removeInstanceUserRole(ProcessInstance instance, RoleInstance roleInstance) {
        roleInstanceDAO.delete(roleInstance);
        flushSession();
        processInstanceDAO.refresh(instance);
    }

    @Override
    public Long updateVariableValue(ProcessInstance instancia, VarInstance mVariavel, Serializable dbVariableCod) {

        Object valorAjustado = mVariavel.getValor();
        Variable variable = null;
        if (dbVariableCod != null) {
            variable = variableDAO.retrieveById(dbVariableCod);
        }

        if (valorAjustado == null || isVazio(valorAjustado)) {
            if (variable != null) {
                variableDAO.delete(variable);
                processInstanceDAO.refresh(instancia);
            }
            return null;
        } else if (variable == null) {
            // Para não forçar carga
            variable = new Variable();
            variable.setProcessInstance(instancia);
            variable.setName(mVariavel.getRef());

            String valorString = mVariavel.getStringPersistencia();
            if (!Objects.equal(valorString, variable.getValue())) {
                variable.setType(retrieveOrCreateEntityVariableType(mVariavel.getTipo()));
                variable.setValue(valorString);
            }

            variableDAO.save(variable);
            processInstanceDAO.refresh(instancia);
        } else {
            String valorString = mVariavel.getStringPersistencia();
            if (!Objects.equal(valorString, variable.getValue())) {
                variable.setType(retrieveOrCreateEntityVariableType(mVariavel.getTipo()));
                variable.setValue(valorString);
                variableDAO.merge(variable);
            }
        }
        return variable.getCod();

    }

    private boolean isVazio(Object obj) {
        if (obj == null) {
            return true;
        } else if (obj instanceof CharSequence) {
            return StringUtils.isEmpty(StringUtils.trimToNull(obj.toString()));
        } else if (obj instanceof Collection<?>) {
            return ((Collection<?>) obj).isEmpty();
        }
        return false;
    }

    @Override
    public void setParentTask(ProcessInstance childrenInstance, TaskInstance parentTask) {
        childrenInstance.setParentTask(parentTask);
        processInstanceDAO.update(childrenInstance);
    }

    @Override
    public void updateTask(TaskInstance task) {
        taskInstanceDAO.update(task);
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
    public TaskHistoricLog saveTaskHistoricLog(TaskInstance task, String typeDescription, String detail,
                                               MUser allocatedUser, MUser responsibleUser, Date dateHour,
                                               ProcessInstance generatedProcessInstance) {
        final TaskInstanceHistory taskInstanceHistory = new TaskInstanceHistory();
        taskInstanceHistory.setTaskInstance(task);
        if (dateHour == null) {
            dateHour = new Date();
        }
        taskInstanceHistory.setBeginDateAllocation(dateHour);
        TaskHistoryType taskHistoryType = taskHistoryTypeDAO.retrieveOrSave(typeDescription);
        taskInstanceHistory.setTaskHistoryType(taskHistoryType);
        taskInstanceHistory.setDescription(detail);
        if (allocatedUser != null) {
            taskInstanceHistory.setAllocatedUser((Actor) allocatedUser);
        }
        if (responsibleUser != null) {
            taskInstanceHistory.setAllocatorUser((Actor) responsibleUser);
        }

        taskInstanceHistoryDAO.save(taskInstanceHistory);
        taskInstanceDAO.refresh(task);

        return new TaskHistoricLog(taskInstanceHistory);
    }

    @Override
    public void saveVariableHistoric(Date dateHour, ProcessInstance instance, TaskInstance originTask,
                                     TaskInstance destinationTask, VarInstanceMap<?> instanceMap) {
        if (instanceMap != null) {
            boolean salvou = false;
            for (VarInstance variavel : instanceMap) {
                if(variavel.getValor() != null){
                    ExecutionVariable novo = new ExecutionVariable();
//                    novo.setVariable((Variable) instance.getVariable(variavel.getRef()));
                    novo.setDate(dateHour);
                    novo.setProcessInstance(instance);
                    novo.setOriginTask(originTask);
                    novo.setDestinationTask(destinationTask);
                    novo.setName(variavel.getRef());
                    novo.setVariableType(retrieveOrCreateEntityVariableType(variavel.getTipo()));
                    novo.setValue(variavel.getStringPersistencia());
                    executionVariableDAO.save(novo);
                    salvou = true;
                }
            }
            if (salvou) {
                if (originTask != null) {
                    taskInstanceDAO.refresh(originTask);
                }
                if (destinationTask != null) {
                    taskInstanceDAO.refresh(destinationTask);
                }
            }
        }
    }

    @Override
    public List<? extends MUser> retrieveUsersByCod(Collection<Integer> cods) {
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
    public VariableType retrieveOrCreateEntityVariableType(VarType varType) {
        return variableTypeDAO.retrieveOrSave(varType);
    }

    @Override
    public void relocateTask(TaskInstance taskInstance, MUser user) {
        taskInstance.setAllocatedUser((Actor) user);
        taskInstance.setSuspensionTargetDate(null);

        updateTask(taskInstance);
    }

    @Override
    public void updateTargetEndDate(TaskInstance taskInstance, Date targetEndDate) {
        taskInstance.setTargetEndDate(targetEndDate);
        updateTask(taskInstance);
    }

    @Override
    public void refreshModel(IEntityByCod model) {
        getSessionLocator().getCurrentSession().refresh(model);
    }

    @Override
    public void flushSession() {
        getSessionLocator().getCurrentSession().flush();
    }

    @Override
    public void commitTransaction() {

    }

    @Override
    public List<ProcessInstance> retrieveProcessInstancesWith(Process process, Date minDataInicio, Date maxDataInicio, Collection<? extends TaskDefinition> states) {
        requireNonNull(process);
        if (minDataInicio == null && maxDataInicio == null) {
            return processDAO.retrivePorEstado(null, null, process.getProcessDefinition(), states);
        }
        return processDAO.retrivePorEstado(minDataInicio, maxDataInicio, process.getProcessDefinition(), states);
    }

    @Override
    public List<ProcessInstance> retrieveProcessInstancesWith(Process process, MUser creatingUser, Boolean active) {
        throw new UnsupportedOperationException("Método não implementado");
    }
}
