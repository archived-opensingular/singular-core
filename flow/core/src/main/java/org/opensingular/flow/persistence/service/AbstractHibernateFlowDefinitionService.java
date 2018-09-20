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

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.opensingular.flow.core.Flow;
import org.opensingular.flow.core.FlowDefinition;
import org.opensingular.flow.core.FlowInstance;
import org.opensingular.flow.core.SBusinessRole;
import org.opensingular.flow.core.STask;
import org.opensingular.flow.core.STransition;
import org.opensingular.flow.core.SingularFlowException;
import org.opensingular.flow.core.TaskAccessStrategy;
import org.opensingular.flow.core.entity.IEntityCategory;
import org.opensingular.flow.core.entity.IEntityFlowDefinition;
import org.opensingular.flow.core.entity.IEntityFlowVersion;
import org.opensingular.flow.core.entity.IEntityModule;
import org.opensingular.flow.core.entity.IEntityRoleDefinition;
import org.opensingular.flow.core.entity.IEntityRoleInstance;
import org.opensingular.flow.core.entity.IEntityRoleTask;
import org.opensingular.flow.core.entity.IEntityTaskDefinition;
import org.opensingular.flow.core.entity.IEntityTaskTransitionVersion;
import org.opensingular.flow.core.entity.IEntityTaskVersion;
import org.opensingular.flow.core.entity.TransitionType;
import org.opensingular.flow.core.service.IFlowDefinitionEntityService;
import org.opensingular.flow.persistence.entity.ModuleEntity;
import org.opensingular.flow.persistence.entity.util.SessionWrapper;
import org.opensingular.lib.support.persistence.SessionLocator;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static java.util.Objects.requireNonNull;

