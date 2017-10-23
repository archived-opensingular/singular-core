/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.flow.persistence.service;

import com.google.common.base.Throwables;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.opensingular.flow.core.Flow;
import org.opensingular.flow.core.SUser;
import org.opensingular.flow.core.SingularFlowException;
import org.opensingular.flow.core.TaskInstance;
import org.opensingular.flow.core.TaskType;
import org.opensingular.flow.core.entity.IEntityByCod;
import org.opensingular.flow.core.entity.IEntityCategory;
import org.opensingular.flow.core.entity.IEntityExecutionVariable;
import org.opensingular.flow.core.entity.IEntityFlowDefinition;
import org.opensingular.flow.core.entity.IEntityFlowInstance;
import org.opensingular.flow.core.entity.IEntityFlowVersion;
import org.opensingular.flow.core.entity.IEntityRoleDefinition;
import org.opensingular.flow.core.entity.IEntityRoleInstance;
import org.opensingular.flow.core.entity.IEntityTaskDefinition;
import org.opensingular.flow.core.entity.IEntityTaskHistoricType;
import org.opensingular.flow.core.entity.IEntityTaskInstance;
import org.opensingular.flow.core.entity.IEntityTaskInstanceHistory;
import org.opensingular.flow.core.entity.IEntityTaskTransitionVersion;
import org.opensingular.flow.core.entity.IEntityTaskVersion;
import org.opensingular.flow.core.entity.IEntityVariableInstance;
import org.opensingular.flow.core.entity.IEntityVariableType;
import org.opensingular.flow.core.service.IPersistenceService;
import org.opensingular.flow.core.variable.VarInstance;
import org.opensingular.flow.core.variable.VarInstanceMap;
import org.opensingular.flow.core.variable.VarType;
import org.opensingular.flow.persistence.entity.util.SessionLocator;
import org.opensingular.flow.persistence.entity.util.SessionWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public abstract class AbstractHibernatePersistenceService<DEFINITION_CATEGORY extends IEntityCategory, FLOW_DEFINITION extends IEntityFlowDefinition, FLOW_VERSION extends IEntityFlowVersion, FLOW_INSTANCE extends IEntityFlowInstance, TASK_INSTANCE extends IEntityTaskInstance, TASK_DEF extends IEntityTaskDefinition, TASK_VERSION extends IEntityTaskVersion, VARIABLE_INSTANCE extends IEntityVariableInstance, ROLE_DEFINITION extends IEntityRoleDefinition, ROLE_INSTANCE extends IEntityRoleInstance>
        extends AbstractHibernateService implements
        IPersistenceService<DEFINITION_CATEGORY, FLOW_DEFINITION, FLOW_VERSION, FLOW_INSTANCE, TASK_INSTANCE, TASK_DEF, TASK_VERSION, VARIABLE_INSTANCE, ROLE_DEFINITION, ROLE_INSTANCE> {

    public AbstractHibernatePersistenceService(SessionLocator sessionLocator) {
        super(sessionLocator);
    }

    // -------------------------------------------------------
    // FlowInstance
    // -------------------------------------------------------

    @Override
    @Nonnull
    public Optional<FLOW_INSTANCE> retrieveFlowInstanceByCod(@Nonnull Integer cod) {
        Objects.requireNonNull(cod);
        return getSession().retrieve(getClassFlowInstance(), cod);
    }
    
    /**
     * Cria uma intancia de FlowInstance parcialmente preenchida. Apenas isola
     * a persistencia do tipo correto.
     */
    protected abstract FLOW_INSTANCE newFlowInstance(FLOW_VERSION flowVersion);

    @Override
    public FLOW_INSTANCE createFlowInstance(FLOW_VERSION flowVersion, TASK_VERSION initialState) {
        FLOW_INSTANCE flowInstance = newFlowInstance(flowVersion);
        flowInstance.setBeginDate(new Date());
        return flowInstance;
    }

    @Override
    public FLOW_INSTANCE saveFlowInstance(FLOW_INSTANCE instance) {
        SessionWrapper sw = getSession();
        sw.saveOrUpdate(instance);
        sw.refresh(instance);
        return instance;
    }

    @Override
    public void setFlowInstanceParent(FLOW_INSTANCE instance, FLOW_INSTANCE parentTask) {
        throw new UnsupportedOperationException("Apenas o AlocPro tem suporte a instância pai.");
    }

    protected abstract ROLE_INSTANCE newEntityRole(FLOW_INSTANCE instance, ROLE_DEFINITION role, SUser user, SUser allocator);

    @Override
    public ROLE_INSTANCE setInstanceUserRole(FLOW_INSTANCE instance, ROLE_DEFINITION role, SUser user) {
        SUser resolvedUser = saveUserIfNeeded(user);

        ROLE_INSTANCE entityRole = newEntityRole(instance, role, resolvedUser, Flow.getUserIfAvailable());

        SessionWrapper sw = getSession();
        sw.save(entityRole);
        sw.refresh(instance);
        return entityRole;
    }

    @Override
    public void removeInstanceUserRole(FLOW_INSTANCE flowInstance, ROLE_INSTANCE roleInstance) {
        SessionWrapper sw = getSession();
        sw.delete(roleInstance);
        sw.refresh(flowInstance);
    }

    public SUser saveUserIfNeeded(SUser sUser) {
        return Flow.getConfigBean().getUserService().saveUserIfNeeded(sUser);
    }

    protected abstract Class<TASK_INSTANCE> getClassTaskInstance();
    
    /**
     * Cria uma nova taskInstance parcialmente preenchiada apenas com
     * FlowInstance e taskVersion.
     */
    protected abstract TASK_INSTANCE newTaskInstance(FLOW_INSTANCE flowInstance, TASK_VERSION taskVersion);

    @Override
    @Nonnull
    public Optional<TASK_INSTANCE> retrieveTaskInstanceByCod(@Nonnull Integer cod) {
        Objects.requireNonNull(cod);
        return getSession().retrieve(getClassTaskInstance(), cod);
    }

    
    @Override
    public TASK_INSTANCE addTask(FLOW_INSTANCE flowInstance, TASK_VERSION taskVersion) {
        Date agora = new Date();
        TASK_INSTANCE taskInstance = newTaskInstance(flowInstance, taskVersion);
        taskInstance.setBeginDate(agora);
        if (taskVersion.isEnd()) {
            flowInstance.setEndDate(agora);
            taskInstance.setEndDate(agora);
        } else {
            flowInstance.setEndDate(null);
        }

        flowInstance.addTask(taskInstance);
        SessionWrapper sw = getSession();

        sw.save(taskInstance);
        sw.update(flowInstance);
        sw.refresh(flowInstance);
        return taskInstance;
    }

    @Override
    public void setParentTask(FLOW_INSTANCE childrenInstance, TASK_INSTANCE parentTask) {
        childrenInstance.setParentTask(parentTask);
        getSession().update(childrenInstance);
    }

    @Override
    public void updateTask(TASK_INSTANCE taskInstance) {
        getSession().update(taskInstance);
    }

    @Override
    public void completeTask(TASK_INSTANCE task, String transitionAbbreviation, SUser responsibleUser) {
        SUser resolvedUser = saveUserIfNeeded(responsibleUser);
        task.setEndDate(new Date());
        IEntityTaskTransitionVersion transition = task.getTaskVersion().getTransition(transitionAbbreviation);
        task.setExecutedTransition(transition);

        if (resolvedUser != null) {
            task.setResponsibleUser(resolvedUser);
        }

        getSession().update(task);
    }

    @Override
    public void updateTargetEndDate(TASK_INSTANCE taskInstance, Date targetEndDate) {
        taskInstance.setTargetEndDate(targetEndDate);
        updateTask(taskInstance);
    }

    @Override
    public void relocateTask(TASK_INSTANCE taskInstance, SUser user) {
        taskInstance.setAllocatedUser(saveUserIfNeeded(user));
        updateTask(taskInstance);
    }

    protected abstract IEntityTaskInstanceHistory newTaskInstanceHistory(TASK_INSTANCE task, IEntityTaskHistoricType taskHistoryType,
            SUser allocatedUser, SUser responsibleUser);

    @Override
    public IEntityTaskInstanceHistory saveTaskHistoricLog(TASK_INSTANCE task, String typeDescription, String detail, SUser allocatedUser,
            SUser responsibleUser, Date dateHour, FLOW_INSTANCE generatedFlowInstance) {
        IEntityTaskHistoricType taskHistoryType = retrieveOrCreateTaskHistoricType(typeDescription);

        SUser resolvedUser = saveUserIfNeeded(responsibleUser);
        IEntityTaskInstanceHistory history = newTaskInstanceHistory(task, taskHistoryType, allocatedUser, resolvedUser);

        history.setAllocationStartDate(dateHour == null ? new Date() : dateHour);
        history.setDescription(detail);

        SessionWrapper sw = getSession();
        sw.save(history);
        sw.refresh(task);

        return history;
    }

    protected abstract Class<? extends IEntityTaskHistoricType> getClassEntityTaskHistoricType();

    protected final IEntityTaskHistoricType retrieveOrCreateTaskHistoricType(String typeDescription) {
        SessionWrapper sw = getSession();
        Class<? extends IEntityTaskHistoricType> entityClass = getClassEntityTaskHistoricType();

        IEntityTaskHistoricType variableType = sw.retrieveFirstFromCachedRetrieveAll(entityClass,
                vt -> vt.getDescription().equals(typeDescription));

        if (variableType == null) {
            try {
                variableType = entityClass.newInstance();
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
    public FLOW_VERSION saveFlowVersion(FLOW_VERSION flowVersion) {
        SessionWrapper sw = getSession();
        sw.saveOrUpdate(flowVersion.getFlowDefinition());
        sw.saveOrUpdate(flowVersion);
        sw.saveOrUpdate(flowVersion.getVersionTasks().stream().map(tv -> tv.getTaskDefinition()));
        sw.saveOrUpdate(flowVersion
                .getVersionTasks().stream().flatMap(tv -> tv.getTaskDefinition().getRolesTask() != null ? tv.getTaskDefinition().getRolesTask().stream() : Stream.empty()));
        sw.saveOrUpdate(flowVersion.getVersionTasks());
        sw.saveOrUpdate(flowVersion.getVersionTasks().stream().flatMap(tv -> tv.getTransitions().stream()));
        
        sw.refresh(flowVersion.getFlowDefinition());
        
        return retrieveFlowVersionByCod(flowVersion.getCod());
    }

    // -------------------------------------------------------
    // Variable
    // -------------------------------------------------------

    @Nonnull
    protected abstract Optional<VARIABLE_INSTANCE> retrieveVariableInstanceByCod(@Nonnull Integer cod);

    @Nonnull
    protected VARIABLE_INSTANCE retrieveVariableInstanceByCodOrException(@Nonnull Integer cod) {
        return retrieveVariableInstanceByCod(cod).orElseThrow(
                () -> new SingularFlowException("Não foi encontrado a variável cod=" + cod));
    }

    protected abstract VARIABLE_INSTANCE newVariableInstance(FLOW_INSTANCE flowInstance, String name);

    @Override
    @Nullable
    public Integer updateVariableValue(@Nonnull FLOW_INSTANCE flowInstance, @Nonnull VarInstance variable, @Nullable Integer dbVariableCod) {
        SessionWrapper ss = getSession();
        Object adjustedValue = variable.getValue();
        VARIABLE_INSTANCE eVariable = null;
        if (dbVariableCod != null) {
            eVariable = retrieveVariableInstanceByCodOrException(dbVariableCod);
        }

        if (adjustedValue == null || isVazio(adjustedValue)) {
            if (eVariable != null) {
                eVariable.setValue(null);
                ss.merge(eVariable);
                ss.refresh(flowInstance);
            } else {
                return null;
            }
        } else if (eVariable == null) {
            // Para não forçar carga
            eVariable = newVariableInstance(flowInstance, variable.getRef());

            String stringValue = variable.getPersistentString();
            if (!Objects.equals(stringValue, eVariable.getValue())) {
                eVariable.setType(retrieveOrCreateEntityVariableType(variable.getType()));
                eVariable.setValue(stringValue);
            }

            ss.save(eVariable);
            ss.refresh(flowInstance);
        } else {
            String stringValue = variable.getPersistentString();
            if (!Objects.equals(stringValue, eVariable.getValue())) {
                eVariable.setType(retrieveOrCreateEntityVariableType(variable.getType()));
                eVariable.setValue(stringValue);
                ss.merge(eVariable);
            }
        }
        return eVariable.getCod();
    }

    protected abstract IEntityExecutionVariable newExecutionVariable(FLOW_INSTANCE instance, IEntityVariableInstance flowInstanceVar,
            TASK_INSTANCE originTask, TASK_INSTANCE destinationTask, IEntityVariableType type);

    @Override
    public void saveVariableHistoric(Date dateHour, FLOW_INSTANCE instance, TASK_INSTANCE originTask, TASK_INSTANCE destinationTask,
            VarInstanceMap<?,?> instanceMap) {
        if (instanceMap == null) {
            return;
        }
        SessionWrapper ss = getSession();

        boolean saved = false;
        for (VarInstance variavel : instanceMap) {
            if (variavel.getValue() != null) {
                try {
                    saveOneVariable(ss, instance, originTask, destinationTask, variavel, dateHour);
                } catch (Exception e) {
                    throw SingularFlowException.rethrow(
                            "Erro ao salvar variável '" + variavel.getName() + "' no histórico", e);
                }
                saved = true;
            }
        }
        if (saved) {
            if (originTask != null) {
                ss.refresh(originTask);
            }
            if (destinationTask != null) {
                ss.refresh(destinationTask);
            }
        }
    }

    private void saveOneVariable(SessionWrapper ss, FLOW_INSTANCE instance, TASK_INSTANCE originTask,
            TASK_INSTANCE destinationTask, VarInstance var, Date dateHour) {
        IEntityVariableType type = retrieveOrCreateEntityVariableType(var.getType());
        String ref = var.getRef();
        IEntityVariableInstance flowInstanceVar = instance.getVariable(ref);

        IEntityExecutionVariable entity = newExecutionVariable(instance, flowInstanceVar, originTask, destinationTask, type);
        entity.setName(ref);
        entity.setValue(var.getPersistentString());
        entity.setDate(dateHour);
        ss.save(entity);
    }

    protected abstract Class<? extends IEntityVariableType> getClassEntityVariableType();

    @Override
    public final IEntityVariableType retrieveOrCreateEntityVariableType(VarType varType) {
        SessionWrapper sw = getSession();
        Class<? extends IEntityVariableType> entityClass = getClassEntityVariableType();
        String typeClassName = varType.getClass().getName();

        IEntityVariableType variableType = sw.retrieveFirstFromCachedRetrieveAll(entityClass, vt -> vt.getTypeClassName().equals(typeClassName));

        if (variableType == null) {
            try {
                variableType = entityClass.newInstance();
            } catch (Exception e) {
                throw Throwables.propagate(e);
            }
            variableType.setDescription(varType.getName());
            variableType.setTypeClassName(typeClassName);
            sw.save(variableType);
        }
        return variableType;
    }

    protected abstract Class<FLOW_INSTANCE> getClassFlowInstance();
    
    public List<FLOW_INSTANCE> retrieveFlowInstancesWith(FLOW_DEFINITION flowDefinition, Date startDate, Date endDate, java.util.Collection<? extends TASK_DEF> states) {
        Objects.requireNonNull(flowDefinition);
        final Criteria c = getSession().createCriteria(getClassFlowInstance(), "PI");
        c.createAlias("PI.flowVersion", "DEF");
        c.add(Restrictions.eq("DEF.flowDefinition", flowDefinition));
        if (states != null && !states.isEmpty()) {
            DetachedCriteria sub = DetachedCriteria.forClass(getClassTaskInstance(), "T");
            sub.add(Restrictions.eqProperty("T.flowInstance.cod", "PI.cod"));
            sub.createAlias("T.task", "TV");
            sub.add(Restrictions.in("TV.taskDefinition", states));
            sub.add(Restrictions.isNull("T.endDate"));
            sub.setProjection(Projections.id());
            
            c.add(Subqueries.exists(sub));
        }
        if (startDate != null && endDate != null) {
            c.add(Restrictions.or(
                Restrictions.and(Restrictions.ge("PI.beginDate", startDate), Restrictions.lt("PI.beginDate", endDate)),
                Restrictions.and(Restrictions.ge("PI.endDate", startDate), Restrictions.lt("PI.endDate", endDate)),
                Restrictions.and(Restrictions.lt("PI.beginDate", startDate), Restrictions.ge("PI.endDate", startDate)),
                Restrictions.and(Restrictions.isNull("PI.endDate"), Restrictions.lt("PI.beginDate", endDate))));
        } else if(startDate != null){
            c.add(Restrictions.or(
                Restrictions.ge("PI.beginDate", startDate),
                Restrictions.ge("PI.endDate", startDate),
                Restrictions.and(Restrictions.lt("PI.beginDate", startDate), Restrictions.isNull("PI.endDate"))));
        } else if (endDate != null) {
            c.add(Restrictions.or(Restrictions.le("PI.beginDate", endDate), Restrictions.le("PI.endDate", endDate)));
        }
        c.addOrder(Order.desc("PI.beginDate"));
        return c.list();
    }

    public List<FLOW_INSTANCE> retrieveFlowInstancesWith(FLOW_DEFINITION flowDefinition, SUser creatingUser, Boolean active) {
        Objects.requireNonNull(flowDefinition);
        Criteria c = getSession().createCriteria(getClassFlowInstance(), "PI");
        c.createAlias("PI.flowVersion", "DEF");
        c.add(Restrictions.eq("DEF.flowDefinition", flowDefinition));

        if (active != null) {
            DetachedCriteria sub = DetachedCriteria.forClass(getClassTaskInstance(), "T");
            sub.createAlias("T.task", "TA");
            sub.add(Restrictions.eqProperty("T.flowInstance.cod", "PI.cod"));
            sub.add(Restrictions.isNull("T.endDate"));
            if (active) {
                sub.add(Restrictions.ne("TA.type", TaskType.END));
            } else {
                sub.add(Restrictions.eq("TA.type", TaskType.END));
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
        entityTaskInstance.setAllocatedUser(null);
        getSession().saveOrUpdate(entityTaskInstance);
        List<? extends IEntityTaskInstanceHistory> histories = entityTaskInstance.getTaskHistory();
        for (ListIterator<? extends IEntityTaskInstanceHistory> it = histories.listIterator(histories.size()); it.hasPrevious(); ) {
            IEntityTaskInstanceHistory history = it.previous();
            if (history.getType().getDescription().toLowerCase().contains(TaskInstance.ALLOCATE.toLowerCase())) {
                history.setAllocationEndDate(new Date());
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
