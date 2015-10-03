package br.net.mirante.singular.flow.core;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableSet;

import br.net.mirante.singular.commons.base.SingularException;
import br.net.mirante.singular.flow.core.builder.ITaskDefinition;
import br.net.mirante.singular.flow.core.entity.TransitionType;
import br.net.mirante.singular.flow.util.vars.VarService;

@SuppressWarnings({"serial", "rawtypes", "unchecked"})
public class FlowMap implements Serializable {

    private final ProcessDefinition<?> processDefinition;

    private final Map<String, MTask<?>> tasksByName = new HashMap<>();

    private final Map<String, MTask<?>> tasksByAbbreviation = new HashMap<>();

    private final Map<String, MTaskEnd> endTasks = new HashMap<>();

    private final Map<String, MProcessRole> rolesByAbbreviation = new HashMap<>();

    private MTask<?> startTask;

    private IRoleChangeListener roleChangeListener;

    public FlowMap(ProcessDefinition<?> processDefinition) {
        this.processDefinition = processDefinition;
    }

    /**
     * Ponto de extensão para customizações.
     */
    protected MTransition newTransition(MTask<?> origin, String name, MTask<?> destinarion, TransitionType type) {
        return new MTransition(origin, name, destinarion, type);
    }

    public Collection<MTask<?>> getTasks() {
        return tasksByName.values();
    }

    public Collection<MTask<?>> getAllTasks() {
        return CollectionUtils.union(getTasks(), getEndTasks());
    }

    public Collection<MTaskPeople> getPeopleTasks() {
        return (Collection<MTaskPeople>) getTasks(TaskType.People);
    }

    public Collection<MTaskJava> getJavaTasks() {
        return (Collection<MTaskJava>) getTasks(TaskType.Java);
    }

    public Collection<MTaskWait> getWaitTasks() {
        return (Collection<MTaskWait>) getTasks(TaskType.Wait);
    }

    public Collection<? extends MTask<?>> getTasks(IEntityTaskType IEntityTaskType) {
        final Builder<MTask<?>> builder = ImmutableList.builder();
        for (final MTask mTask : getTasks()) {
            if (mTask.getTaskType() == IEntityTaskType) {
                builder.add(mTask);
            }
        }
        return builder.build();
    }

    public Collection<MTaskEnd> getEndTasks() {
        return endTasks.values();
    }

    public boolean hasRoleWithAbbreviation(String sigla) {
        return rolesByAbbreviation.containsKey(sigla.toLowerCase());
    }

    public MProcessRole getRoleWithAbbreviation(String abbreviation) {
        return rolesByAbbreviation.get(abbreviation.toLowerCase());
    }

    public Collection<MProcessRole> getRoles() {
        return ImmutableSet.copyOf(rolesByAbbreviation.values());
    }

    public MProcessRole addRoleDefinition(String name, String abbreviation, UserRoleSettingStrategy<? extends ProcessInstance> userRoleSettingStrategy,
            boolean automaticUserAllocation) {
        final MProcessRole processRole = new MProcessRole(name, abbreviation, userRoleSettingStrategy, automaticUserAllocation);
        if (hasRoleWithAbbreviation(processRole.getAbbreviation())) {
            throw new SingularFlowException(createErrorMsg("Role with abbreviation '" + processRole.getAbbreviation() + "' already defined"));
        }
        rolesByAbbreviation.put(processRole.getAbbreviation().toLowerCase(), processRole);
        return processRole;
    }

    public <T extends ProcessInstance> FlowMap setRoleChangeListener(IRoleChangeListener<T> roleChangeListener) {
        this.roleChangeListener = roleChangeListener;
        return this;
    }

    public void notifyRoleChange(final ProcessInstance instance, final MProcessRole role, MUser previousUser, MUser newUser) {
        if (roleChangeListener != null) {
            roleChangeListener.execute(instance, role, previousUser, newUser);
        }
    }

    private <T extends MTask> T addTask(T task) {
        if (tasksByName.containsKey(task.getName())) {
            throw new SingularFlowException(createErrorMsg("Task with name '" + task.getName() + "' already defined"));
        }
        tasksByName.put(task.getName(), task);
        if (tasksByAbbreviation.containsKey(task.getAbbreviation())) {
            throw new SingularFlowException(createErrorMsg("Task with abbreviation '" + task.getAbbreviation() + "' already defined"));
        }
        tasksByAbbreviation.put(task.getAbbreviation(), task);
        return task;
    }

    public MTaskPeople addPeopleTask(ITaskDefinition definition) {
        return addTask(new MTaskPeople(this, definition.getName(), definition.getKey()));
    }

    public MTaskJava addJavaTask(ITaskDefinition definition) {
        return addTask(new MTaskJava(this, definition.getName(), definition.getKey()));
    }

    public MTaskWait addWaitTask(ITaskDefinition definition) {
        return addWaitTask(definition, (IExecutionDateStrategy<ProcessInstance>) null);
    }

