/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.persistence.service;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.net.mirante.singular.flow.core.Flow;
import br.net.mirante.singular.flow.core.MProcessRole;
import br.net.mirante.singular.flow.core.MTask;
import br.net.mirante.singular.flow.core.MTransition;
import br.net.mirante.singular.flow.core.ProcessDefinition;
import br.net.mirante.singular.flow.core.SingularFlowException;
import br.net.mirante.singular.flow.core.entity.IEntityCategory;
import br.net.mirante.singular.flow.core.entity.IEntityProcessDefinition;
import br.net.mirante.singular.flow.core.entity.IEntityProcessGroup;
import br.net.mirante.singular.flow.core.entity.IEntityProcessVersion;
import br.net.mirante.singular.flow.core.entity.IEntityRoleDefinition;
import br.net.mirante.singular.flow.core.entity.IEntityRoleInstance;
import br.net.mirante.singular.flow.core.entity.IEntityTaskDefinition;
import br.net.mirante.singular.flow.core.entity.IEntityTaskTransitionVersion;
import br.net.mirante.singular.flow.core.entity.IEntityTaskVersion;
import br.net.mirante.singular.flow.core.service.IProcessDefinitionEntityService;
import br.net.mirante.singular.persistence.entity.ProcessGroupEntity;
import br.net.mirante.singular.persistence.entity.util.SessionLocator;
import br.net.mirante.singular.persistence.entity.util.SessionWrapper;

