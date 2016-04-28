/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.persistence.service;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.joda.time.LocalDate;

import com.google.common.base.Throwables;

import br.net.mirante.singular.flow.core.Flow;
import br.net.mirante.singular.flow.core.MUser;
import br.net.mirante.singular.flow.core.TaskInstance;
import br.net.mirante.singular.flow.core.TaskType;
import br.net.mirante.singular.flow.core.entity.IEntityByCod;
import br.net.mirante.singular.flow.core.entity.IEntityCategory;
import br.net.mirante.singular.flow.core.entity.IEntityExecutionVariable;
import br.net.mirante.singular.flow.core.entity.IEntityProcessDefinition;
import br.net.mirante.singular.flow.core.entity.IEntityProcessInstance;
import br.net.mirante.singular.flow.core.entity.IEntityProcessVersion;
import br.net.mirante.singular.flow.core.entity.IEntityRoleDefinition;
import br.net.mirante.singular.flow.core.entity.IEntityRoleInstance;
import br.net.mirante.singular.flow.core.entity.IEntityTaskDefinition;
import br.net.mirante.singular.flow.core.entity.IEntityTaskHistoricType;
import br.net.mirante.singular.flow.core.entity.IEntityTaskInstance;
import br.net.mirante.singular.flow.core.entity.IEntityTaskInstanceHistory;
import br.net.mirante.singular.flow.core.entity.IEntityTaskTransitionVersion;
import br.net.mirante.singular.flow.core.entity.IEntityTaskVersion;
import br.net.mirante.singular.flow.core.entity.IEntityVariableInstance;
import br.net.mirante.singular.flow.core.entity.IEntityVariableType;
import br.net.mirante.singular.flow.core.service.IPersistenceService;
import br.net.mirante.singular.flow.core.variable.VarInstance;
import br.net.mirante.singular.flow.core.variable.VarInstanceMap;
import br.net.mirante.singular.flow.core.variable.VarType;
import br.net.mirante.singular.persistence.entity.util.SessionLocator;
import br.net.mirante.singular.persistence.entity.util.SessionWrapper;

