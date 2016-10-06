/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.core.service;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.opensingular.flow.core.MUser;
import org.opensingular.flow.core.builder.ITaskDefinition;
import org.opensingular.flow.core.MTask;
import org.opensingular.flow.core.ProcessInstance;
import org.opensingular.flow.core.entity.IEntityTaskDefinition;

/**
 * Service to provide an interface to retrieve data about the process runtime
 */
public interface IProcessDataService<I extends ProcessInstance> {

    I retrieveInstance(Integer entityCod);

    List<I> retrieveActiveInstancesCreatedBy(MUser user);

    List<I> retrieveActiveInstances();

    List<I> retrieveActiveInstancesWithPeople();

    List<I> retrieveActiveInstancesWithPeopleOrWaiting();

    List<I> retrieveAllInstances(boolean showEnded);

    List<I> retrieveEndedInstances();

    List<I> retrieveEndedInstancesCreatedBy(MUser user);

    List<I> retrieveAllInstancesIn(Collection<? extends IEntityTaskDefinition> entityTasks);

    List<I> retrieveAllInstancesIn(ITaskDefinition... tasks);

    List<I> retrieveAllInstancesIn(MTask<?> task);

    List<I> retrieveAllInstancesIn(Date beginDate, Date endDate, boolean showEnded, ITaskDefinition... tasksNames);

    List<I> retrieveAllInstancesIn(Date beginDate, Date endDate, boolean showEnded, IEntityTaskDefinition... entityTasks);
}
