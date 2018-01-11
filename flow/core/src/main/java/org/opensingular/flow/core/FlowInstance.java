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

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.opensingular.flow.core.entity.IEntityCategory;
import org.opensingular.flow.core.entity.IEntityFlowDefinition;
import org.opensingular.flow.core.entity.IEntityFlowInstance;
import org.opensingular.flow.core.entity.IEntityFlowVersion;
import org.opensingular.flow.core.entity.IEntityRoleDefinition;
import org.opensingular.flow.core.entity.IEntityRoleInstance;
import org.opensingular.flow.core.entity.IEntityTaskDefinition;
import org.opensingular.flow.core.entity.IEntityTaskInstance;
import org.opensingular.flow.core.entity.IEntityTaskVersion;
import org.opensingular.flow.core.entity.IEntityVariableInstance;
import org.opensingular.flow.core.service.IPersistenceService;
import org.opensingular.flow.core.variable.VarInstanceMap;
import org.opensingular.lib.commons.base.SingularException;
import org.opensingular.lib.commons.net.Lnk;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Esta é a classe responsável por manter os dados de instância de um
 * determinado fluxo.
 *
 * @author Daniel Bordin
 */
@SuppressWarnings({ "serial", "unchecked" })
public class FlowInstance implements Serializable {

    private RefFlowDefinition flowDefinitionRef;

    private Integer codEntity;

    private transient IEntityFlowInstance entity;

    private transient STask<?> currentState;

    private transient ExecutionContext executionContext;

    private transient VarInstanceMap<?,?> variables;

    final void setFlowDefinition(FlowDefinition<?> flowDefinition) {
        if (flowDefinitionRef != null) {
            throw SingularException.rethrow("Erro Interno");
        }
        flowDefinitionRef = RefFlowDefinition.of(flowDefinition);
        flowDefinitionRef.get().inject(this);
    }

    /**
     * Retorna a definição de fluxo desta instância.
     *
     * @param <K> o tipo da definição de fluxo.
     */
    @Nonnull
    public <K extends FlowDefinition<?>> K getFlowDefinition() {
        if (flowDefinitionRef == null) {
            throw SingularException.rethrow(
                    "A instância não foi inicializada corretamente, pois não tem uma referência a FlowDefinition! Tente chamar o método newPreStartInstance() a partir da definição do fluxo.");
        }
        return (K) flowDefinitionRef.get();
    }

    /**
     * Inicia esta instância de fluxo.
     *
     * @return A tarefa atual da instância depois da inicialização.
     */
    @Deprecated
    public TaskInstance start() {
        getPersistedDescription(); // Força a geração da descrição
        return FlowEngine.start(this, getVariables());
    }

    /**
     * Realiza a montagem necessária para execução da transição default da tarefa atual desta instância.
     * Senão for encontrada uma tarefa atual ou a tarefa não possui um transação default, dispara exception.
     */
    @Nonnull
    public TransitionCall prepareTransition() {
        return getCurrentTaskOrException().prepareTransition();
    }

    /**
     * Realiza a montagem necessária para execução da transição especificada a partir da tarefa atual desta instância.
     * Senão for encontrada uma tarefa atual ou transição correspodente ao nome informado, dispara exception.
     *
     * @param transitionName a transição especificada.
     * @see TaskInstance#prepareTransition(String)
     */
    @Nonnull
    public TransitionCall prepareTransition(String transitionName) {
        return getCurrentTaskOrException().prepareTransition(transitionName);
    }

    @Nonnull
    final IEntityFlowInstance getInternalEntity() {
        if (entity == null) {
            if(codEntity != null) {
                IEntityFlowInstance newFromDB = getPersistenceService().retrieveFlowInstanceByCodOrException(codEntity);
                IEntityFlowDefinition entityFlowDefinition = getFlowDefinition().getEntityFlowDefinition();
                if (!entityFlowDefinition.equals(newFromDB.getFlowVersion().getFlowDefinition())) {
                    throw SingularException.rethrow(getFlowDefinition().getName() + " id=" + codEntity +
                            " se refere a definição de fluxo " +
                            newFromDB.getFlowVersion().getFlowDefinition().getKey() +
                            " mas era esperado que referenciasse " +
                            entityFlowDefinition);
                }
                entity = newFromDB;
            }
            if (entity == null) {
                throw SingularException.rethrow(
                    getClass().getName() + " is not binded to a new and neither to a existing database flow instance entity.");
            }
        }
        return entity;
    }