    public <T extends ProcessInstance> MTaskWait addWaitTask(ITaskDefinition definition, IExecutionDateStrategy<T> dateExecutionStrategy) {
        return addTask(new MTaskWait(this, definition.getName(), definition.getKey(), dateExecutionStrategy));
    }

    public MTask<?> setStartTask(ITaskDefinition initialTask) {
        return setStartTask(getTask(initialTask));
    }

    public MTask<?> setStartTask(MTask<?> task) {
        Objects.requireNonNull(task);
        if (task.getFlowMap() != this) {
            throw new SingularFlowException(createErrorMsg("The task does not belong to this flow"));
        }
        startTask = task;
        return task;
    }

    public boolean hasMultiplePeopleTasks() {
        return (getPeopleTasks().size() > 1);
    }

    public MTask<?> getStartTask() {
        Objects.requireNonNull(startTask);
        return startTask;
    }

    public ProcessDefinition<?> getProcessDefinition() {
        return processDefinition;
    }

    public MTaskEnd addEnd(ITaskDefinition definition) {
        Objects.requireNonNull(definition.getKey());
        Objects.requireNonNull(definition.getName());
        if (endTasks.containsKey(definition.getName())) {
            throw new SingularFlowException(createErrorMsg("End task '" + definition.getName() + "' already defined"));
        }
        final MTaskEnd fim = new MTaskEnd(this, definition.getName(), definition.getKey());
        endTasks.put(definition.getName(), fim);
        tasksByAbbreviation.put(fim.getAbbreviation(), fim);
        return fim;
    }

    public MTask<?> getTaskBybbreviation(String abbreviation) {
        return tasksByAbbreviation.get(abbreviation);
    }

    public MTask<?> getTaskByAbbreviationOrException(String abbreviation) {
        MTask<?> t = tasksByAbbreviation.get(abbreviation);
        if (t == null) {
            throw new SingularFlowException(createErrorMsg("Task with abbreviation '" + abbreviation + "' not found"));
        }
        return t;
    }

    public MTaskPeople getPeopleTaskByAbbreviation(String abbreviation) {
        return castCheck(getTaskBybbreviation(abbreviation), MTaskPeople.class, abbreviation);
    }

    public MTaskPeople getPeopleTaskByAbbreviationOrException(String abbreviation) {
        return castCheck(getTaskByAbbreviationOrException(abbreviation), MTaskPeople.class, abbreviation);
    }

    private <T extends MTask> T castCheck(MTask<?> target, Class<T> expectedClass, String abbreviation) {
        if (target == null) {
            return null;
        } else if (expectedClass.isInstance(target)) {
            return expectedClass.cast(target);
        }
        throw new SingularFlowException(createErrorMsg("Task with abbreviation '" + abbreviation + "' found, but it is of type "
                + target.getClass().getName() + " and was expected to be " + expectedClass.getClass().getName()));
    }

    /**
     * Encontra a definição da task informada ou dispara exception senão
     * encontrada.
     *
     * @return Sempre diferente de null
     */
    public MTask<?> getTask(ITaskDefinition taskDefinition) {
        MTask<?> task = getTaskWithName(taskDefinition.getName());
        if (task == null) {
            throw new SingularException(
                    "Task " + taskDefinition.getKey() + " não encontrada em " + getProcessDefinition().getAbbreviation());
        }
        return task;
    }

    public MTask<?> getTaskWithName(String name) {
        if (tasksByName.containsKey(name)) {
            return tasksByName.get(name);
        }
        return endTasks.get(name);
    }

    public void verifyConsistency() {
        verifyTasksConsistency();
        if(startTask == null){
            throw new SingularFlowException(createErrorMsg("There is no initial task setted"));
        }
        checkRouteToTheEnd();
    }

    private void verifyTasksConsistency() {
        tasksByAbbreviation.values().stream().forEach(MTask::verifyConsistency);
    }

    private void checkRouteToTheEnd() {
        final Set<MTask<?>> tasks = new HashSet<>(tasksByName.values());
        while (removeIfReachesTheEnd(tasks)) {
        }
        if (!tasks.isEmpty()) {
            throw new SingularFlowException(createErrorMsg("The following tasks have no way to reach the end: " + joinTaskNames(tasks)));
        }
    }

    private static boolean removeIfReachesTheEnd(Set<MTask<?>> tasks) {
        boolean removeuPeloMenosUm = tasks.removeIf((task) -> task.getTransitions().stream()
                .anyMatch((transition) -> transition.getDestination().isEnd() || !tasks.contains(transition.getDestination())));
        return removeuPeloMenosUm;
    }

    private static String joinTaskNames(Set<MTask<?>> tasks) {
        return tasks.stream().map(MTask::getName).collect(Collectors.joining(", "));
    }

    final String createErrorMsg(String msg) {
        return getProcessDefinition() + " -> " + msg;
    }

    protected VarService getVarService() {
        return processDefinition.getVarService();
    }

    @Override
    public String toString() {
        return "FlowMap [processDefinition=" + processDefinition.getName() + "]";
    }
}
