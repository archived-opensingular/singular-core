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

/**
 * <p>Esta classe representa o mapa de fluxo de uma dada definição de processo.</p>
 *
 * @author Mirante Tecnologia
 */
@SuppressWarnings({"serial", "rawtypes", "unchecked"})
public class FlowMap implements Serializable {

    private final ProcessDefinition<?> processDefinition;

    private final Map<String, MTask<?>> tasksByName = new HashMap<>();

    private final Map<String, MTask<?>> tasksByAbbreviation = new HashMap<>();

    private final Map<String, MTaskEnd> endTasks = new HashMap<>();

    private final Map<String, MProcessRole> rolesByAbbreviation = new HashMap<>();

    private MTask<?> startTask;

    private IRoleChangeListener roleChangeListener;

    /**
     * <p>Instancia um novo mapa para a definição de processo especificado.</p>
     *
     * @param processDefinition a definição de processo especificado.
     */
    public FlowMap(ProcessDefinition<?> processDefinition) {
        this.processDefinition = processDefinition;
    }

    /**
     * <p>Ponto de extensão para customizações. Cria uma nova transição com as características
     * informadas.</p>
     *
     * @param origin tarefa de origem.
     * @param name o nome da transição.
     * @param destinarion a tarefa destino.
     * @param type o tipo de transição.
     * @return a nova transição criada.
     */
    protected MTransition newTransition(MTask<?> origin, String name, MTask<?> destinarion, TransitionType type) {
        return new MTransition(origin, name, destinarion, type);
    }

    /**
     * <p>Retorna as tarefas definidas neste mapa. Apenas tarefas que não são do tipo fim
     * ({@link TaskType#End}) são retornadas.</p>
     *
     * @return as tarefas definidas.
     */
    public Collection<MTask<?>> getTasks() {
        return tasksByName.values();
    }

    /**
     * <p>Retorna todas as tarefas definidas neste mapa.</p>
     *
     * @return todas as tarefas definidas.
     */
    public Collection<MTask<?>> getAllTasks() {
        return CollectionUtils.union(getTasks(), getEndTasks());
    }

    /**
     * <p>Retorna as tarefas definidas neste mapa do tipo {@link TaskType#People}.</p>
     *
     * @return as tarefas definidas do tipo {@link TaskType#People}.
     */
    public Collection<MTaskPeople> getPeopleTasks() {
        return (Collection<MTaskPeople>) getTasks(TaskType.People);
    }

    /**
     * <p>Retorna as tarefas definidas neste mapa do tipo {@link TaskType#Java}.</p>
     *
     * @return as tarefas definidas do tipo {@link TaskType#Java}.
     */
    public Collection<MTaskJava> getJavaTasks() {
        return (Collection<MTaskJava>) getTasks(TaskType.Java);
    }

    /**
     * <p>Retorna as tarefas definidas neste mapa do tipo {@link TaskType#Wait}.</p>
     *
     * @return as tarefas definidas do tipo {@link TaskType#Wait}.
     */
    public Collection<MTaskWait> getWaitTasks() {
        return (Collection<MTaskWait>) getTasks(TaskType.Wait);
    }

    /**
     * <p>Retorna as tarefas definidas neste mapa do tipo especificado.</p>
     *
     * @param IEntityTaskType o tipo especificado.
     * @return as tarefas definidas do tipo especificado.
     */
    public Collection<? extends MTask<?>> getTasks(IEntityTaskType IEntityTaskType) {
        final Builder<MTask<?>> builder = ImmutableList.builder();
        for (final MTask mTask : getTasks()) {
            if (mTask.getTaskType() == IEntityTaskType) {
                builder.add(mTask);
            }
        }
        return builder.build();
    }

    /**
     * <p>Retorna as tarefas definidas neste mapa do tipo fim ({@link TaskType#End}).</p>
     *
     * @return as tarefas definidas do tipo fim.
     */
    public Collection<MTaskEnd> getEndTasks() {
        return endTasks.values();
    }

    /**
     * <p>Verifica se há um papel definido com a sigla especificada.</p>
     *
     * @param sigla a sigla especificada.
     * @return {@code true} caso exista; {@code false} caso contrário.
     */
    public boolean hasRoleWithAbbreviation(String sigla) {
        return rolesByAbbreviation.containsKey(sigla.toLowerCase());
    }

    /**
     * <p>Retorna o papel definido com a sigla especificada.</p>
     *
     * @param abbreviation a sigla especificada.
     * @return o papel definido; {@code null} caso não haja papel com a sigla especificada.
     */
    public MProcessRole getRoleWithAbbreviation(String abbreviation) {
        return rolesByAbbreviation.get(abbreviation.toLowerCase());
    }

    /**
     * <p>Retorna os papeis definidos. A coleção retornada é do tipo {@link ImmutableSet}.</p>
     *
     * @return todos os papeis definidos.
     */
    public Collection<MProcessRole> getRoles() {
        return ImmutableSet.copyOf(rolesByAbbreviation.values());
    }