    final void setInternalEntity(IEntityFlowInstance entity) {
        Objects.requireNonNull(entity);
        this.entity = entity;
        this.codEntity = entity.getCod();
    }

    /** Configura a instância "pai" desta instância de fluxo. */
    public void setParent(FlowInstance parent) {
        getPersistenceService().setFlowInstanceParent(getInternalEntity(), parent.getInternalEntity());
    }

    /** Retorna a tarefa "pai" desta instância de fluxo. */
    public TaskInstance getParentTask() {
        IEntityTaskInstance dbTaskInstance = getInternalEntity().getParentTask();
        return dbTaskInstance == null ? null : Flow.getTaskInstance(dbTaskInstance);
    }

    /**
     * Retorna o tipo da tarefa corrente desta instância de fluxo. Pode não encotrar se a tarefa em banco não
     * tiver correspondência com o fluxo do fluxo em memória (tarefa legada).
     */
    public Optional<STask<?>> getState() {
        if (currentState == null) {
            Optional<TaskInstance> current = getCurrentTask();
            if (current.isPresent()) {
                currentState = getFlowDefinition().getFlowMap().getTaskByAbbreviation(current.get().getAbbreviation()).orElse(null);
            } else if (isFinished()) {
                current = getNewestTask();
                if (current.isPresent()&& current.get().isFinished()) {
                    currentState = getFlowDefinition().getFlowMap().getTaskByAbbreviation(current.get().getAbbreviation()).orElse(null);
                } else {
                    throw new SingularFlowException(createErrorMsg(
                            "incossitencia: o estado final está null, mas deveria ter um estado do tipo final por " +
                                    "estar finalizado"), this);
                }
            } else {
                throw new SingularFlowException(createErrorMsg("getState() não pode ser invocado para essa instância"), this);
            }
        }
        return Optional.ofNullable(currentState);
    }

    /**
     * Verifica se esta instância está encerrada.
     *
     * @return {@code true} caso esta instância está encerrada; {@code false}
     * caso contrário.
     */
    public boolean isFinished() {
        return getEndDate() != null;
    }

    /**
     * Retornar o nome da definição de fluxo desta instância.
     */
    public String getFlowName() {
        return getFlowDefinition().getName();
    }

    /**
     * Retorna o nome da tarefa atual desta instância de fluxo.
     */
    @Nonnull
    public Optional<String> getCurrentTaskName() {
        Optional<String> name = getState().map(STask::getName);
        if (! name.isPresent()) {
            name = getCurrentTask().map(TaskInstance::getName);
        }
        return name;
    }

    /**
     * <p>
     * Retorna o <i>link resolver</i> padrão desta instância de fluxo.
     * </p>
     *
     * @return o <i>link resolver</i> padrão.
     */
    public final Lnk getDefaultHref() {
        return Flow.getDefaultHrefFor(this);
    }

    /**
     * Retorna os códigos de usuários com direito de execução da tarefa humana
     * definida para o fluxo correspondente a esta instância.
     *
     * @param taskName o nome da tarefa humana a ser inspecionada.
     * @return os códigos de usuários com direitos de execução.
     */
    public Set<Integer> getFirstLevelUsersCodWithAccess(String taskName) {
        return getFlowDefinition().getFlowMap().getHumanTaskByAbbreviationOrException(taskName).getAccessStrategy()
            .getFirstLevelUsersCodWithAccess(this);
    }

    /**
     * Verifica de o usuário especificado pode executar a tarefa corrente desta
     * instância de fluxo.
     *
     * @return {@code true} caso o usuário possa executar a tarefa corrente;
     * {@code false} caso contrário.
     */
    public final boolean canExecuteTask(SUser user) {
        Optional<STask<?>> currentState = getState();
        if (! currentState.isPresent()) {
            return false;
        }
        IEntityTaskType tt = currentState.get().getTaskType();
        if (tt.isHuman() || tt.isWait()) {
            if (isAllocated(user.getCod())) {
                return  true;
            }
            Optional<TaskAccessStrategy<FlowInstance>> strategy = getAccessStrategy();
            return strategy.isPresent() && strategy.get().canExecute(this, user);
        }
        return false;
    }

