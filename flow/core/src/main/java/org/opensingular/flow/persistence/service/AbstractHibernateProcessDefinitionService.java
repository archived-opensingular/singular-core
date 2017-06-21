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
import org.opensingular.flow.core.ProcessDefinition;
import org.opensingular.flow.core.ProcessInstance;
import org.opensingular.flow.core.SProcessRole;
import org.opensingular.flow.core.STask;
import org.opensingular.flow.core.STransition;
import org.opensingular.flow.core.SingularFlowException;
import org.opensingular.flow.core.TaskAccessStrategy;
import org.opensingular.flow.core.entity.IEntityCategory;
import org.opensingular.flow.core.entity.IEntityModule;
import org.opensingular.flow.core.entity.IEntityProcessDefinition;
import org.opensingular.flow.core.entity.IEntityProcessVersion;
import org.opensingular.flow.core.entity.IEntityRoleDefinition;
import org.opensingular.flow.core.entity.IEntityRoleInstance;
import org.opensingular.flow.core.entity.IEntityRoleTask;
import org.opensingular.flow.core.entity.IEntityTaskDefinition;
import org.opensingular.flow.core.entity.IEntityTaskTransitionVersion;
import org.opensingular.flow.core.entity.IEntityTaskVersion;
import org.opensingular.flow.core.service.IProcessDefinitionEntityService;
import org.opensingular.flow.persistence.entity.ModuleEntity;
import org.opensingular.flow.persistence.entity.util.SessionLocator;
import org.opensingular.flow.persistence.entity.util.SessionWrapper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static java.util.Objects.requireNonNull;