    /**
     * <p>Adiciona um novo papel a este mapa.</p>
     *
     * @param name o nome do papel.
     * @param abbreviation a sigla do papel.
     * @param userRoleSettingStrategy o {@link UserRoleSettingStrategy} do papel.
     * @param automaticUserAllocation indicador de alocação automática.
     * @return o papel adicionado ao mapa.
     */
    public MProcessRole addRoleDefinition(String name, String abbreviation,
            UserRoleSettingStrategy<? extends ProcessInstance> userRoleSettingStrategy,
            boolean automaticUserAllocation) {
        final MProcessRole processRole = new MProcessRole(name, abbreviation, userRoleSettingStrategy, automaticUserAllocation);
        if (hasRoleWithAbbreviation(processRole.getAbbreviation())) {
            throw new SingularFlowException(createErrorMsg("Role with abbreviation '" + processRole.getAbbreviation() + "' already defined"));
        }
        rolesByAbbreviation.put(processRole.getAbbreviation().toLowerCase(), processRole);
        return processRole;
    }

    /**
     * <p>Registra um <i>listener</i> para mudaças de papel.</p>
     *
     * @param <T> o tipo deste mapa de fluxo.
     * @param roleChangeListener o <i>listener</i> do tipo {@link IRoleChangeListener}.
     * @return este mapa com o <i>listener</i> registrado.
     */
    public <T extends ProcessInstance> FlowMap setRoleChangeListener(IRoleChangeListener<T> roleChangeListener) {
        this.roleChangeListener = roleChangeListener;
        return this;
    }

    /**
     * <p>Notifica mudança de papel. Internamente notifica o <i>listener</i> registrado, caso exista.</p>
     *
     * @param instance a instância de processo.
     * @param role o papel.
     * @param previousUser o usuário anteriormente atribuído ao papel.
     * @param newUser o novo usuário atribuído ao papel.
     */
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

    /**
     * <p>Cria e adiciona uma nova tarefa do tipo {@link TaskType#People}.</p>
     *
     * @param definition a definição da tarefa.
     * @return a nova tarefa criada e adicionada.
     */
    public MTaskPeople addPeopleTask(ITaskDefinition definition) {
        return addTask(new MTaskPeople(this, definition.getName(), definition.getKey()));
    }

    /**
     * <p>Cria e adiciona uma nova tarefa do tipo {@link TaskType#Java}.</p>
     *
     * @param definition a definição da tarefa.
     * @return a nova tarefa criada e adicionada.
     */
    public MTaskJava addJavaTask(ITaskDefinition definition) {
        return addTask(new MTaskJava(this, definition.getName(), definition.getKey()));
    }

    /**
     * <p>Cria e adiciona uma nova tarefa do tipo {@link TaskType#Wait}.</p>
     *
     * @param definition a definição da tarefa.
     * @return a nova tarefa criada e adicionada.
     */
    public MTaskWait addWaitTask(ITaskDefinition definition) {
        return addWaitTask(definition, null);
    }

    /**
     * <p>Cria e adiciona uma nova tarefa do tipo {@link TaskType#Wait}.</p>
     *
     * <p>Configura a estratégia de execução conforme a especificada ({@link IExecutionDateStrategy}).
     * Isso define a data alvo de uma instância desta tarefa.</p>
     *
     * @param <T> o tipo da instância de processo.
     * @param definition a definição da tarefa.
     * @param dateExecutionStrategy a estratégia de execução.
     * @return a nova tarefa criada e adicionada.
     */
    public <T extends ProcessInstance> MTaskWait addWaitTask(ITaskDefinition definition,
            IExecutionDateStrategy<T> dateExecutionStrategy) {
        return addTask(new MTaskWait(this, definition.getName(), definition.getKey(), dateExecutionStrategy));
    }

    /**
     * <p>Seleciona a tarefa inicial deste mapa.</p>
     *
     * @param initialTask a definição da tarefa que corresponde à inicial.
     * @return a tarefa inicial.
     */
    public MTask<?> setStartTask(ITaskDefinition initialTask) {
        return setStartTask(getTask(initialTask));
    }

    /**
     * <p>Seleciona a tarefa inicial deste mapa.</p>
     *
     * @param task a tarefa inicial.
     * @return a tarefa inicial.
     */
    public MTask<?> setStartTask(MTask<?> task) {
        Objects.requireNonNull(task);
        if (task.getFlowMap() != this) {
            throw new SingularFlowException(createErrorMsg("The task does not belong to this flow"));
        }
        startTask = task;
        return task;
    }

    /**
     * <p>Verifica se há pelo menos duas tarefas do tipo {@link TaskType#People} neste mapa.</p>
     *
     * @return {@code true} caso haja pelo menos duas tarefas do tipo {@link TaskType#People};
     * {@code false} caso contrário.
     */
    public boolean hasMultiplePeopleTasks() {
        return (getPeopleTasks().size() > 1);
    }

    /**
     * <p>Retorna a tarefa inicial deste mapa.</p>
     *
     * @return a tarefa inicial.
     */
    public MTask<?> getStartTask() {
        Objects.requireNonNull(startTask);
        return startTask;
    }