    /**
     * Verifica de o usuário especificado pode visualizar a tarefa corrente
     * desta instância de fluxo.
     *
     * @param user o usuário especificado.
     * @return {@code true} caso o usuário possa visualizar a tarefa corrente;
     * {@code false} caso contrário.
     */
    public boolean canVisualize(@Nonnull SUser user) {
        Objects.requireNonNull(user);
        Optional<STask<?>> tt = getLastTaskOrException().getFlowTask();
        if (tt.isPresent()) {
            if ((tt.get().isPeople() || tt.get().isWait()) && hasAllocatedUser() && isAllocated(user.getCod())) {
                return true;
            }
        }
        Optional<TaskAccessStrategy<FlowInstance>> strategey = getAccessStrategy();
        return strategey.isPresent() && strategey.get().canVisualize(this, user);
    }

    /**
     * Retorna os códigos de usuários com direito de execução da tarefa corrente
     * desta instância de fluxo.
     *
     * @return os códigos de usuários com direitos de execução.
     */
    public Set<Integer> getFirstLevelUsersCodWithAccess() {
        return getAccessStrategy()
                .map(strategy -> strategy.getFirstLevelUsersCodWithAccess(this))
                .orElse(Collections.emptySet());
    }

    /**
     * Retorna os usuários com direito de execução da tarefa corrente desta
     * instância de fluxo.
     *
     * @return os usuários com direitos de execução.
     */
    @Nonnull
    public List<SUser> listAllowedUsers() {
        return getAccessStrategy()
                .map(strategy -> (List<SUser>) strategy.listAllowedUsers(this))
                .orElse(Collections.emptyList());
    }

    /**
     * <p>
     * Formata uma mensagem de erro.
     * </p>
     * <p>
     * A formatação da mensagem segue o seguinte padrão:
     * </p>
     *
     * <pre>
     * getClass().getName() + &quot; - &quot; + getFullId() + &quot; : &quot; + message
     * </pre>
     *
     * @param message a mensagem a ser formatada.
     * @return a mensagem formatada.
     * @see #getFullId()
     */
    public final String createErrorMsg(String message) {
        return getClass().getName() + " - " + getFullId() + " : " + message;
    }

    @SuppressWarnings("rawtypes")
    private Optional<TaskAccessStrategy<FlowInstance>> getAccessStrategy() {
        return getState().map( task -> task.getAccessStrategy());
    }

    /**
     * Apenas para uso interno da engine de fluxo e da persistencia.
     */
    public final void refreshEntity() {
        getPersistenceService().refreshModel(getInternalEntity());
    }

    /**
     * Recupera a entidade persistente correspondente a esta instância de
     * fluxo.
     */
    public final IEntityFlowInstance getEntity() {
        if (codEntity == null && getInternalEntity().getCod() == null) {
            return saveEntity();
        }
        entity = getPersistenceService().retrieveFlowInstanceByCodOrException(codEntity);
        return entity;
    }

    /**
     * Retorna o usuário desta instância de fluxo atribuído ao papel
     * especificado.
     *
     * @param roleAbbreviation a sigla do papel especificado.
     * @return o usuário atribuído ao papel.
     */
    public final SUser getUserWithRole(String roleAbbreviation) {
        final IEntityRoleInstance entityRole = getEntity().getRoleUserByAbbreviation(roleAbbreviation);
        if (entityRole != null) {
            return entityRole.getUser();
        }
        return null;
    }

    /**
     * Recupera a lista de papeis da entidade persistente correspondente a esta
     * instância.
     */
    // TODO Daniel deveria retornar um objeto que isolasse da persistência
    final List<? extends IEntityRoleInstance> getUserRoles() {
        return getEntity().getRoles();
    }

    /**
     * Recupera a lista de papeis com a sigla especificada da entidade
     * persistente correspondente a esta instância.
     *
     * @param roleAbbreviation a sigla especificada.
     */
    public final IEntityRoleInstance getRoleUserByAbbreviation(String roleAbbreviation) {
        return getEntity().getRoleUserByAbbreviation(roleAbbreviation);
    }

    /**
     * Verifica se há papeis definidos.
     *
     * @return {@code true} caso haja pelo menos um papel definido;
     * {@code false} caso contrário.
     */
    public final boolean hasUserRoles() {
        return !getEntity().getRoles().isEmpty();
    }

