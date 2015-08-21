package br.net.mirante.singular.flow.core.service;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import br.net.mirante.singular.flow.core.MTask;
import br.net.mirante.singular.flow.core.MUser;
import br.net.mirante.singular.flow.core.ProcessInstance;
import br.net.mirante.singular.flow.core.entity.IEntityTaskDefinition;

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

    List<I> retrieveAllInstancesIn(String... tasksNames);

    List<I> retrieveAllInstancesIn(MTask<?> task);

    List<I> retrieveAllInstancesIn(Date minCreateDate, Date maxCreateDate, boolean showEnded, String... tasksNames);

    List<I> retrieveAllInstancesIn(Date minCreateDate, Date maxCreateDate, boolean showEnded, IEntityTaskDefinition... entityTasks);
}
