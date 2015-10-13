package br.net.mirante.singular.persistence.service;

import java.util.Collection;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Objects;
import com.google.common.base.Throwables;

import br.net.mirante.singular.flow.core.Flow;
import br.net.mirante.singular.flow.core.MUser;
import br.net.mirante.singular.flow.core.entity.IEntityByCod;
import br.net.mirante.singular.flow.core.entity.IEntityCategory;
import br.net.mirante.singular.flow.core.entity.IEntityExecutionVariable;
import br.net.mirante.singular.flow.core.entity.IEntityProcessDefinition;
import br.net.mirante.singular.flow.core.entity.IEntityProcessInstance;
import br.net.mirante.singular.flow.core.entity.IEntityProcessRole;
import br.net.mirante.singular.flow.core.entity.IEntityProcessVersion;
import br.net.mirante.singular.flow.core.entity.IEntityRole;
import br.net.mirante.singular.flow.core.entity.IEntityTaskDefinition;
import br.net.mirante.singular.flow.core.entity.IEntityTaskHistoricType;
import br.net.mirante.singular.flow.core.entity.IEntityTaskInstance;
import br.net.mirante.singular.flow.core.entity.IEntityTaskInstanceHistory;
import br.net.mirante.singular.flow.core.entity.IEntityTaskTransition;
import br.net.mirante.singular.flow.core.entity.IEntityTaskVersion;
import br.net.mirante.singular.flow.core.entity.IEntityVariableInstance;
import br.net.mirante.singular.flow.core.entity.IEntityVariableType;
import br.net.mirante.singular.flow.core.service.IPersistenceService;
import br.net.mirante.singular.flow.util.vars.VarInstance;
import br.net.mirante.singular.flow.util.vars.VarInstanceMap;
import br.net.mirante.singular.flow.util.vars.VarType;
import br.net.mirante.singular.persistence.entity.util.SessionLocator;
import br.net.mirante.singular.persistence.entity.util.SessionWrapper;

