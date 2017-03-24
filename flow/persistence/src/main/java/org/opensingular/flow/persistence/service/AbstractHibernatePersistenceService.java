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
import org.opensingular.flow.core.entity.IEntityProcessDefinition;
import org.opensingular.flow.core.entity.IEntityProcessInstance;
import org.opensingular.flow.core.entity.IEntityProcessVersion;
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
    @Nonnull
    public Optional<PROCESS_INSTANCE> retrieveProcessInstanceByCod(@Nonnull Integer cod) {
        Objects.requireNonNull(cod);
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

    protected abstract ROLE_USER newEntityRole(PROCESS_INSTANCE instance, PROCESS_ROLE role, SUser user, SUser allocator);

    @Override
    public ROLE_USER setInstanceUserRole(PROCESS_INSTANCE instance, PROCESS_ROLE role, SUser user) {
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

    public SUser saveUserIfNeeded(SUser sUser) {
        return Flow.getConfigBean().getUserService().saveUserIfNeeded(sUser);
    }

    protected abstract Class<TASK_INSTANCE> getClassTaskInstance();
    
    /**
     * Cria uma nova taskInstance parcialmente preenchiada apenas com
     * processIntance e taskVersion.
     */
    protected abstract TASK_INSTANCE newTaskInstance(PROCESS_INSTANCE processInstance, TASK_VERSION taskVersion);

    @Override
    @Nonnull
    public Optional<TASK_INSTANCE> retrieveTaskInstanceByCod(@Nonnull Integer cod) {
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
    public void completeTask(TASK_INSTANCE task, String transitionAbbreviation, SUser responsibleUser) {
        responsibleUser = saveUserIfNeeded(responsibleUser);
        task.setEndDate(new Date());
        IEntityTaskTransitionVersion transition = task.getTaskVersion().getTransition(transitionAbbreviation);
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
    public void relocateTask(TASK_INSTANCE taskInstance, SUser user) {
        user = saveUserIfNeeded(user);
        taskInstance.setAllocatedUser(user);

        updateTask(taskInstance);
    }

    protected abstract IEntityTaskInstanceHistory newTaskInstanceHistory(TASK_INSTANCE task, IEntityTaskHistoricType taskHistoryType,
            SUser allocatedUser, SUser responsibleUser);

    @Override
    public IEntityTaskInstanceHistory saveTaskHistoricLog(TASK_INSTANCE task, String typeDescription, String detail, SUser allocatedUser,
            SUser responsibleUser, Date dateHour, PROCESS_INSTANCE generatedProcessInstance) {
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
        sw.saveOrUpdate(processVersion.getVersionTasks().stream().flatMap(tv -> tv.getTaskDefinition().getRolesTask() != null ? tv.getTaskDefinition().getRolesTask().stream() : Stream.empty()));
        sw.saveOrUpdate(processVersion.getVersionTasks());
        sw.saveOrUpdate(processVersion.getVersionTasks().stream().flatMap(tv -> tv.getTransitions().stream()));
        
        sw.refresh(processVersion.getProcessDefinition());
        
        return retrieveProcessVersionByCod(processVersion.getCod());
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

    protected abstract VARIABLE_INSTANCE newVariableInstance(PROCESS_INSTANCE processInstance, String name);

    @Override
    @Nullable
    public Integer updateVariableValue(@Nonnull PROCESS_INSTANCE processInstance, @Nonnull VarInstance mVariavel, @Nullable Integer dbVariableCod) {
        SessionWrapper ss = getSession();
        Object valorAjustado = mVariavel.getValue();
        VARIABLE_INSTANCE variavel = null;
        if (dbVariableCod != null) {
            variavel = retrieveVariableInstanceByCodOrException(dbVariableCod);
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

            String valorString = mVariavel.getPersistentString();
            if (!Objects.equals(valorString, variavel.getValue())) {
                variavel.setType(retrieveOrCreateEntityVariableType(mVariavel.getType()));
                variavel.setValue(valorString);
            }

            ss.save(variavel);
            ss.refresh(processInstance);
        } else {
            String valorString = mVariavel.getPersistentString();
            if (!Objects.equals(valorString, variavel.getValue())) {
                variavel.setType(retrieveOrCreateEntityVariableType(mVariavel.getType()));
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
            VarInstanceMap<?,?> instanceMap) {
        if (instanceMap != null) {
            SessionWrapper ss = getSession();

            boolean salvou = false;
            for (VarInstance variavel : instanceMap) {
                if (variavel.getValue() != null) {

                    IEntityVariableType type = retrieveOrCreateEntityVariableType(variavel.getType());
                    String ref = variavel.getRef();
                    IEntityVariableInstance processInstanceVar = instance.getVariable(ref);

                    IEntityExecutionVariable novo = newExecutionVariable(instance, processInstanceVar, originTask, destinationTask, type);
                    novo.setName(ref);
                    novo.setValue(variavel.getPersistentString());
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

    public List<PROCESS_INSTANCE> retrieveProcessInstancesWith(PROCESS_DEF process, SUser creatingUser, Boolean active) {
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