    /**
     * <p>Retorna a definição de processo deste mapa.</p>
     *
     * @return a definição de processo.
     */
    public ProcessDefinition<?> getProcessDefinition() {
        return processDefinition;
    }

    /**
     * <p>Cria e adiciona uma nova tarefa do tipo fim ({@link TaskType#End}).</p>
     *
     * @param definition a definição da tarefa.
     * @return a nova tarefa criada e adicionada.
     */
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

    /**
     * <p>Retorna a tarefa deste mapa com a sigla especificada.</p>
     *
     * @param abbreviation a sigla especificada.
     * @return a tarefa deste mapa com a sigla especificada; ou {@code null} caso não a encontre.
     */
    public MTask<?> getTaskBybbreviation(String abbreviation) {
        return tasksByAbbreviation.get(abbreviation);
    }

    /**
     * <p>Retorna a tarefa deste mapa com a sigla especificada.</p>
     *
     * @param abbreviation a sigla especificada.
     * @return a tarefa deste mapa com a sigla especificada.
     * @throws SingularFlowException caso não encontre tarefa com a sigla especificada.
     */
    public MTask<?> getTaskByAbbreviationOrException(String abbreviation) {
        MTask<?> t = tasksByAbbreviation.get(abbreviation);
        if (t == null) {
            throw new SingularFlowException(createErrorMsg("Task with abbreviation '" + abbreviation + "' not found"));
        }
        return t;
    }

    /**
     * <p>Retorna a tarefa do tipo {@link TaskType#People} deste mapa com a sigla especificada.</p>
     *
     * @param abbreviation a sigla especificada.
     * @return a tarefa deste mapa com a sigla especificada; ou {@code null} caso não a encontre.
     */
    public MTaskPeople getPeopleTaskByAbbreviation(String abbreviation) {
        return castCheck(getTaskBybbreviation(abbreviation), MTaskPeople.class, abbreviation);
    }

    /**
     * <p>Retorna a tarefa do tipo {@link TaskType#People} deste mapa com a sigla especificada.</p>
     *
     * @param abbreviation a sigla especificada.
     * @return a tarefa deste mapa com a sigla especificada.
     * @throws SingularFlowException caso não encontre tarefa com a sigla especificada.
     */
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
     * <p>Encontra a definição da tarefa informada ou dispara uma exceção caso não a encontre.</p>
     *
     * @param taskDefinition a definição informada.
     * @return a definição da tarefa informada.
     * @throws SingularException caso não encontre a tarefa.
     */
    public MTask<?> getTask(ITaskDefinition taskDefinition) {
        MTask<?> task = getTaskWithName(taskDefinition.getName());
        if (task == null) {
            throw new SingularException(
                    "Task " + taskDefinition.getKey() + " não encontrada em " + getProcessDefinition().getAbbreviation());
        }
        return task;
    }

    /**
     * <p>Retorna a tarefa deste mapa com o none especificado.</p>
     *
     * @param name o nome especificado.
     * @return a tarefa deste mapa com o nome especificado; ou {@code null} caso não a encontre.
     */
    public MTask<?> getTaskWithName(String name) {
        if (tasksByName.containsKey(name)) {
            return tasksByName.get(name);
        }
        return endTasks.get(name);
    }

    /**
     * <p>Verifica a consistência deste mapa.</p>
     *
     * <p>Um mapa é considerado consistente caso passe nos seguintes testes:</p>
     * <ul>
     *     <li>Cada tarefa definida neste mapa é consistente</li>
     *     <li>A tarefa inicial foi selecionada</li>
     *     <li>Todas as transições levam a uma tarefa válida</li>
     * </ul>
     */
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

    @SuppressWarnings("StatementWithEmptyBody")
    private void checkRouteToTheEnd() {
        final Set<MTask<?>> tasks = new HashSet<>(tasksByName.values());
        while (removeIfReachesTheEnd(tasks)) {
            /* CORPO VAZIO */
        }
        if (!tasks.isEmpty()) {
            throw new SingularFlowException(createErrorMsg("The following tasks have no way to reach the end: "
                    + joinTaskNames(tasks)));
        }
    }

    private static boolean removeIfReachesTheEnd(Set<MTask<?>> tasks) {
        return tasks.removeIf((task) -> task.getTransitions().stream()
                .anyMatch((transition) -> transition.getDestination().isEnd()
                        || !tasks.contains(transition.getDestination())));
    }

    private static String joinTaskNames(Set<MTask<?>> tasks) {
        return tasks.stream().map(MTask::getName).collect(Collectors.joining(", "));
    }

    final String createErrorMsg(String msg) {
        return getProcessDefinition() + " -> " + msg;
    }

    /**
     * <p>Retorna o serviço de consulta das definições de variáveis.</p>
     *
     * @return o serviço de consulta.
     */
    protected VarService getVarService() {
        return processDefinition.getVarService();
    }

    @Override
    public String toString() {
        return "FlowMap [processDefinition=" + processDefinition.getName() + "]";
    }
}