public abstract class AbstractHibernateProcessDefinitionService<CATEGORY extends IEntityCategory, PROCESS_DEF extends IEntityProcessDefinition, PROCESS_VERSION extends IEntityProcessVersion, TASK_DEF extends IEntityTaskDefinition, TASK_VERSION extends IEntityTaskVersion, TRANSITION extends IEntityTaskTransitionVersion, PROCESS_ROLE_DEF extends IEntityRoleDefinition, PROCESS_ROLE extends IEntityRoleInstance, ROLE_TASK extends IEntityRoleTask>
        extends AbstractHibernateService
        implements IProcessDefinitionEntityService<CATEGORY, PROCESS_DEF, PROCESS_VERSION, TASK_DEF, TASK_VERSION, TRANSITION, PROCESS_ROLE_DEF, ROLE_TASK> {

    public AbstractHibernateProcessDefinitionService(SessionLocator sessionLocator) {
        super(sessionLocator);
    }

    protected abstract Class<? extends PROCESS_DEF> getClassProcessDefinition();

    @Override
    public PROCESS_VERSION generateEntityFor(ProcessDefinition<?> processDefinition) {
        PROCESS_DEF entityProcessDefinition = retrieveOrcreateEntityProcessDefinitionFor(processDefinition);

        checkRoleDefChanges(processDefinition, entityProcessDefinition);

        PROCESS_VERSION entityProcessVersion = createEntityProcessVersion(entityProcessDefinition);
        entityProcessVersion.setVersionDate(new Date());

        for (STask<?> task : processDefinition.getFlowMap().getAllTasks()) {
            TASK_DEF entityTaskDefinition = retrieveOrCreateEntityDefinitionTask(entityProcessDefinition, task);

            TASK_VERSION entityTask = createEntityTaskVersion(entityProcessVersion, entityTaskDefinition, task);
            entityTask.setName(task.getName());

            ((List<TASK_VERSION>) entityProcessVersion.getVersionTasks()).add(entityTask);
        }
        for (STask<?> task : processDefinition.getFlowMap().getAllTasks()) {
            TASK_VERSION originTask = (TASK_VERSION) entityProcessVersion.getTaskVersion(task.getAbbreviation());
            for (STransition sTransition : task.getTransitions()) {
                TASK_VERSION destinationTask = (TASK_VERSION) entityProcessVersion
                        .getTaskVersion(sTransition.getDestination().getAbbreviation());

                TRANSITION entityTransition = createEntityTaskTransition(originTask, destinationTask);
                entityTransition.setAbbreviation(sTransition.getAbbreviation());
                entityTransition.setName(sTransition.getName());
                entityTransition.setType(sTransition.getType());

                ((List<TRANSITION>) originTask.getTransitions()).add(entityTransition);
            }
        }
        return entityProcessVersion;
    }

    protected abstract PROCESS_VERSION createEntityProcessVersion(PROCESS_DEF entityProcessDefinition);

    protected abstract TASK_VERSION createEntityTaskVersion(PROCESS_VERSION process, TASK_DEF entityTaskDefinition, STask<?> task);

    protected abstract TRANSITION createEntityTaskTransition(TASK_VERSION originTask, TASK_VERSION destinationTask);


    private final PROCESS_DEF retrieveOrcreateEntityProcessDefinitionFor(ProcessDefinition<?> definicao) {
        SessionWrapper sw = getSession();
        requireNonNull(definicao);
        String key = definicao.getKey();
        PROCESS_DEF def = sw.retrieveFirstFromCachedRetriveAll(getClassProcessDefinition(),
                pd -> pd.getKey().equals(key));
        if (def == null) {
            def = sw.retrieveFirstFromCachedRetriveAll(getClassProcessDefinition(),
                pd -> pd.getDefinitionClassName().equals(definicao.getClass().getName()));
        }
        IEntityModule module = retrieveModule();
        String        name         = definicao.getName();
        String        category     = definicao.getCategory();
        if (def == null) {
            def = newInstanceOf(getClassProcessDefinition());
            def.setCategory(retrieveOrCreateCategoryWith(category));
            def.setModule(module);
            def.setName(name);
            def.setKey(key);
            def.setDefinitionClassName(definicao.getClass().getName());

            sw.save(def);
            sw.refresh(def);
        } else {
            if(!def.getModule().equals(module)){
                throw new SingularFlowException("O processo "+ name +" esta associado a outro grupo/sistema: "+def.getModule().getCod()+" - "+def.getModule().getName());
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
            if (!Objects.equals(def.getDefinitionClassName(), definicao.getClass().getName())) {
                def.setDefinitionClassName(definicao.getClass().getName());
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

    protected abstract Class<? extends PROCESS_ROLE_DEF> getClassProcessRoleDef();

    protected abstract Class<? extends PROCESS_ROLE> getClassProcessRole();

    private final void checkRoleDefChanges(ProcessDefinition<?> processDefinition, PROCESS_DEF entityProcessDefinition) {
        SessionWrapper sw = getSession();

        Set<String> abbreviations = new HashSet<>();
        for (IEntityRoleDefinition role : new ArrayList<>(entityProcessDefinition.getRoles())) {
            SProcessRole roleAbbreviation = processDefinition.getFlowMap().getRoleWithAbbreviation(role.getAbbreviation());
            if (roleAbbreviation == null) {
                if (!hasRoleInstances(sw, role)) {
                    entityProcessDefinition.getRoles().remove(role);
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

        for (SProcessRole mPapel : processDefinition.getFlowMap().getRoles()) {
            if (!abbreviations.contains(mPapel.getAbbreviation())) {
                PROCESS_ROLE_DEF role = newInstanceOf(getClassProcessRoleDef());
                role.setProcessDefinition(entityProcessDefinition);
                role.setName(mPapel.getName());
                role.setAbbreviation(mPapel.getAbbreviation());
                sw.save(role);
            }
        }
        sw.refresh(entityProcessDefinition);
    }
    
    private boolean hasRoleInstances(SessionWrapper sw, IEntityRoleDefinition role){
        Criteria criteria = sw.createCriteria(getClassProcessRole());
        criteria.add(Restrictions.eq("role", role));
        criteria.setProjection(Projections.rowCount());
        return ((Number)criteria.uniqueResult()).doubleValue() > 0;
    }
    
    protected abstract Class<? extends CATEGORY> getClassCategory();

    private final CATEGORY retrieveOrCreateCategoryWith(String name) {
        requireNonNull(name);
        SessionWrapper sw = getSession();
        CATEGORY category = sw.retrieveFirstFromCachedRetriveAll(getClassCategory(), cat -> cat.getName().equals(name));
        if (category == null) {
            category = newInstanceOf(getClassCategory());
            category.setName(name);
            sw.save(category);
        }
        return category;
    }

    protected abstract TASK_DEF createEntityDefinitionTask(PROCESS_DEF process);

    private final TASK_DEF retrieveOrCreateEntityDefinitionTask(PROCESS_DEF process, STask<?> task) {

        String abbreviation = task.getAbbreviation();
        TASK_DEF taskDefinition = (TASK_DEF) process.getTaskDefinition(abbreviation);

        if (taskDefinition == null) {
            taskDefinition = createEntityDefinitionTask(process);
            taskDefinition.setAbbreviation(abbreviation);

            TaskAccessStrategy<ProcessInstance> accessStrategy = task.getAccessStrategy();

            if (accessStrategy != null) {
                taskDefinition.setAccessStrategyType(accessStrategy.getType());
                List<String> roles = accessStrategy.getVisualizeRoleNames(task.getFlowMap().getProcessDefinition(), task);
                addRolesToTaks(process, taskDefinition, roles);
            }
        }
        return taskDefinition;
    }

    private void addRolesToTaks(PROCESS_DEF process, TASK_DEF taskDefinition, @Nonnull List<String> roles) {
        for (String roleName : roles) {
            PROCESS_ROLE_DEF roleDefinition = null;
            for (IEntityRoleDefinition rd : new ArrayList<>(process.getRoles())) {
                if (roleName.toUpperCase().endsWith(rd.getName().toUpperCase())) {
                    roleDefinition = (PROCESS_ROLE_DEF) rd;
                    break;
                }
            }
            addRoleToTask(roleDefinition, taskDefinition);
        }
    }

    protected abstract ROLE_TASK addRoleToTask(PROCESS_ROLE_DEF roleDefinition, TASK_DEF taskDefinition);

    @Override
    public boolean isDifferentVersion(IEntityProcessVersion oldEntity, IEntityProcessVersion newEntity) {
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