public abstract class AbstractHibernateProcessDefinitionService<CATEGORY extends IEntityCategory, PROCESS_DEF extends IEntityProcessDefinition, PROCESS_VERSION extends IEntityProcessVersion, TASK_DEF extends IEntityTaskDefinition, TASK_VERSION extends IEntityTaskVersion, TRANSITION extends IEntityTaskTransitionVersion, PROCESS_ROLE_DEF extends IEntityRoleDefinition, PROCESS_ROLE extends IEntityRoleInstance>
        extends AbstractHibernateService
        implements IProcessDefinitionEntityService<CATEGORY, PROCESS_DEF, PROCESS_VERSION, TASK_DEF, TASK_VERSION, TRANSITION, PROCESS_ROLE_DEF> {

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

        for (MTask<?> task : processDefinition.getFlowMap().getAllTasks()) {
            TASK_DEF entityTaskDefinition = retrieveOrCreateEntityDefinitionTask(entityProcessDefinition, task);

            TASK_VERSION entityTask = createEntityTaskVersion(entityProcessVersion, entityTaskDefinition, task);
            entityTask.setName(task.getName());

            ((List<TASK_VERSION>) entityProcessVersion.getVersionTasks()).add(entityTask);
        }
        for (MTask<?> task : processDefinition.getFlowMap().getAllTasks()) {
            TASK_VERSION originTask = (TASK_VERSION) entityProcessVersion.getTaskVersion(task.getAbbreviation());
            for (MTransition mTransition : task.getTransitions()) {
                TASK_VERSION destinationTask = (TASK_VERSION) entityProcessVersion
                        .getTaskVersion(mTransition.getDestination().getAbbreviation());

                TRANSITION entityTransition = createEntityTaskTransition(originTask, destinationTask);
                entityTransition.setAbbreviation(mTransition.getAbbreviation());
                entityTransition.setName(mTransition.getName());
                entityTransition.setType(mTransition.getType());

                ((List<TRANSITION>) originTask.getTransitions()).add(entityTransition);
            }
        }
        return entityProcessVersion;
    }

    protected abstract PROCESS_VERSION createEntityProcessVersion(PROCESS_DEF entityProcessDefinition);

    protected abstract TASK_VERSION createEntityTaskVersion(PROCESS_VERSION process, TASK_DEF entityTaskDefinition, MTask<?> task);

    protected abstract TRANSITION createEntityTaskTransition(TASK_VERSION originTask, TASK_VERSION destinationTask);


    private final PROCESS_DEF retrieveOrcreateEntityProcessDefinitionFor(ProcessDefinition<?> definicao) {
        SessionWrapper sw = getSession();
        requireNonNull(definicao);
        PROCESS_DEF def = sw.retrieveFirstFromCachedRetriveAll(getClassProcessDefinition(),
                pd -> pd.getKey().equals(definicao.getKey()));
        if (def == null) {
            def = sw.retrieveFirstFromCachedRetriveAll(getClassProcessDefinition(),
                pd -> pd.getDefinitionClassName().equals(definicao.getClass().getName()));
        }
        IEntityProcessGroup processGroup = retrieveProcessGroup();
        if (def == null) {
            def = newInstanceOf(getClassProcessDefinition());
            def.setCategory(retrieveOrCreateCategoryWith(definicao.getCategory()));
            def.setProcessGroup(processGroup);
            def.setName(definicao.getName());
            def.setKey(definicao.getKey());
            def.setDefinitionClassName(definicao.getClass().getName());

            sw.save(def);
            sw.refresh(def);
        } else {
            if(!def.getProcessGroup().equals(processGroup)){
                throw new SingularFlowException("O processo "+definicao.getName()+" esta associado a outro grupo/sistema: "+def.getProcessGroup().getCod()+" - "+def.getProcessGroup().getName());
            }
            boolean mudou = false;
            if (!definicao.getKey().equals(def.getKey())) {
                def.setKey(definicao.getKey());
                mudou = true;
            }
            if (!definicao.getName().equals(def.getName())) {
                def.setName(definicao.getName());
                mudou = true;
            }
            CATEGORY categoria = retrieveOrCreateCategoryWith(definicao.getCategory());
            if (!Objects.equals(def.getCategory(), categoria)) {
                def.setCategory(retrieveOrCreateCategoryWith(definicao.getCategory()));
                mudou = true;
            }
            if (!definicao.getClass().getName().equals(def.getDefinitionClassName())) {
                def.setDefinitionClassName(definicao.getClass().getName());
                mudou = true;
            }
            if (mudou) {
                sw.update(def);
            }
        }

        return def;
    }

    protected final IEntityProcessGroup retrieveProcessGroup() {
        IEntityProcessGroup group = getSession().retrieve(getClassProcessGroup(), Flow.getConfigBean().getProcessGroupCod());
        Objects.requireNonNull(group);
        return group;
    }

    protected Class<? extends IEntityProcessGroup> getClassProcessGroup() {
        return ProcessGroupEntity.class;
    }

    protected abstract Class<? extends PROCESS_ROLE_DEF> getClassProcessRoleDef();

    protected abstract Class<? extends PROCESS_ROLE> getClassProcessRole();

    private final void checkRoleDefChanges(ProcessDefinition<?> processDefinition, PROCESS_DEF entityProcessDefinition) {
        SessionWrapper sw = getSession();

        Set<String> abbreviations = new HashSet<>();
        for (IEntityRoleDefinition role : new ArrayList<>(entityProcessDefinition.getRoles())) {
            MProcessRole roleAbbreviation = processDefinition.getFlowMap().getRoleWithAbbreviation(role.getAbbreviation());
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

        for (MProcessRole mPapel : processDefinition.getFlowMap().getRoles()) {
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

    private final TASK_DEF retrieveOrCreateEntityDefinitionTask(PROCESS_DEF process, MTask<?> task) {
        IEntityTaskDefinition taskDefinition = process.getTaskDefinition(task.getAbbreviation());
        if (taskDefinition == null) {
            taskDefinition = createEntityDefinitionTask(process);
            taskDefinition.setAbbreviation(task.getAbbreviation());
            if (task.getAccessStrategy() != null) {
                taskDefinition.setAccessStrategyType(task.getAccessStrategy().getType());
            }
        }
        return (TASK_DEF) taskDefinition;
    }

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

    private boolean isNewVersion(IEntityTaskTransitionVersion oldEntityTaskTransition, IEntityTaskTransitionVersion newEntityTaskTransition) {
        return oldEntityTaskTransition == null || !oldEntityTaskTransition.getName().equalsIgnoreCase(newEntityTaskTransition.getName())
                || !oldEntityTaskTransition.getAbbreviation().equalsIgnoreCase(newEntityTaskTransition.getAbbreviation())
                || oldEntityTaskTransition.getType() != newEntityTaskTransition.getType() || !oldEntityTaskTransition.getDestinationTask()
                        .getAbbreviation().equalsIgnoreCase(newEntityTaskTransition.getDestinationTask().getAbbreviation());
    }
}