public abstract class AbstractHibernatePersistenceService<DEFINITION_CATEGORY extends IEntityCategory, PROCESS_DEF extends IEntityProcessDefinition, PROCESS_VERSION extends IEntityProcessVersion, PROCESS_INSTANCE extends IEntityProcessInstance, TASK_INSTANCE extends IEntityTaskInstance, TASK_DEF extends IEntityTaskDefinition, TASK_VERSION extends IEntityTaskVersion, VARIABLE_INSTANCE extends IEntityVariableInstance, PROCESS_ROLE extends IEntityProcessRole, ROLE_USER extends IEntityRole>
        extends AbstractHibernateService implements
        IPersistenceService<DEFINITION_CATEGORY, PROCESS_DEF, PROCESS_VERSION, PROCESS_INSTANCE, TASK_INSTANCE, TASK_DEF, TASK_VERSION, VARIABLE_INSTANCE, PROCESS_ROLE, ROLE_USER> {

    public AbstractHibernatePersistenceService(SessionLocator sessionLocator) {
        super(sessionLocator);
    }

    // -------------------------------------------------------
    // ProcessIntance
    // -------------------------------------------------------

    /**
     * Cria uma intancia de ProcessIntance parcialmente preenchida. Apenas isola
     * a persistencia do tipo correto.
     */
    protected abstract PROCESS_INSTANCE newProcessInstance(PROCESS_VERSION process);

    @Override
    public PROCESS_INSTANCE createProcessInstance(PROCESS_VERSION process, TASK_VERSION initialState) {
        PROCESS_INSTANCE processInstance = newProcessInstance(process);
        processInstance.setBeginDate(new Date());
        return processInstance;
    }

    @Override
    public PROCESS_INSTANCE saveProcessInstance(PROCESS_INSTANCE instancia) {
        SessionWrapper sw = getSession();
        sw.saveOrUpdate(instancia);
        sw.refresh(instancia);
        return instancia;
    }

    @Override
    public void setProcessInstanceParent(PROCESS_INSTANCE instance, PROCESS_INSTANCE parentTask) {
        throw new UnsupportedOperationException("Apenas o AlocPro tem suporte a instância pai.");
    }

    protected abstract ROLE_USER newEntityRole(PROCESS_INSTANCE instance, PROCESS_ROLE role, MUser user, MUser allocator);

    @Override
    public ROLE_USER setInstanceUserRole(PROCESS_INSTANCE instance, PROCESS_ROLE role, MUser user) {
        ROLE_USER entityRole = newEntityRole(instance, role, user, Flow.getUserIfAvailable());

        SessionWrapper sw = getSession();
        sw.save(entityRole);
        sw.refresh(instance);
        return entityRole;
    }

    @Override
    public void removeInstanceUserRole(PROCESS_INSTANCE processInstance, ROLE_USER roleInstance) {
        SessionWrapper sw = getSession();
        sw.delete(roleInstance);
        sw.refresh(processInstance);
    }

    /**
     * Cria uma nova taskInstance parcialmente preenchiada apenas com
     * processIntance e taskVersion.
     */
    protected abstract TASK_INSTANCE newTaskInstance(PROCESS_INSTANCE processInstance, TASK_VERSION taskVersion);

    @Override
    public TASK_INSTANCE addTask(PROCESS_INSTANCE processInstance, TASK_VERSION taskVersion) {
        Date agora = new Date();
        TASK_INSTANCE taskInstance = newTaskInstance(processInstance, taskVersion);
        taskInstance.setBeginDate(agora);
        if (taskVersion.isEnd()) {
            processInstance.setEndDate(agora);
            // taskInstance.setEndDate(agora);
        } else {
            processInstance.setEndDate(null);
        }

        processInstance.addTask(taskInstance);
        SessionWrapper sw = getSession();

        sw.save(taskInstance);
        sw.update(processInstance);
        sw.refresh(processInstance);
        return taskInstance;
    }

    @Override
    public void setParentTask(PROCESS_INSTANCE childrenInstance, TASK_INSTANCE parentTask) {
        childrenInstance.setParentTask(parentTask);
        getSession().update(childrenInstance);
    }

    @Override
    public void updateTask(TASK_INSTANCE tarefa) {
        getSession().update(tarefa);
    }

    @Override
    public void completeTask(TASK_INSTANCE task, String transitionAbbreviation, MUser responsibleUser) {
        task.setEndDate(new Date());
        IEntityTaskTransition transition = task.getTask().getTransition(transitionAbbreviation);
        task.setExecutedTransition(transition);

        if (responsibleUser != null) {
            task.setResponsibleUser(responsibleUser);
        }

        getSession().update(task);
    }

    @Override
    public void updateTargetEndDate(TASK_INSTANCE taskInstance, Date targetEndDate) {
        taskInstance.setTargetEndDate(targetEndDate);
        updateTask(taskInstance);
    }

    @Override
    public void relocateTask(TASK_INSTANCE taskInstance, MUser user) {
        taskInstance.setAllocatedUser(user);
        taskInstance.setSuspensionTargetDate(null);

        updateTask(taskInstance);
    }

    protected abstract IEntityTaskInstanceHistory newTaskInstanceHistory(TASK_INSTANCE task, IEntityTaskHistoricType taskHistoryType,
            MUser allocatedUser, MUser responsibleUser);

    @Override
    public IEntityTaskInstanceHistory saveTaskHistoricLog(TASK_INSTANCE task, String typeDescription, String detail, MUser allocatedUser,
            MUser responsibleUser, Date dateHour, PROCESS_INSTANCE generatedProcessInstance) {
        IEntityTaskHistoricType taskHistoryType = retrieveOrCreateTaskHistoricType(typeDescription);

        IEntityTaskInstanceHistory history = newTaskInstanceHistory(task, taskHistoryType, allocatedUser, responsibleUser);

        if (dateHour == null) {
            dateHour = new Date();
        }
        history.setBeginDateAllocation(dateHour);
        history.setDescription(detail);

        SessionWrapper sw = getSession();
        sw.save(history);
        sw.refresh(task);

        return history;
    }

    protected abstract Class<? extends IEntityTaskHistoricType> getClassEntityTaskHistoricType();

    protected final IEntityTaskHistoricType retrieveOrCreateTaskHistoricType(String typeDescription) {
        SessionWrapper sw = getSession();
        Class<? extends IEntityTaskHistoricType> classe = getClassEntityTaskHistoricType();

        IEntityTaskHistoricType variableType = sw.retrieveFirstFromCachedRetriveAll(classe,
                vt -> vt.getDescription().equals(typeDescription));

        if (variableType == null) {
            try {
                variableType = classe.newInstance();
            } catch (Exception e) {
                throw Throwables.propagate(e);
            }
            variableType.setDescription(typeDescription);
            sw.save(variableType);
        }
        return variableType;
    }

    // -------------------------------------------------------
    // Versions
    // -------------------------------------------------------

    @Override
    public PROCESS_VERSION saveProcessVersion(PROCESS_VERSION processVersion) {
        SessionWrapper sw = getSession();
        sw.saveOrUpdate(processVersion.getProcessDefinition());
        sw.saveOrUpdate(processVersion);
        sw.saveOrUpdate(processVersion.getTasks().stream().map(tv -> tv.getTaskDefinition()));
        sw.saveOrUpdate(processVersion.getTasks());
        sw.saveOrUpdate(processVersion.getTasks().stream().flatMap(tv -> tv.getTransitions().stream()));
        return retrieveProcessVersionByCod(processVersion.getCod());
    }

    // -------------------------------------------------------
    // Variable
    // -------------------------------------------------------

    protected abstract VARIABLE_INSTANCE retrieveVariableInstanceByCod(Integer cod);

    protected abstract VARIABLE_INSTANCE newVariableInstance(PROCESS_INSTANCE processInstance, String name);

    @Override
    public Integer updateVariableValue(PROCESS_INSTANCE processInstance, VarInstance mVariavel, Integer dbVariableCod) {
        SessionWrapper ss = getSession();
        Object valorAjustado = mVariavel.getValor();
        VARIABLE_INSTANCE variavel = null;
        if (dbVariableCod != null) {
            variavel = retrieveVariableInstanceByCod(dbVariableCod);
        }

        if (valorAjustado == null || isVazio(valorAjustado)) {
            if (variavel != null) {
                ss.delete(variavel);
                ss.refresh(processInstance);
            }
            return null;
        } else if (variavel == null) {
            // Para não forçar carga
            variavel = newVariableInstance(processInstance, mVariavel.getRef());

            String valorString = mVariavel.getStringPersistencia();
            if (!Objects.equal(valorString, variavel.getValue())) {
                variavel.setType(retrieveOrCreateEntityVariableType(mVariavel.getTipo()));
                variavel.setValue(valorString);
            }

            ss.save(variavel);
            ss.refresh(processInstance);
        } else {
            String valorString = mVariavel.getStringPersistencia();
            if (!Objects.equal(valorString, variavel.getValue())) {
                variavel.setType(retrieveOrCreateEntityVariableType(mVariavel.getTipo()));
                variavel.setValue(valorString);
                ss.merge(variavel);
            }
        }
        return variavel.getCod();
    }

    protected abstract IEntityExecutionVariable newExecutionVariable(PROCESS_INSTANCE instance, IEntityVariableInstance processInstanceVar,
            TASK_INSTANCE originTask, TASK_INSTANCE destinationTask, IEntityVariableType type);

    @Override
    public void saveVariableHistoric(Date dateHour, PROCESS_INSTANCE instance, TASK_INSTANCE originTask, TASK_INSTANCE destinationTask,
            VarInstanceMap<?> instanceMap) {
        if (instanceMap != null) {
            SessionWrapper ss = getSession();

            boolean salvou = false;
            for (VarInstance variavel : instanceMap) {
                if (variavel.getValor() != null) {
                    IEntityVariableType type = retrieveOrCreateEntityVariableType(variavel.getTipo());
                    IEntityVariableInstance processInstanceVar = instance.getVariable(variavel.getRef());

                    IEntityExecutionVariable novo = newExecutionVariable(instance, processInstanceVar, originTask, destinationTask, type);
                    novo.setName(variavel.getRef());
                    novo.setValue(variavel.getStringPersistencia());
                    novo.setDate(dateHour);
                    ss.save(novo);
                    salvou = true;
                }
            }
            if (salvou) {
                if (originTask != null) {
                    ss.refresh(originTask);
                }
                if (destinationTask != null) {
                    ss.refresh(destinationTask);
                }
            }
        }
    }

    protected abstract Class<? extends IEntityVariableType> getClassEntityVariableType();

    @Override
    public final IEntityVariableType retrieveOrCreateEntityVariableType(VarType varType) {
        SessionWrapper sw = getSession();
        Class<? extends IEntityVariableType> classe = getClassEntityVariableType();
        String typeClassName = varType.getClass().getName();

        IEntityVariableType variableType = sw.retrieveFirstFromCachedRetriveAll(classe, vt -> vt.getTypeClassName().equals(typeClassName));

        if (variableType == null) {
            try {
                variableType = classe.newInstance();
            } catch (Exception e) {
                throw Throwables.propagate(e);
            }
            variableType.setDescription(varType.getName());
            variableType.setTypeClassName(typeClassName);
            sw.save(variableType);
        }
        return variableType;
    }

    // -------------------------------------------------------
    // Util
    // -------------------------------------------------------

    @Override
    public void refreshModel(IEntityByCod model) {
        getSession().refresh(model);
    }

    @Override
    public void flushSession() {
        getSession().flush();
    }

    @Override
    public void commitTransaction() {
        getSession().commitAndContinue();
    }

    private static final boolean isVazio(Object obj) {
        if (obj == null) {
            return true;
        } else if (obj instanceof CharSequence) {
            return StringUtils.isEmpty(StringUtils.trimToNull(obj.toString()));
        } else if (obj instanceof Collection<?>) {
            return ((Collection<?>) obj).isEmpty();
        }
        return false;
    }
}
