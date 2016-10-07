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