public abstract class AbstractHibernatePersistenceService<DEFINITION_CATEGORY extends IEntityCategory, PROCESS_DEF extends IEntityProcessDefinition, PROCESS_VERSION extends IEntityProcessVersion, PROCESS_INSTANCE extends IEntityProcessInstance, TASK_INSTANCE extends IEntityTaskInstance, TASK_DEF extends IEntityTaskDefinition, TASK_VERSION extends IEntityTaskVersion, VARIABLE_INSTANCE extends IEntityVariableInstance, PROCESS_ROLE extends IEntityRoleDefinition, ROLE_USER extends IEntityRoleInstance>
        extends AbstractHibernateService implements
        IPersistenceService<DEFINITION_CATEGORY, PROCESS_DEF, PROCESS_VERSION, PROCESS_INSTANCE, TASK_INSTANCE, TASK_DEF, TASK_VERSION, VARIABLE_INSTANCE, PROCESS_ROLE, ROLE_USER> {

    public AbstractHibernatePersistenceService(SessionLocator sessionLocator) {
        super(sessionLocator);
    }

    // -------------------------------------------------------
    // ProcessIntance
    // -------------------------------------------------------

    @Override
    public PROCESS_INSTANCE retrieveProcessInstanceByCod(Integer cod) {
        return getSession().retrieve(getClassProcessInstance(), cod);
    }
    
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
        user = saveUserIfNeeded(user);

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

    public MUser saveUserIfNeeded(MUser mUser) {
        return Flow.getConfigBean().getUserService().saveUserIfNeeded(mUser);
    }

    protected abstract Class<TASK_INSTANCE> getClassTaskInstance();
    
    /**
     * Cria uma nova taskInstance parcialmente preenchiada apenas com
     * processIntance e taskVersion.
     */
    protected abstract TASK_INSTANCE newTaskInstance(PROCESS_INSTANCE processInstance, TASK_VERSION taskVersion);

    @Override
    public TASK_INSTANCE retrieveTaskInstanceByCod(Integer cod) {
        Objects.requireNonNull(cod);
        return getSession().retrieve(getClassTaskInstance(), cod);
    }

    
    @Override
    public TASK_INSTANCE addTask(PROCESS_INSTANCE processInstance, TASK_VERSION taskVersion) {
        Date agora = new Date();
        TASK_INSTANCE taskInstance = newTaskInstance(processInstance, taskVersion);
        taskInstance.setBeginDate(agora);
        if (taskVersion.isEnd()) {
            processInstance.setEndDate(agora);
            taskInstance.setEndDate(agora);
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
        responsibleUser = saveUserIfNeeded(responsibleUser);
        task.setEndDate(new Date());
        IEntityTaskTransitionVersion transition = task.getTask().getTransition(transitionAbbreviation);
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
        user = saveUserIfNeeded(user);
        taskInstance.setAllocatedUser(user);

        updateTask(taskInstance);
    }

    protected abstract IEntityTaskInstanceHistory newTaskInstanceHistory(TASK_INSTANCE task, IEntityTaskHistoricType taskHistoryType,
            MUser allocatedUser, MUser responsibleUser);

    @Override
    public IEntityTaskInstanceHistory saveTaskHistoricLog(TASK_INSTANCE task, String typeDescription, String detail, MUser allocatedUser,
            MUser responsibleUser, Date dateHour, PROCESS_INSTANCE generatedProcessInstance) {
        IEntityTaskHistoricType taskHistoryType = retrieveOrCreateTaskHistoricType(typeDescription);

        responsibleUser = saveUserIfNeeded(responsibleUser);
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
        sw.saveOrUpdate(processVersion.getVersionTasks().stream().map(tv -> tv.getTaskDefinition()));
        sw.saveOrUpdate(processVersion.getVersionTasks());
        sw.saveOrUpdate(processVersion.getVersionTasks().stream().flatMap(tv -> tv.getTransitions().stream()));
        
        sw.refresh(processVersion.getProcessDefinition());
        
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
                variavel.setValue(null);
                ss.merge(variavel);
                ss.refresh(processInstance);
            } else {
                return null;
            }
        } else if (variavel == null) {
            // Para não forçar carga
            variavel = newVariableInstance(processInstance, mVariavel.getRef());

            String valorString = mVariavel.getStringPersistencia();
            if (!Objects.equals(valorString, variavel.getValue())) {
                variavel.setType(retrieveOrCreateEntityVariableType(mVariavel.getTipo()));
                variavel.setValue(valorString);
            }

            ss.save(variavel);
            ss.refresh(processInstance);
        } else {
            String valorString = mVariavel.getStringPersistencia();
            if (!Objects.equals(valorString, variavel.getValue())) {
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

    protected abstract Class<PROCESS_INSTANCE> getClassProcessInstance();
    
    public List<PROCESS_INSTANCE> retrieveProcessInstancesWith(PROCESS_DEF process, Date dataInicio, Date dataFim, java.util.Collection<? extends TASK_DEF> states) {
        Objects.requireNonNull(process);
        final Criteria c = getSession().createCriteria(getClassProcessInstance(), "PI");
        c.createAlias("PI.processVersion", "DEF");
        c.add(Restrictions.eq("DEF.processDefinition", process));
        if (states != null && !states.isEmpty()) {
            DetachedCriteria sub = DetachedCriteria.forClass(getClassTaskInstance(), "T");
            sub.add(Restrictions.eqProperty("T.processInstance.cod", "PI.cod"));
            sub.createAlias("T.task", "TV");
            sub.add(Restrictions.in("TV.taskDefinition", states));
            sub.add(Restrictions.isNull("T.endDate"));
            sub.setProjection(Projections.id());
            
            c.add(Subqueries.exists(sub));
        }
        if (dataInicio != null && dataFim != null) {
            c.add(Restrictions.or(
                Restrictions.and(Restrictions.ge("PI.beginDate", dataInicio), Restrictions.lt("PI.beginDate", dataFim)),
                Restrictions.and(Restrictions.ge("PI.endDate", dataInicio), Restrictions.lt("PI.endDate", dataFim)),
                Restrictions.and(Restrictions.lt("PI.beginDate", dataInicio), Restrictions.ge("PI.endDate", dataInicio)),
                Restrictions.and(Restrictions.isNull("PI.endDate"), Restrictions.lt("PI.beginDate", dataFim))));
        } else if(dataInicio != null){
            c.add(Restrictions.or(
                Restrictions.ge("PI.beginDate", dataInicio), 
                Restrictions.ge("PI.endDate", dataInicio),
                Restrictions.and(Restrictions.lt("PI.beginDate", dataInicio), Restrictions.isNull("PI.endDate"))));
        } else if (dataFim != null) {
            c.add(Restrictions.or(Restrictions.le("PI.beginDate", dataFim), Restrictions.le("PI.endDate", dataFim)));
        }
        c.addOrder(Order.desc("PI.beginDate"));
        return c.list();
    }

    public List<PROCESS_INSTANCE> retrieveProcessInstancesWith(PROCESS_DEF process, MUser creatingUser, Boolean active) {
        Objects.requireNonNull(process);
        Criteria c = getSession().createCriteria(getClassProcessInstance(), "PI");
        c.createAlias("PI.processVersion", "DEF");
        c.add(Restrictions.eq("DEF.processDefinition", process));

        if (active != null) {
            DetachedCriteria sub = DetachedCriteria.forClass(getClassTaskInstance(), "T");
            sub.createAlias("T.task", "TA");
            sub.add(Restrictions.eqProperty("T.processInstance.cod", "PI.cod"));
            sub.add(Restrictions.isNull("T.endDate"));
            if (active) {
                sub.add(Restrictions.ne("TA.type", TaskType.End));
            } else {
                sub.add(Restrictions.eq("TA.type", TaskType.End));
            }
            sub.setProjection(Projections.id());
            
            c.add(Subqueries.exists(sub));
        }

        if (creatingUser != null) {
            c.add(Restrictions.eq("PI.userCreator", creatingUser));
        }
        c.setCacheable(true).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return c.list();
    }

    @Override
    public void endLastAllocation(TASK_INSTANCE entityTaskInstance) {
        List<? extends IEntityTaskInstanceHistory> histories = entityTaskInstance.getTaskHistoric();
        for (ListIterator<? extends IEntityTaskInstanceHistory> it = histories.listIterator(histories.size()); it.hasPrevious(); ) {
            IEntityTaskInstanceHistory history = it.previous();
            if (history.getType().getDescription().toLowerCase().contains(TaskInstance.ALOCACAO.toLowerCase())) {
                history.setEndDateAllocation(new Date());
                getSession().saveOrUpdate(history);
            }
        }
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