    /**
     * Retorna o usuário que criou esta instância de fluxo.
     */
    public final SUser getUserCreator() {
        return getInternalEntity().getUserCreator();
    }

    /**
     * Altera a descrição desta instância de fluxo.
     * <p>A descrição será truncada para um tamanho máximo de 250 caracteres.</p>
     */
    public final void setDescription(String description) {
        getInternalEntity().setDescription(StringUtils.left(description, 250));
    }

    /**
     * Persiste esta instância de fluxo.
     *
     * @return a entidade persistida.
     */
    public final <K extends IEntityFlowInstance> K saveEntity() {
        setInternalEntity(getPersistenceService().saveFlowInstance(getInternalEntity()));
        return (K) getInternalEntity();
    }

    /**
     * Realiza uma transição manual da tarefa atual para a tarefa especificada.
     *
     * @param task a tarefa especificada.
     */
    public final void forceStateUpdate(@Nonnull STask<?> task) {
        Objects.requireNonNull(task);
        final TaskInstance originTask      = getLastTaskOrException();
        List<SUser> previousUsers = getDirectlyResponsibles();
        final Date         now             = new Date();
        TaskInstance       newTask        = updateState(originTask, null, task, now);
        originTask.log("Alteração Manual de Estado", "de '" + originTask.getName() + "' para '" + task.getName() + "'",
            null, Flow.getUserIfAvailable(), now).sendEmail(previousUsers);
        FlowEngine.initTask(this, task, newTask);
        ExecutionContext executionContext = new ExecutionContext(this, newTask, null);

        TaskInstance taskNew2 = getNewestTask(task).orElseThrow(() -> new SingularFlowException("Erro Interno", this));
        task.notifyTaskStart(taskNew2, executionContext);
        if (task.isImmediateExecution()) {
            prepareTransition().go();
        }
    }

    /**
     * Realiza uma transição da tarefa de origiem para a tarefa alvo
     * especificadas.
     *
     * @param originTransition a transição disparada.
     * @param task a tarefa alvo.
     * @param now o momento da transição.
     * @return a tarefa corrente depois da transição.
     */
    @Nonnull
    protected final TaskInstance updateState(TaskInstance originTaskInstance, STransition originTransition,
            @Nonnull STask<?> task, Date now) {
        synchronized (this) {
            if (originTaskInstance != null) {
                originTaskInstance.endLastAllocation();
                String transitionName = null;
                if (originTransition != null) {
                    transitionName = originTransition.getAbbreviation();
                }
                getPersistenceService().completeTask(originTaskInstance.getEntityTaskInstance(), transitionName, Flow.getUserIfAvailable());
            }
            IEntityTaskVersion newState = getFlowDefinition().getEntityTaskVersion(task);

            IEntityTaskInstance newTaskEntity = getPersistenceService().addTask(getEntity(), newState);

            TaskInstance newTask = getTaskInstance(newTaskEntity);
            currentState = task;

            Flow.notifyListeners(n -> n.notifyStateUpdate(FlowInstance.this));
            return newTask;
        }
    }

    /**
     * Retorna a data inicial desta instância.
     */
    @Nonnull
    public Date getBeginDate() {
        return getInternalEntity().getBeginDate();
    }

    /**
     * Retorna a data de encerramento desta instância.
     */
    @Nullable
    public final Date getEndDate() {
        return getInternalEntity().getEndDate();
    }

    /**
     * Retorna o código desta instância.
     */
    public final Integer getEntityCod() {
        return codEntity;
    }

    /**
     * Retorna o código desta instância como uma {@link String}.
     */
    public final String getId() {
        return getEntityCod().toString();
    }

    /**
     * Retorna um novo <b>ID</b> autogerado para esta instância.
     */
    public final String getFullId() {
        return Flow.generateID(this);
    }

    @Nonnull
    private TaskInstance getTaskInstance(@Nonnull IEntityTaskInstance entityTaskInstance) {
        return new TaskInstance(this, Objects.requireNonNull(entityTaskInstance));
    }

    /**
     * A mesma descrição completa de {@link #getCompleteDescription()}.
     */
    public String getDescription() {
        return getCompleteDescription();
    }

