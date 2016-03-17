/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.flow.core.service;

import br.net.mirante.singular.flow.core.ProcessDefinition;
import br.net.mirante.singular.flow.core.entity.IEntityCategory;
import br.net.mirante.singular.flow.core.entity.IEntityProcessDefinition;
import br.net.mirante.singular.flow.core.entity.IEntityRoleDefinition;
import br.net.mirante.singular.flow.core.entity.IEntityProcessVersion;
import br.net.mirante.singular.flow.core.entity.IEntityTaskDefinition;
import br.net.mirante.singular.flow.core.entity.IEntityTaskTransitionVersion;
import br.net.mirante.singular.flow.core.entity.IEntityTaskVersion;

public interface IProcessDefinitionEntityService<CATEGORY extends IEntityCategory, PROCESS_DEF extends IEntityProcessDefinition,
        PROCESS_VERSION extends IEntityProcessVersion, TASK_DEF extends IEntityTaskDefinition, TASK_VERSION extends IEntityTaskVersion,
 TRANSITION extends IEntityTaskTransitionVersion, PROCESS_ROLE extends IEntityRoleDefinition> {

    /**
     * Generates a new {@link IEntityProcessVersion} if {@link ProcessDefinition} is
     * new or has changed
     */
    PROCESS_VERSION generateEntityFor(ProcessDefinition<?> processDefinition);

    boolean isDifferentVersion(IEntityProcessVersion oldEntity, IEntityProcessVersion newEntity);
}