public abstract class AbstractHibernateFlowDefinitionService<CATEGORY extends IEntityCategory, FLOW_DEFINITION extends IEntityFlowDefinition, FLOW_VERSION extends IEntityFlowVersion, TASK_DEF extends IEntityTaskDefinition, TASK_VERSION extends IEntityTaskVersion, TRANSITION extends IEntityTaskTransitionVersion, ROLE_DEFINITION extends IEntityRoleDefinition, ROLE_INSTANCE extends IEntityRoleInstance, ROLE_TASK extends IEntityRoleTask>
        extends AbstractHibernateService
        implements
        IFlowDefinitionEntityService<CATEGORY, FLOW_DEFINITION, FLOW_VERSION, TASK_DEF, TASK_VERSION, TRANSITION, ROLE_DEFINITION, ROLE_TASK> {

    public AbstractHibernateFlowDefinitionService(SessionLocator sessionLocator) {
        super(sessionLocator);
    }

    protected abstract Class<? extends FLOW_DEFINITION> getClassFlowDefinition();

    @Override
    public FLOW_VERSION generateEntityFor(FlowDefinition<?> flowDefinition) {
        FLOW_DEFINITION entityFlowDefinition = retrieveOrcreateEntityFlowDefinitionFor(flowDefinition);

        checkRoleDefChanges(flowDefinition, entityFlowDefinition);

        FLOW_VERSION entityFlowVersion = createEntityFlowVersion(entityFlowDefinition);
        entityFlowVersion.setVersionDate(new Date());

        for (STask<?> task : flowDefinition.getFlowMap().getAllTasks()) {
            TASK_DEF entityTaskDefinition = retrieveOrCreateEntityDefinitionTask(entityFlowDefinition, task);

            TASK_VERSION entityTask = createEntityTaskVersion(entityFlowVersion, entityTaskDefinition, task);
            entityTask.setName(task.getName());

            ((List<TASK_VERSION>) entityFlowVersion.getVersionTasks()).add(entityTask);
        }
        for (STask<?> task : flowDefinition.getFlowMap().getAllTasks()) {
            TASK_VERSION originTask = (TASK_VERSION) entityFlowVersion.getTaskVersion(task.getAbbreviation());
            for (STransition sTransition : task.getTransitions()) {
                TASK_VERSION destinationTask = (TASK_VERSION) entityFlowVersion
                        .getTaskVersion(sTransition.getDestination().getAbbreviation());

                TRANSITION entityTransition = createEntityTaskTransition(originTask, destinationTask);
                entityTransition.setAbbreviation(sTransition.getAbbreviation());
                entityTransition.setName(sTransition.getName());
                entityTransition.setType(getType(sTransition));

                ((List<TRANSITION>) originTask.getTransitions()).add(entityTransition);
            }
        }
        return entityFlowVersion;
    }

    private TransitionType getType(STransition transition) {
        if (transition.getPredicate() != null) {
            return TransitionType.A;
        } else if (transition.getDisplayInfo().getDisplayEventType() != null) {
            return TransitionType.E;
        }
        return TransitionType.H;
    }

    protected abstract FLOW_VERSION createEntityFlowVersion(FLOW_DEFINITION entityFlowDefinition);

    protected abstract TASK_VERSION createEntityTaskVersion(FLOW_VERSION flowVersion, TASK_DEF entityTaskDefinition, STask<?> task);

    protected abstract TRANSITION createEntityTaskTransition(TASK_VERSION originTask, TASK_VERSION destinationTask);


    private final FLOW_DEFINITION retrieveOrcreateEntityFlowDefinitionFor(FlowDefinition<?> definition) {
        SessionWrapper sw = getSession();
        requireNonNull(definition);
        String key = definition.getKey();
        FLOW_DEFINITION def = sw.retrieveFirstFromCachedRetrieveAll(getClassFlowDefinition(),
                pd -> pd.getKey().equals(key));
        if (def == null) {
            def = sw.retrieveFirstFromCachedRetrieveAll(getClassFlowDefinition(),
                pd -> pd.getDefinitionClassName().equals(definition.getClass().getName()));
        }
        IEntityModule module = retrieveModule();
        String        name         = definition.getName();
        String        category     = definition.getCategory();
        if (def == null) {
            def = newInstanceOf(getClassFlowDefinition());
            def.setCategory(retrieveOrCreateCategoryWith(category));
            def.setModule(module);
            def.setName(name);
            def.setKey(key);
            def.setDefinitionClassName(definition.getClass().getName());

            sw.save(def);
            sw.refresh(def);
        } else {
            if(!def.getModule().equals(module)){
                throw new SingularFlowException("O fluxo "+ name +" esta associado a outro grupo/sistema: "+def.getModule().getCod()+" - "+def.getModule().getName());
            }
            boolean mudou = false;
            if (!key.equals(def.getKey())) {
                def.setKey(key);
                mudou = true;
            }
            if (!name.equals(def.getName())) {
                def.setName(name);
                mudou = true;
            }
            CATEGORY categoria = retrieveOrCreateCategoryWith(category);
            if (!Objects.equals(def.getCategory(), categoria)) {
                def.setCategory(retrieveOrCreateCategoryWith(category));
                mudou = true;
            }
            if (!Objects.equals(def.getDefinitionClassName(), definition.getClass().getName())) {
                def.setDefinitionClassName(definition.getClass().getName());
                mudou = true;
            }
            if (mudou) {
                sw.update(def);
            }
        }

        return def;
    }

    @Nonnull
    protected final IEntityModule retrieveModule() {
        return getSession().retrieveOrException(getClassModule(), Flow.getConfigBean().getModuleCod());
    }

    protected Class<? extends IEntityModule> getClassModule() {
        return ModuleEntity.class;
    }

    protected abstract Class<? extends ROLE_DEFINITION> getClassRoleDefinition();

    protected abstract Class<? extends ROLE_INSTANCE> getClassRoleInstance();

    private final void checkRoleDefChanges(FlowDefinition<?> flowDefinition, FLOW_DEFINITION entityFlowDefinition) {
        SessionWrapper sw = getSession();

        Set<String> abbreviations = new HashSet<>();
        for (IEntityRoleDefinition role : new ArrayList<>(entityFlowDefinition.getRoles())) {
            SBusinessRole roleAbbreviation = flowDefinition.getFlowMap().getRoleWithAbbreviation(role.getAbbreviation());
            if (roleAbbreviation == null) {
                if (!hasRoleInstances(sw, role)) {
                    entityFlowDefinition.getRoles().remove(role);
                    sw.delete(role);
                }
            } else {
                if (!role.getName().equals(roleAbbreviation.getName())
                        || !role.getAbbreviation().equals(roleAbbreviation.getAbbreviation())) {
                    role.setName(roleAbbreviation.getName());
                    role.setAbbreviation(roleAbbreviation.getAbbreviation());
                    sw.update(role);
                }
                abbreviations.add(role.getAbbreviation());
            }
        }

        for (SBusinessRole bRole : flowDefinition.getFlowMap().getRoles()) {
            if (!abbreviations.contains(bRole.getAbbreviation())) {
                ROLE_DEFINITION role = newInstanceOf(getClassRoleDefinition());
                role.setFlowDefinition(entityFlowDefinition);
                role.setName(bRole.getName());
                role.setAbbreviation(bRole.getAbbreviation());
                sw.save(role);
            }
        }
        sw.refresh(entityFlowDefinition);
    }
    
    private boolean hasRoleInstances(SessionWrapper sw, IEntityRoleDefinition role){
        Criteria criteria = sw.createCriteria(getClassRoleInstance());
        criteria.add(Restrictions.eq("role", role));
        criteria.setProjection(Projections.rowCount());
        return ((Number)criteria.uniqueResult()).doubleValue() > 0;
    }
    
    protected abstract Class<? extends CATEGORY> getClassCategory();

    private final CATEGORY retrieveOrCreateCategoryWith(String name) {
        requireNonNull(name);
        SessionWrapper sw = getSession();
        CATEGORY category = sw.retrieveFirstFromCachedRetrieveAll(getClassCategory(), cat -> cat.getName().equals(name));
        if (category == null) {
            category = newInstanceOf(getClassCategory());
            category.setName(name);
            sw.save(category);
        }
        return category;
    }

    protected abstract TASK_DEF createEntityDefinitionTask(FLOW_DEFINITION flow);

    private final TASK_DEF retrieveOrCreateEntityDefinitionTask(FLOW_DEFINITION flow, STask<?> task) {

        String abbreviation = task.getAbbreviation();
        TASK_DEF taskDefinition = (TASK_DEF) flow.getTaskDefinition(abbreviation);

        if (taskDefinition == null) {
            taskDefinition = createEntityDefinitionTask(flow);
            taskDefinition.setAbbreviation(abbreviation);

            TaskAccessStrategy<FlowInstance> accessStrategy = task.getAccessStrategy();

            if (accessStrategy != null) {
                taskDefinition.setAccessStrategyType(accessStrategy.getType());
                List<String> roles = accessStrategy.getVisualizeRoleNames(task.getFlowMap().getFlowDefinition(), task);
                addRolesToTaks(flow, taskDefinition, roles);
            }
        }
        return taskDefinition;
    }

    private void addRolesToTaks(FLOW_DEFINITION flow, TASK_DEF taskDefinition, @Nonnull List<String> roles) {
        for (String roleName : roles) {
            ROLE_DEFINITION roleDefinition = null;
            for (IEntityRoleDefinition rd : new ArrayList<>(flow.getRoles())) {
                if (roleName.toUpperCase().endsWith(rd.getName().toUpperCase())) {
                    roleDefinition = (ROLE_DEFINITION) rd;
                    break;
                }
            }
            addRoleToTask(roleDefinition, taskDefinition);
        }
    }

    protected abstract ROLE_TASK addRoleToTask(ROLE_DEFINITION roleDefinition, TASK_DEF taskDefinition);

    @Override
    public boolean isDifferentVersion(IEntityFlowVersion oldEntity, IEntityFlowVersion newEntity) {
        if (oldEntity == null || oldEntity.getVersionTasks().size() != newEntity.getVersionTasks().size()) {
            return true;
        }
        for (IEntityTaskVersion newEntitytask : newEntity.getVersionTasks()) {
            IEntityTaskVersion oldEntityTask = oldEntity.getTaskVersion(newEntitytask.getAbbreviation());
            if (isNewVersion(oldEntityTask, newEntitytask)) {
                return true;
            }
        }
        return false;
    }

    private boolean isNewVersion(IEntityTaskVersion oldEntityTask, IEntityTaskVersion newEntitytask) {
        if (oldEntityTask == null || !oldEntityTask.getName().equalsIgnoreCase(newEntitytask.getName())
                || !oldEntityTask.getType().getAbbreviation().equals(newEntitytask.getType().getAbbreviation())
                || oldEntityTask.getTransitions().size() != newEntitytask.getTransitions().size()) {
            return true;
        }
        for (IEntityTaskTransitionVersion newEntityTaskTransition : newEntitytask.getTransitions()) {
            IEntityTaskTransitionVersion oldEntityTaskTransition = oldEntityTask.getTransition(newEntityTaskTransition.getAbbreviation());
            if (isNewVersion(oldEntityTaskTransition, newEntityTaskTransition)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isNewVersion(IEntityTaskTransitionVersion oldTaskTransition, IEntityTaskTransitionVersion newTaskTransition) {
        //@formatter:off
        return oldTaskTransition == null ||
                isDifferentTask(oldTaskTransition, newTaskTransition) ||
                isDifferentDestinationTask(oldTaskTransition, newTaskTransition);
        //@formatter:on
    }

    private static boolean isDifferentTask(IEntityTaskTransitionVersion oldTaskTransition, IEntityTaskTransitionVersion newTaskTransition) {
        return !oldTaskTransition.getName().equalsIgnoreCase(newTaskTransition.getName()) ||
                !oldTaskTransition.getAbbreviation().equalsIgnoreCase(newTaskTransition.getAbbreviation()) ||
                oldTaskTransition.getType() != newTaskTransition.getType();
    }

    private static boolean isDifferentDestinationTask(IEntityTaskTransitionVersion oldTaskTransition, IEntityTaskTransitionVersion newTaskTransition) {
        return !oldTaskTransition.getDestinationTask().getAbbreviation().equalsIgnoreCase(newTaskTransition.getDestinationTask().getAbbreviation());
    }
}