    /**
     * Retorna o nome do fluxo seguido da descrição completa.
     */
    public final String getExtendedDescription() {
        String description = getDescription();
        if (description == null) {
            return getFlowName();
        }
        return getFlowName() + " - " + description;
    }

    /**
     * Retorna a descrição atual desta instância.
     */
    protected final String getPersistedDescription() {
        String description = getInternalEntity().getDescription();
        if (description == null) {
            description = generateInitialDescription();
            if (!StringUtils.isBlank(description)) {
                setDescription(description);
            }
        }
        return description;
    }

    /**
     * Cria a descrição que vai gravada no banco de dados. Deve ser sobreescrito
     * para ter efeito.
     */
    @Nullable
    protected String generateInitialDescription() {
        return null;
    }

    /**
     * Sobrescreve a descrição da demanda a partir do método
     * {@link #generateInitialDescription()}.
     *
     * @return {@code true} caso tenha sido alterada a descrição; {@code false}
     * caso contrário.
     */
    public final boolean regenerateInitialDescription() {
        String description = generateInitialDescription();
        if (!StringUtils.isBlank(description) && !description.equalsIgnoreCase(getInternalEntity().getDescription())) {
            setDescription(description);
            return true;
        }
        return false;
    }

    /**
     * <p>
     * Cria versão extendida da descrição em relação ao campo descrição no BD.
     * </p>
     * <p>
     * Geralmente são adicionadas informações que não precisam ter cache feito
     * em banco de dados.
     * </p>
     *
     * @return a descrição atual desta instância.
     */
    protected String getCompleteDescription() {
        return getPersistedDescription();
    }

    /**
     * Retorna a lista de usuário diretamente responsáveis pela tarefa atual (se existir tarefa atual). Pode retorna
     * uma lista vazia se não houver tarefa taual ou se a tarefa não tive nenhum responsavel direto ou se nao fizer
     * sentido ter responsável direto (ex.: task Java).
     */
    @Nonnull
    public List<SUser> getDirectlyResponsibles() {
        return getCurrentTask().map(TaskInstance::getDirectlyResponsible).orElse(Collections.emptyList());
    }

    private void addUserRole(SBusinessRole sBusinessRole, SUser user) {
        if (getUserWithRole(sBusinessRole.getAbbreviation()) == null) {
            getPersistenceService().setInstanceUserRole(getEntity(),
                getFlowDefinition().getEntityFlowDefinition().getRole(sBusinessRole.getAbbreviation()), user);
        }
    }

    /**
     * Atribui ou substitui o usuário para o papel especificado.
     *
     * @param roleAbbreviation o papel especificado.
     * @param newUser o novo usuário atribuído ao papel.
     */
    public final void addOrReplaceUserRole(final String roleAbbreviation, SUser newUser) {
        SBusinessRole sBusinessRole = getFlowDefinition().getFlowMap().getRoleWithAbbreviation(roleAbbreviation);
        if (sBusinessRole == null) {
            throw new SingularFlowException("Não foi possível encontrar a role: " + roleAbbreviation, this);
        }
        SUser previousUser = getUserWithRole(sBusinessRole.getAbbreviation());
        if (Objects.isNull(previousUser)) {
            addOrReplaceUserRoleForNewUser(newUser, sBusinessRole);
        } else if (Objects.isNull(newUser)|| !previousUser.equals(newUser)) {
            addOrReplaceUserRoleForPreviousUser(newUser, sBusinessRole, previousUser);
        }
    }

    private void addOrReplaceUserRoleForPreviousUser(SUser newUser, SBusinessRole sBusinessRole, SUser previousUser) {
        IEntityFlowInstance entityTmp = getEntity();
        getPersistenceService().removeInstanceUserRole(entityTmp, entityTmp.getRoleUserByAbbreviation(sBusinessRole.getAbbreviation()));
        if (Objects.nonNull(newUser)) {
            addUserRole(sBusinessRole, newUser);
        }
        getFlowDefinition().getFlowMap().notifyRoleChange(this, sBusinessRole, previousUser, newUser);
        Optional<TaskInstance> latestTask = getNewestTask();
        if (latestTask.isPresent()) {
            if (Objects.nonNull(newUser)) {
                latestTask.get().log("Papel alterado", String.format("%s: %s", sBusinessRole.getName(), newUser.getSimpleName()));
            } else {
                latestTask.get().log("Papel removido", sBusinessRole.getName());
            }
        }
    }

