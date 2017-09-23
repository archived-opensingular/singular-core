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

package org.opensingular.flow.core;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.collections.CollectionUtils;
import org.opensingular.flow.core.property.MetaDataRef;
import org.opensingular.flow.core.variable.VarService;
import org.opensingular.lib.commons.base.SingularException;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>Esta classe representa o mapa de fluxo de uma dada definição de processo.</p>
 *
 * @author Daniel Bordin
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class FlowMap {

    private final FlowDefinition<?> flowDefinition;

    private final Map<String, STask<?>> tasksByName = new HashMap<>();

    private final Map<String, STask<?>> tasksByAbbreviation = new HashMap<>();

    private final Map<String, STaskEnd> endTasks = new HashMap<>();

    private final Map<String, SBusinessRole> rolesByAbbreviation = new HashMap<>();

    private SStart start;

    private IRoleChangeListener roleChangeListener;

    private final Map<String, DashboardView> dashboardViews = new LinkedHashMap<>();

    /**
     * <p>Instancia um novo mapa para a definição de processo especificado.</p>
     *
     * @param flowDefinition a definição de processo especificado.
     */
    public FlowMap(FlowDefinition<?> flowDefinition) {
        this.flowDefinition = flowDefinition;
    }

    /**
     * <p>Ponto de extensão para customizações. Cria uma nova transição com as características
     * informadas.</p>
     *
     * @param origin tarefa de origem.
     * @param name o nome da transição.
     * @param destination a tarefa destino.
     * @param type o tipo de transição.
     * @return a nova transição criada.
     */
    protected STransition newTransition(STask<?> origin, String name, STask<?> destination) {
        return new STransition(origin, name, destination);
    }

    /**
     * <p>Retorna as tarefas definidas neste mapa. Apenas tarefas que não são do tipo fim
     * ({@link TaskType#END}) são retornadas.</p>
     *
     * @return as tarefas definidas.
     */
    @Nonnull
    public Collection<STask<?>> getTasks() {
        return tasksByName.values();
    }

    /**
     * <p>Retorna todas as tarefas definidas neste mapa.</p>
     *
     * @return todas as tarefas definidas.
     */
    @Nonnull
    public Collection<STask<?>> getAllTasks() {
        return CollectionUtils.union(getTasks(), getEndTasks());
    }

    /**
     * <p>Retorna as tarefas definidas neste mapa do tipo {@link TaskType#HUMAN}.</p>
     *
     * @return as tarefas definidas do tipo {@link TaskType#HUMAN} ou uma lista vazia.
     */
    @Nonnull
    public Collection<STaskHuman> getHumanTasks() {
        return (Collection<STaskHuman>) getTasks(TaskType.HUMAN);
    }

    /**
     * Retorna as tarefas definidas neste mapa do tipo {@link TaskType#JAVA}.
     *
     * @return as tarefas definidas do tipo {@link TaskType#JAVA} ou uma lista vazia.
     */
    @Nonnull
    public Collection<STaskJava> getJavaTasks() {
        return (Collection<STaskJava>) getTasks(TaskType.JAVA);
    }

    /**
     * <p>Retorna as tarefas definidas neste mapa do tipo {@link TaskType#WAIT}.</p>
     *
     * @return as tarefas definidas do tipo {@link TaskType#WAIT} ou uma lista vazia.
     */
    @Nonnull
    public Collection<STaskWait> getWaitTasks() {
        return (Collection<STaskWait>) getTasks(TaskType.WAIT);
    }

    /**
     * Retorna as tarefas definidas neste mapa do tipo especificado.
     *
     * @param IEntityTaskType o tipo especificado.
     * @return as tarefas definidas do tipo especificado ou uma lista vazia
     */
    @Nonnull
    public Collection<? extends STask<?>> getTasks(IEntityTaskType IEntityTaskType) {
        final Builder<STask<?>> builder = ImmutableList.builder();
        for (final STask sTask : getTasks()) {
            if (sTask.getTaskType() == IEntityTaskType) {
                builder.add(sTask);
            }
        }
        return builder.build();
    }

    /**
     * <p>Retorna as tarefas definidas neste mapa do tipo fim ({@link TaskType#END}).</p>
     *
     * @return as tarefas definidas do tipo fim.
     */
    @Nonnull
    public Collection<STaskEnd> getEndTasks() {
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
    public SBusinessRole getRoleWithAbbreviation(String abbreviation) {
        return rolesByAbbreviation.get(abbreviation.toLowerCase());
    }

    /**
     * <p>Retorna os papeis definidos. A coleção retornada é do tipo {@link ImmutableSet}.</p>
     *
     * @return todos os papeis definidos.
     */
    public Collection<SBusinessRole> getRoles() {
        return ImmutableSet.copyOf(rolesByAbbreviation.values());
    }

    /**
     * <p>Adiciona um novo papel a este mapa.</p>
     *
     * @param name o nome do papel.
     * @param abbreviation a sigla do papel.
     * @param businessRoleStrategy o {@link BusinessRoleStrategy} do papel.
     * @param automaticUserAllocation indicador de alocação automática.
     * @return o papel adicionado ao mapa.
     */
    public SBusinessRole addRoleDefinition(String name, String abbreviation,
            BusinessRoleStrategy<? extends FlowInstance> businessRoleStrategy,
            boolean automaticUserAllocation) {
        final SBusinessRole processRole = new SBusinessRole(name, abbreviation, businessRoleStrategy, automaticUserAllocation);
        if (hasRoleWithAbbreviation(processRole.getAbbreviation())) {
            throw new SingularFlowException("Role with abbreviation '" + processRole.getAbbreviation() + "' already defined", this);
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
    public <T extends FlowInstance> FlowMap setRoleChangeListener(IRoleChangeListener<T> roleChangeListener) {
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
    public void notifyRoleChange(final FlowInstance instance, final SBusinessRole role, SUser previousUser, SUser newUser) {
        if (roleChangeListener != null) {
            roleChangeListener.execute(instance, role, previousUser, newUser);
        }
    }

    /**
     * <p>Adiciona uma nova tarefa.</p>
     *
     * @param task a tarefa para adicionar.
     * @return a tarefa adicionada.
     */
    protected <T extends STask> T addTask(T task) {

        String name = task.getName();
        String abbreviation = task.getAbbreviation();

        if (tasksByAbbreviation.containsKey(abbreviation)) {
            throw new SingularFlowException("Task with abbreviation '" + abbreviation + "' already defined", this);
        }
        if (tasksByName.containsKey(name)) {
            throw new SingularFlowException("Task with name '" + name + "' already defined", this);
        }

        tasksByName.put(name, task);
        tasksByAbbreviation.put(abbreviation, task);

        return task;
    }

    /**
     * <p>Cria e adiciona uma nova tarefa do tipo {@link TaskType#HUMAN}.</p>
     *
     * @param definition a definição da tarefa.
     * @return a nova tarefa criada e adicionada.
     */
    public STaskHuman addHumanTask(ITaskDefinition definition) {
        return addTask(new STaskHuman(this, definition.getName(), definition.getKey()));
    }

    /**
     * <p>Cria e adiciona uma nova tarefa do tipo {@link TaskType#JAVA}.</p>
     *
     * @param definition a definição da tarefa.
     * @return a nova tarefa criada e adicionada.
     */
    public STaskJava addJavaTask(ITaskDefinition definition) {
        return addTask(new STaskJava(this, definition.getName(), definition.getKey()));
    }

    /**
     * <p>Cria e adiciona uma nova tarefa do tipo {@link TaskType#WAIT}.</p>
     *
     * @param definition a definição da tarefa.
     * @return a nova tarefa criada e adicionada.
     */
    public STaskWait addWaitTask(ITaskDefinition definition) {
        return addWaitTask(definition, null);
    }

    /**
     * <p>Cria e adiciona uma nova tarefa do tipo {@link TaskType#WAIT}.</p>
     *
     * <p>Configura a estratégia de execução conforme a especificada ({@link IExecutionDateStrategy}).
     * Isso define a data alvo de uma instância desta tarefa.</p>
     *
     * @param <T> o tipo da instância de processo.
     * @param definition a definição da tarefa.
     * @param dateExecutionStrategy a estratégia de execução.
     * @return a nova tarefa criada e adicionada.
     */
    public <T extends FlowInstance> STaskWait addWaitTask(ITaskDefinition definition,
            IExecutionDateStrategy<T> dateExecutionStrategy) {
        return addTask(new STaskWait(this, definition.getName(), definition.getKey(), dateExecutionStrategy));
    }

    /**
     * <p>Seleciona a tarefa inicial deste mapa.</p>
     *
     * @param initialTask a definição da tarefa que corresponde à inicial.
     * @return a tarefa inicial.
     */
    public SStart setStart(ITaskDefinition initialTask) {
        return setStart(getTask(initialTask));
    }

    /**
     * <p>Seleciona a tarefa inicial deste mapa.</p>
     *
     * @param task a tarefa inicial.
     * @return a tarefa inicial.
     */
    public SStart setStart(STask<?> task) {
        Objects.requireNonNull(task);
        if (task.getFlowMap() != this) {
            throw new SingularFlowException("The task does not belong to this flow", this);
        } else if (start != null) {
            throw new SingularFlowException("The start point is already setted", this);
        }
        start = new SStart(task);
        return start;
    }

    /**
     * <p>Retorna a tarefa inicial deste mapa.</p>
     *
     * @return a tarefa inicial.
     */
    public SStart getStart() {
        if (start == null) {
            throw new SingularFlowException("Task inicial não definida no processo", this);
        }
        return start;
    }

    /**
     * <p>Retorna a definição de processo deste mapa.</p>
     *
     * @return a definição de processo.
     */
    public FlowDefinition<?> getFlowDefinition() {
        return flowDefinition;
    }

    /**
     * <p>Cria e adiciona uma nova tarefa do tipo fim ({@link TaskType#END}).</p>
     *
     * @param definition a definição da tarefa.
     * @return a nova tarefa criada e adicionada.
     */
    public STaskEnd addEnd(ITaskDefinition definition) {
        Objects.requireNonNull(definition.getKey());
        Objects.requireNonNull(definition.getName());
        if (endTasks.containsKey(definition.getName())) {
            throw new SingularFlowException("End task '" + definition.getName() + "' already defined", this);
        }
        final STaskEnd fim = new STaskEnd(this, definition.getName(), definition.getKey());
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
    public Optional<STask<?>> getTaskByAbbreviation(String abbreviation) {
        return Optional.ofNullable(tasksByAbbreviation.get(abbreviation));
    }

    /**
     * <p>Retorna a tarefa deste mapa com a sigla especificada.</p>
     *
     * @param abbreviation a sigla especificada.
     * @return a tarefa deste mapa com a sigla especificada.
     * @throws SingularFlowException caso não encontre tarefa com a sigla especificada.
     */
    public STask<?> getTaskByAbbreviationOrException(String abbreviation) {
        return getTaskByAbbreviation(abbreviation).orElseThrow(
                () -> new SingularFlowException("Task with abbreviation '" + abbreviation + "' not found", this));
    }

    /**
     * <p>Retorna a tarefa do tipo {@link TaskType#HUMAN} deste mapa com a sigla especificada.</p>
     *
     * @param abbreviation a sigla especificada.
     * @return a tarefa deste mapa com a sigla especificada; ou {@code null} caso não a encontre.
     */
    public Optional<STaskHuman> getHumanTaskByAbbreviation(String abbreviation) {
        return getTaskByAbbreviation(abbreviation).map(task -> castCheck(task, STaskHuman.class, abbreviation));
    }

    /**
     * <p>Retorna a tarefa do tipo {@link TaskType#HUMAN} deste mapa com a sigla especificada.</p>
     *
     * @param abbreviation a sigla especificada.
     * @return a tarefa deste mapa com a sigla especificada.
     * @throws SingularFlowException caso não encontre tarefa com a sigla especificada.
     */
    public STaskHuman getHumanTaskByAbbreviationOrException(String abbreviation) {
        return castCheck(getTaskByAbbreviationOrException(abbreviation), STaskHuman.class, abbreviation);
    }

    private <T extends STask> T castCheck(STask<?> target, Class<T> expectedClass, String abbreviation) {
        if (target == null) {
            return null;
        } else if (expectedClass.isInstance(target)) {
            return expectedClass.cast(target);
        }
        throw new SingularFlowException("Task with abbreviation '" + abbreviation + "' found, but it is of type "
                + target.getClass().getName() + " and was expected to be " + expectedClass.getName(), this);
    }

    /**
     * <p>Encontra a definição da tarefa informada ou dispara uma exceção caso não a encontre.</p>
     *
     * @param taskDefinition a definição informada.
     * @return a definição da tarefa informada.
     * @throws SingularException caso não encontre a tarefa.
     */
    public STask<?> getTask(ITaskDefinition taskDefinition) {
        STask<?> task = getTaskWithName(taskDefinition.getName());
        if (task == null) {
            throw new SingularFlowException(
                    "Task " + taskDefinition.getKey() + " não encontrada em " + getFlowDefinition().getKey(), this);
        }
        return task;
    }

    /**
     * <p>Retorna a tarefa deste mapa com o none especificado.</p>
     *
     * @param name o nome especificado.
     * @return a tarefa deste mapa com o nome especificado; ou {@code null} caso não a encontre.
     */
    public STask<?> getTaskWithName(String name) {
        if (tasksByName.containsKey(name)) {
            return tasksByName.get(name);
        }
        return endTasks.get(name);
    }

    public List<STask<?>> getTasksWithMetadata(MetaDataRef ref) {
        return (List<STask<?>>) getAllTasks().stream()
                .filter(t -> t.getMetaData().get(ref) != null)
                .collect(Collectors.toList());
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
        if(start == null){
            throw new SingularFlowException("There is no initial task set", this);
        }
        checkRouteToTheEnd();
    }

    private void verifyTasksConsistency() {
        tasksByAbbreviation.values().stream().forEach(STask::verifyConsistency);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    private void checkRouteToTheEnd() {
        final Set<STask<?>> tasks = new HashSet<>(tasksByName.values());
        while (removeIfReachesTheEnd(tasks)) {
            /* CORPO VAZIO */
        }
        if (!tasks.isEmpty()) {
            throw new SingularFlowException("The following tasks have no way to reach the end: "
                    + joinTaskNames(tasks), this);
        }
    }

    private static boolean removeIfReachesTheEnd(Set<STask<?>> tasks) {
        return tasks.removeIf((task) -> task.getTransitions().stream()
                .anyMatch((transition) -> transition.getDestination().isEnd()
                        || !tasks.contains(transition.getDestination())));
    }

    private static String joinTaskNames(Set<STask<?>> tasks) {
        return tasks.stream().map(STask::getName).collect(Collectors.joining(", "));
    }

    /**
     * <p>Retorna o serviço de consulta das definições de variáveis.</p>
     *
     * @return o serviço de consulta.
     */
    protected VarService getVarService() {
        return flowDefinition.getVarService();
    }

    @Override
    public String toString() {
        return "FlowMap [processDefinition=" + flowDefinition.getName() + "]";
    }

    public void addDashboardView(DashboardView dashboardView) {
        dashboardViews.put(dashboardView.getName(), dashboardView);
    }

    public List<DashboardView> getDashboardViews() {
        return new ArrayList<>(dashboardViews.values());
    }

    public DashboardView getDashboardViewWithName(String name) {
        return dashboardViews.get(name);
    }

    @Nonnull
    public <T extends Serializable> FlowMap setMetaDataValue(@Nonnull MetaDataRef<T> propRef, T value) {
        getFlowDefinition().setMetaDataValue(propRef, value);
        return this;
    }
}