    private void addOrReplaceUserRoleForNewUser(SUser newUser, SBusinessRole sBusinessRole) {
        if (Objects.nonNull(newUser)) {
            addUserRole(sBusinessRole, newUser);
            getFlowDefinition().getFlowMap().notifyRoleChange(this, sBusinessRole, null, newUser);
            Optional<TaskInstance> latestTask = getNewestTask();
            latestTask.ifPresent(taskInstance -> taskInstance.log("Papel definido", String.format("%s: %s", sBusinessRole.getName(), newUser.getSimpleName())));
        }
    }

    /**
     * Configura o valor variável especificada.
     *
     * @param variableName o nome da variável especificada.
     * @param value o valor a ser configurado.
     */
    public void setVariable(String variableName, Object value) {
        getVariables().setValue(variableName, value);
    }

    /**
     * Retorna o valor da variável do tipo {@link Boolean} especificada.
     */
    public final Boolean getVariableValueBoolean(String variableName) {
        return getVariables().getValueBoolean(variableName);
    }

    /**
     * Retorna o valor da variável do tipo {@link String} especificada.
     */
    public String getVariableValueString(String variableName) {
        return getVariables().getValueString(variableName);
    }

    /**
     * Retorna o valor da variável do tipo {@link Integer} especificada.
     */
    public final Integer getVariableValueInteger(String variableName) {
        return getVariables().getValueInteger(variableName);
    }

    /**
     * Retorna o valor da variável especificada.
     */
    public <T> T getVariableValue(String variableName) {
        return getVariables().getValue(variableName);
    }

    /**
     * Retorna o mapa das variáveis desta instância de fluxo.
     */
    public final VarInstanceMap<?,?> getVariables() {
        if (variables == null) {
            variables = new VarInstanceTableFlow(this);
        }
        return variables;
    }

    /**
     * Verifica se há usuário alocado em alguma tarefa desta instância de
     * fluxo.
     *
     * @return {@code true} caso haja algum usuário alocado; {@code false} caso
     * contrário.
     */
    public boolean hasAllocatedUser() {
        return getEntity().getTasks().stream().anyMatch(task -> task.isActive() && task.getAllocatedUser() != null);
    }

    /**
     * Retorna os usuários alocados nas tarefas ativas
     */
    @Nonnull
    public Set<SUser> getAllocatedUsers() {
        return getEntity().getTasks().stream().filter(task -> task.isActive() && task.getAllocatedUser() != null).map(task -> task.getAllocatedUser()).collect(Collectors.toSet());
    }

    /**
     * Verifica se o usuário especificado está alocado em alguma tarefa desta
     * instância de fluxo.
     *
     * @param userCod o código usuário especificado.
     * @return {@code true} caso o usuário esteja alocado; {@code false} caso
     * contrário.
     */
    public boolean isAllocated(Integer userCod) {
        return getEntity().getTasks().stream().anyMatch(task -> task.isActive() && task.getAllocatedUser() != null
            && task.getAllocatedUser().getCod().equals(userCod));
    }

    /** Retorna a lista de todas as tarefas ordenadas da mais antiga para a mais nova. */
    @Nonnull
    public List<TaskInstance> getTasksOlderFirst() {
        return getTasksOlderFirstAsStream().collect(Collectors.toList());
    }

    /** Retorna a lista de todas as tarefas ordenadas da mais antiga para a mais nova. */
    @Nonnull
    public Stream<TaskInstance> getTasksOlderFirstAsStream() {
        IEntityFlowInstance instance = getEntity();
        return instance.getTasks().stream().map(this::getTaskInstance);
    }

    /** Retorna a lista de todas as tarefas ordenadas da mais nova para a mais antiga. */
    public Stream<TaskInstance> getTasksNewerFirstAsStream() {
        IEntityFlowInstance instance = getEntity();
        return Lists.reverse(instance.getTasks()).stream().map(this::getTaskInstance);
    }

    public Stream<TaskInstance> getTasksNewerFirstAsStream(ITaskDefinition... tasksTypes) {
        return getTasksNewerFirstAsStream().filter(TaskPredicates.simpleTaskType(tasksTypes));
    }

    public Stream<TaskInstance> getTasksNewerFirstAsStream(List<ITaskDefinition> tasksTypes) {
        return getTasksNewerFirstAsStream().filter(TaskPredicates.simpleTaskType(tasksTypes));
    }

    /**
     * Retorna a mais nova tarefa que atende a condição informada.
     */
    @Nonnull
    public Optional<TaskInstance> getNewestTask(@Nonnull Predicate<TaskInstance> predicate) {
        Objects.requireNonNull(predicate);
        List<? extends IEntityTaskInstance> list = getEntity().getTasks();
        for (int i = list.size() - 1; i != -1; i--) {
            TaskInstance task = getTaskInstance(list.get(i));
            if (predicate.test(task)) {
                return Optional.of(task);
            }
        }
        return Optional.empty();
    }

    /** Retorna a tarefa atual (tarefa ativa). */
    @Nonnull
    public Optional<TaskInstance> getCurrentTask() {
        return getEntity().getCurrentTask().map(this::getTaskInstance);
    }

    /** Retorna a tarefa atual (tarefa ativa) ou dispara exception senão existir. */
    @Nonnull
    public TaskInstance getCurrentTaskOrException() {
        return getCurrentTask().orElseThrow(
                () -> new SingularFlowException(createErrorMsg("Não há tarefa atual para essa instancia de fluxo"),
                        this));
    }

    /**
     * Retorna a mais nova tarefa encerrada ou ativa.
     */
    @Nonnull
    public Optional<TaskInstance> getNewestTask() {
        return getNewestTask(t -> true);
    }

    /**
     * Retorna a última tarefa encerrada ou ativa.
     */
    @Nonnull
    public TaskInstance getLastTaskOrException() {
        return getNewestTask().orElseThrow(
                () -> new SingularFlowException(createErrorMsg("Não há nenhuma tarefa no fluxo"), this));
    }

    /**
     * Encontra a mais nova tarefa encerrada ou ativa com a sigla da referência.
     */
    @Nonnull
    public Optional<TaskInstance> getNewestTask(@Nonnull ITaskDefinition taskRef) {
        return getNewestTask(TaskPredicates.simpleTaskType(taskRef));
    }

    /**
     * Encontra a mais nova tarefa encerrada ou ativa do tipo informado.
     */
    @Nonnull
    public Optional<TaskInstance> getNewestTask(@Nonnull STask<?> type) {
        return getNewestTask(TaskPredicates.simpleTaskType(type));
    }

    /**
     * Encontra a mais nova tarefa encerrada e com a mesma sigla da referência.
     */
    @Nonnull
    public Optional<TaskInstance> getFinishedTask(@Nonnull ITaskDefinition taskRef) {
        return getNewestTask(TaskPredicates.finished().and(TaskPredicates.simpleTaskType(taskRef)));
    }

    /**
     * Encontra a mais nova tarefa encerrada e com a mesma sigla do tipo.
     */
    @Nonnull
    public Optional<TaskInstance> getFinishedTask(@Nonnull STask<?> type) {
        return getNewestTask(TaskPredicates.finished().and(TaskPredicates.simpleTaskType(type)));
    }

    protected IPersistenceService<IEntityCategory, IEntityFlowDefinition, IEntityFlowVersion, IEntityFlowInstance, IEntityTaskInstance, IEntityTaskDefinition, IEntityTaskVersion, IEntityVariableInstance, IEntityRoleDefinition, IEntityRoleInstance> getPersistenceService() {
        return getFlowDefinition().getPersistenceService();
    }

    /**
     * Configura o contexto de execução.
     * @param executionContext o novo contexto de execução.
     */
    final void setExecutionContext(@Nullable ExecutionContext executionContext) {
        if (this.executionContext != null && executionContext != null) {
            throw new SingularFlowException(createErrorMsg("A instancia já está com um tarefa em execução"),
                    this);
        }
        this.executionContext = executionContext;
    }

    /**
     * Retorna a última tarefa encerrada.
     * Caso a tarefa atual esteja finalizada essa será retornada.
     * Caso a tarefa atual esteja ativa, será retornada a tarefa imediatamente anterior.
     */
    @Nonnull
    public Optional<TaskInstance> getLastFinishedTask() {
        return getNewestTask(TaskPredicates.finished());
    }
}
