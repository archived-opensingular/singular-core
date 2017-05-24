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
import org.opensingular.flow.core.entity.IEntityProcessDefinition;
import org.opensingular.flow.core.entity.IEntityProcessInstance;
import org.opensingular.flow.core.entity.IEntityProcessVersion;
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
 * <p>
 * Esta é a classe responsável por manter os dados de instância de um
 * determinado processo.
 * </p>
 *
 * @author Daniel Bordin
 */
@SuppressWarnings({"serial", "unchecked"})
public class ProcessInstance implements Serializable {

    private RefProcessDefinition processDefinitionRef;

    private Integer codEntity;

    private transient IEntityProcessInstance entity;

    private transient STask<?> estadoAtual;

    private transient ExecutionContext executionContext;

    private transient VarInstanceMap<?, ?> variables;

    final void setProcessDefinition(ProcessDefinition<?> processDefinition) {
        if (processDefinitionRef != null) {
            throw SingularException.rethrow("Erro Interno");
        }
        processDefinitionRef = RefProcessDefinition.of(processDefinition);
        processDefinitionRef.get().inject(this);
    }

    /**
     * <p>
     * Retorna a definição de processo desta instância.
     * </p>
     *
     * @param <K> o tipo da definição de processo.
     * @return a definição de processo desta instância.
     */
    @Nonnull
    public <K extends ProcessDefinition<?>> K getProcessDefinition() {
        if (processDefinitionRef == null) {
            throw SingularException.rethrow(
                    "A instância não foi inicializada corretamente, pois não tem uma referência a ProcessDefinition! Tente chamar o método newPreStartInstance() a partir da definição do processo.");
        }
        return (K) processDefinitionRef.get();
    }

    /**
     * Inicia esta instância de processo.
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

    final @Nonnull
    IEntityProcessInstance getInternalEntity() {
        if (entity == null) {
            if (codEntity != null) {
                IEntityProcessInstance   newfromDB               = getPersistenceService().retrieveProcessInstanceByCodOrException(codEntity);
                IEntityProcessDefinition entityProcessDefinition = getProcessDefinition().getEntityProcessDefinition();
                if (!entityProcessDefinition.equals(newfromDB.getProcessVersion().getProcessDefinition())) {
                    throw SingularException.rethrow(getProcessDefinition().getName() + " id=" + codEntity +
                            " se refere a definição de processo " +
                            newfromDB.getProcessVersion().getProcessDefinition().getKey() +
                            " mas era esperado que referenciasse " +
                            entityProcessDefinition);
                }
                entity = newfromDB;
            }
            if (entity == null) {
                throw SingularException.rethrow(
                        getClass().getName() + " is not binded to a new and neither to a existing database intance process entity.");
            }
        }
        return entity;
    }

    final void setInternalEntity(IEntityProcessInstance entity) {
        Objects.requireNonNull(entity);
        this.entity = entity;
        this.codEntity = entity.getCod();
    }

    /**
     * <p>
     * Configura a instância "pai" desta instância de processo.
     * </p>
     *
     * @param pai a instância "pai".
     */
    public void setParent(ProcessInstance pai) {
        getPersistenceService().setProcessInstanceParent(getInternalEntity(), pai.getInternalEntity());
    }

    /**
     * <p>
     * Retorna a tarefa "pai" desta instância de processo.
     * </p>
     *
     * @return a tarefa "pai".
     */
    public TaskInstance getParentTask() {
        IEntityTaskInstance dbTaskInstance = getInternalEntity().getParentTask();
        return dbTaskInstance == null ? null : Flow.getTaskInstance(dbTaskInstance);
    }

    /**
     * Retorna o tipo da tarefa corrente desta instância de processo. Pode não encotrar se a tarefa em banco não
     * tiver correspondência com o fluxo do processo em memória (tarefa legada).
     */
    public Optional<STask<?>> getState() {
        if (estadoAtual == null) {
            Optional<TaskInstance> current = getCurrentTask();
            if (current.isPresent()) {
                estadoAtual = getProcessDefinition().getFlowMap().getTaskByAbbreviation(current.get().getAbbreviation()).orElse(null);
            } else if (isFinished()) {
                current = getTaskNewer();
                if (current.isPresent() && current.get().isFinished()) {
                    estadoAtual = getProcessDefinition().getFlowMap().getTaskByAbbreviation(current.get().getAbbreviation()).orElse(null);
                } else {
                    throw new SingularFlowException(createErrorMsg(
                            "incossitencia: o estado final está null, mas deveria ter um estado do tipo final por " +
                                    "estar finalizado"), this);
                }
            } else {
                throw new SingularFlowException(createErrorMsg("getState() não pode ser invocado para essa instância"), this);
            }
        }
        return Optional.ofNullable(estadoAtual);
    }

    /**
     * <p>
     * Verifica se esta instância está encerrada.
     * </p>
     *
     * @return {@code true} caso esta instância está encerrada; {@code false}
     * caso contrário.
     */
    public boolean isFinished() {
        return getEndDate() != null;
    }

    /**
     * <p>
     * Retornar o nome da definição de processo desta instância.
     * </p>
     *
     * @return o nome da definição de processo.
     */
    public String getProcessName() {
        return getProcessDefinition().getName();
    }

    /**
     * Retorna o nome da tarefa atual desta instância de processo.
     *
     * @return o nome da tarefa atual; ou {@code null} caso não haja uma tarefa atual.
     */
    @Nonnull
    public Optional<String> getCurrentTaskName() {
        Optional<String> name = getState().map(STask::getName);
        if (!name.isPresent()) {
            name = getCurrentTask().map(TaskInstance::getName);
        }
        return name;
    }

    /**
     * <p>
     * Retorna o <i>link resolver</i> padrão desta instância de processo.
     * </p>
     *
     * @return o <i>link resolver</i> padrão.
     */
    public final Lnk getDefaultHref() {
        return Flow.getDefaultHrefFor(this);
    }

    /**
     * <p>
     * Retorna os códigos de usuários com direito de execução da tarefa humana
     * definida para o processo correspondente a esta instância.
     * </p>
     *
     * @param nomeTarefa o nome da tarefa humana a ser inspecionada.
     * @return os códigos de usuários com direitos de execução.
     */
    public Set<Integer> getFirstLevelUsersCodWithAccess(String nomeTarefa) {
        return getProcessDefinition().getFlowMap().getPeopleTaskByAbbreviationOrException(nomeTarefa).getAccessStrategy()
                .getFirstLevelUsersCodWithAccess(this);
    }

    /**
     * <p>
     * Verifica de o usuário especificado pode executar a tarefa corrente desta
     * instância de processo.
     * </p>
     *
     * @param user o usuário especificado.
     * @return {@code true} caso o usuário possa executar a tarefa corrente;
     * {@code false} caso contrário.
     */
    public final boolean canExecuteTask(SUser user) {
        Optional<STask<?>> currentState = getState();
        if (!currentState.isPresent()) {
            return false;
        }
        IEntityTaskType tt = currentState.get().getTaskType();
        if (tt.isPeople() || tt.isWait()) {
            if (isAllocated(user.getCod())) {
                return true;
            }
            Optional<TaskAccessStrategy<ProcessInstance>> strategy = getAccessStrategy();
            return strategy.isPresent() && strategy.get().canExecute(this, user);
        }
        return false;
    }

    /**
     * Verifica de o usuário especificado pode visualizar a tarefa corrente
     * desta instância de processo.
     *
     * @param user o usuário especificado.
     * @return {@code true} caso o usuário possa visualizar a tarefa corrente;
     * {@code false} caso contrário.
     */
    public boolean canVisualize(@Nonnull SUser user) {
        Objects.requireNonNull(user);
        Optional<STask<?>> tt = getLatestTaskOrException().getFlowTask();
        if (tt.isPresent()) {
            if ((tt.get().isPeople() || tt.get().isWait()) && hasAllocatedUser() && isAllocated(user.getCod())) {
                return true;
            }
        }
        Optional<TaskAccessStrategy<ProcessInstance>> strategey = getAccessStrategy();
        return strategey.isPresent() && strategey.get().canVisualize(this, user);
    }

    /**
     * Retorna os códigos de usuários com direito de execução da tarefa corrente
     * desta instância de processo.
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
     * instância de processo.
     *
     * @return os usuários com direitos de execução.
     */
    public List<SUser> listAllocableUsers() {
        return getAccessStrategy()
                .map(strategy -> (List<SUser>) strategy.listAllocableUsers(this))
                .orElse(Collections.emptyList());
    }

    /**
     * <p>
     * Formata uma mensagem de erro.
     * </p>
     * <p>
     * A formatação da mensagem segue o seguinte padrão:
     * </p>
     * <p>
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
    private Optional<TaskAccessStrategy<ProcessInstance>> getAccessStrategy() {
        return getState().map(task -> task.getAccessStrategy());
    }

    /**
     * Apenas para uso interno da engine de processo e da persistencia.
     */
    public final void refreshEntity() {
        getPersistenceService().refreshModel(getInternalEntity());
    }

    /**
     * Recupera a entidade persistente correspondente a esta instância de
     * processo.
     */
    public final IEntityProcessInstance getEntity() {
        if (codEntity == null && getInternalEntity().getCod() == null) {
            return saveEntity();
        }
        entity = getPersistenceService().retrieveProcessInstanceByCodOrException(codEntity);
        return entity;
    }

    /**
     * <p>
     * Retorna o usuário desta instância de processo atribuído ao papel
     * especificado.
     * </p>
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
     * Retorna o usuário que criou esta instância de processo.
     */
    public final SUser getUserCreator() {
        return getInternalEntity().getUserCreator();
    }

    /**
     * Altera a descrição desta instância de processo.
     * <p>
     * A descrição será truncada para um tamanho máximo de 250 caracteres.
     * </p>
     *
     * @param descricao a nova descrição.
     */
    public final void setDescription(String descricao) {
        getInternalEntity().setDescription(StringUtils.left(descricao, 250));
    }

    /**
     * Persiste esta instância de processo.
     *
     * @param <K> o tipo da entidade desta instância.
     * @return a entidade persistida.
     */
    public final <K extends IEntityProcessInstance> K saveEntity() {
        setInternalEntity(getPersistenceService().saveProcessInstance(getInternalEntity()));
        return (K) getInternalEntity();
    }

    /**
     * Realiza uma transição manual da tarefa atual para a tarefa especificada.
     *
     * @param task a tarefa especificada.
     */
    public final void forceStateUpdate(@Nonnull STask<?> task) {
        Objects.requireNonNull(task);
        final TaskInstance tarefaOrigem      = getLatestTaskOrException();
        List<SUser>        pessoasAnteriores = getDirectlyResponsibles();
        final Date         agora             = new Date();
        TaskInstance       tarefaNova        = updateState(tarefaOrigem, null, task, agora);
        tarefaOrigem.log("Alteração Manual de Estado", "de '" + tarefaOrigem.getName() + "' para '" + task.getName() + "'",
                null, Flow.getUserIfAvailable(), agora).sendEmail(pessoasAnteriores);
        FlowEngine.initTask(this, task, tarefaNova);
        ExecutionContext execucaoMTask = new ExecutionContext(this, tarefaNova, null);

        TaskInstance taskNew2 = getTaskNewer(task).orElseThrow(() -> new SingularFlowException("Erro Interno", this));
        task.notifyTaskStart(taskNew2, execucaoMTask);
        if (task.isImmediateExecution()) {
            prepareTransition().go();
        }
    }

    /**
     * <p>
     * Realiza uma transição da tarefa de origiem para a tarefa alvo
     * especificadas.
     * </p>
     *
     * @param originTaskInstance a tarefa de origem.
     * @param transicaoOrigem    a transição disparada.
     * @param task               a tarefa alvo.
     * @param agora              o momento da transição.
     * @return a tarefa corrente depois da transição.
     */
    protected final TaskInstance updateState(TaskInstance originTaskInstance, STransition transicaoOrigem,
                                             @Nonnull STask<?> task, Date agora) {
        synchronized (this) {
            if (originTaskInstance != null) {
                originTaskInstance.endLastAllocation();
                String transitionName = null;
                if (transicaoOrigem != null) {
                    transitionName = transicaoOrigem.getAbbreviation();
                }
                getPersistenceService().completeTask(originTaskInstance.getEntityTaskInstance(), transitionName, Flow.getUserIfAvailable());
            }
            IEntityTaskVersion situacaoNova = getProcessDefinition().getEntityTaskVersion(task);

            IEntityTaskInstance tarefa = getPersistenceService().addTask(getEntity(), situacaoNova);

            TaskInstance tarefaNova = getTaskInstance(tarefa);
            estadoAtual = task;

            Flow.notifyListeners(n -> n.notifyStateUpdate(ProcessInstance.this));
            return tarefaNova;
        }
    }

    /**
     * Retorna a data inicial desta instância.
     *
     * @return nunca null.
     */
    public Date getBeginDate() {
        return getInternalEntity().getBeginDate();
    }

    /**
     * <p>
     * Retorna a data de encerramento desta instância.
     * </p>
     *
     * @return a data de encerramento.
     */
    public final Date getEndDate() {
        return getInternalEntity().getEndDate();
    }

    /**
     * <p>
     * Retorna o código desta instância.
     * </p>
     *
     * @return o código.
     */
    public final Integer getEntityCod() {
        return codEntity;
    }

    /**
     * <p>
     * Retorna o código desta instância como uma {@link String}.
     * </p>
     *
     * @return o código.
     */
    public final String getId() {
        return getEntityCod().toString();
    }

    /**
     * <p>
     * Retorna um novo <b>ID</b> autogerado para esta instância.
     * </p>
     *
     * @return o <b>ID</b> autogerado.
     */
    public final String getFullId() {
        return Flow.generateID(this);
    }

    @Nonnull
    private TaskInstance getTaskInstance(@Nonnull IEntityTaskInstance tarefa) {
        return new TaskInstance(this, Objects.requireNonNull(tarefa));
    }

    /**
     * <p>
     * O mesmo que {@link #getCompleteDescription()}.
     * </p>
     *
     * @return a descrição completa.
     */
    public String getDescription() {
        return getCompleteDescription();
    }

    /**
     * <p>
     * Retorna o nome do processo seguido da descrição completa.
     *
     * @return o nome do processo seguido da descrição completa.
     */
    public final String getExtendedDescription() {
        String descricao = getDescription();
        if (descricao == null) {
            return getProcessName();
        }
        return getProcessName() + " - " + descricao;
    }

    /**
     * <p>
     * Retorna a descrição atual desta instância.
     * </p>
     *
     * @return a descrição atual.
     */
    protected final String getPersistedDescription() {
        String descricao = getInternalEntity().getDescription();
        if (descricao == null) {
            descricao = generateInitialDescription();
            if (!StringUtils.isBlank(descricao)) {
                setDescription(descricao);
            }
        }
        return descricao;
    }

    /**
     * <p>
     * Cria a descrição que vai gravada no banco de dados. Deve ser sobreescrito
     * para ter efeito.
     * </p>
     *
     * @return a descrição criada.
     */
    protected String generateInitialDescription() {
        return null;
    }

    /**
     * <p>
     * Sobrescreve a descrição da demanda a partir do método
     * {@link #generateInitialDescription()}.
     * </p>
     *
     * @return {@code true} caso tenha sido alterada a descrição; {@code false}
     * caso contrário.
     */
    public final boolean regenerateInitialDescription() {
        String descricao = generateInitialDescription();
        if (!StringUtils.isBlank(descricao) && !descricao.equalsIgnoreCase(getInternalEntity().getDescription())) {
            setDescription(descricao);
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
        return getCurrentTask().map(TaskInstance::getDirectlyResponsibles).orElse(Collections.emptyList());
    }

    private void addUserRole(SProcessRole sProcessRole, SUser user) {
        if (getUserWithRole(sProcessRole.getAbbreviation()) == null) {
            getPersistenceService().setInstanceUserRole(getEntity(),
                    getProcessDefinition().getEntityProcessDefinition().getRole(sProcessRole.getAbbreviation()), user);
        }
    }

    /**
     * <p>
     * Atribui ou substitui o usuário para o papel especificado.
     * </p>
     *
     * @param roleAbbreviation o papel especificado.
     * @param newUser          o novo usuário atribuído ao papel.
     */
    public final void addOrReplaceUserRole(final String roleAbbreviation, SUser newUser) {
        SProcessRole sProcessRole = getProcessDefinition().getFlowMap().getRoleWithAbbreviation(roleAbbreviation);
        if (sProcessRole == null) {
            throw new SingularFlowException("Não foi possível encontrar a role: " + roleAbbreviation, this);
        }
        SUser previousUser = getUserWithRole(sProcessRole.getAbbreviation());

        if (isNewUser(newUser, previousUser)) {
            addUserRole(sProcessRole, newUser);
            getProcessDefinition().getFlowMap().notifyRoleChange(this, sProcessRole, null, newUser);
            getTaskNewer().ifPresent(taskInstance -> taskInstance.log("Papel definido", String.format("%s: %s", sProcessRole.getName(), newUser.getSimpleName())));
        } else if (isNewUserNotEqualsToPrevious(newUser, previousUser)) {
            IEntityProcessInstance entityTmp = getEntity();
            getPersistenceService().removeInstanceUserRole(entityTmp, entityTmp.getRoleUserByAbbreviation(sProcessRole.getAbbreviation()));
            if (isUserNotNull(newUser)) {
                addUserRole(sProcessRole, newUser);
            }
            getProcessDefinition().getFlowMap().notifyRoleChange(this, sProcessRole, previousUser, newUser);
            Optional<TaskInstance> latestTask = getTaskNewer();
            if (latestTask.isPresent()) {
                if (isUserNotNull(newUser)) {
                    latestTask.get().log("Papel alterado", String.format("%s: %s", sProcessRole.getName(), newUser.getSimpleName()));
                } else {
                    latestTask.get().log("Papel removido", sProcessRole.getName());
                }
            }
        }
    }

    protected boolean isUserNotNull(SUser user) {
        return user != null;
    }

    protected boolean isNewUser(SUser newUser, SUser previousUser) {
        return previousUser == null && isUserNotNull(newUser);
    }

    protected boolean isNewUserNotEqualsToPrevious(SUser newUser, SUser previousUser) {
        return newUser == null || !previousUser.equals(newUser);
    }

    /**
     * <p>
     * Configura o valor variável especificada.
     * </p>
     *
     * @param variableName o nome da variável especificada.
     * @param value        o valor a ser configurado.
     */
    public void setVariable(String variableName, Object value) {
        getVariables().setValue(variableName, value);
    }

    /**
     * <p>
     * Retorna o valor da variável do tipo {@link Boolean} especificada.
     * </p>
     *
     * @param variableName o nome da variável especificada.
     * @return o valor da variável.
     */
    public final Boolean getVariableValueBoolean(String variableName) {
        return getVariables().getValueBoolean(variableName);
    }

    /**
     * <p>
     * Retorna o valor da variável do tipo {@link String} especificada.
     * </p>
     *
     * @param variableName o nome da variável especificada.
     * @return o valor da variável.
     */
    public String getVariableValueString(String variableName) {
        return getVariables().getValueString(variableName);
    }

    /**
     * <p>
     * Retorna o valor da variável do tipo {@link Integer} especificada.
     * </p>
     *
     * @param variableName o nome da variável especificada.
     * @return o valor da variável.
     */
    public final Integer getVariableValueInteger(String variableName) {
        return getVariables().getValueInteger(variableName);
    }

    /**
     * <p>
     * Retorna o valor da variável especificada.
     * </p>
     *
     * @param <T>          o tipo da variável especificada.
     * @param variableName o nome da variável especificada.
     * @return o valor da variável.
     */
    public <T> T getVariableValue(String variableName) {
        return getVariables().getValue(variableName);
    }

    /**
     * <p>
     * Retorna o mapa das variáveis desta instância de processo.
     * </p>
     *
     * @return o mapa das variáveis.
     */
    public final VarInstanceMap<?, ?> getVariables() {
        if (variables == null) {
            variables = new VarInstanceTableProcess(this);
        }
        return variables;
    }

    /**
     * <p>
     * Verifica se há usuário alocado em alguma tarefa desta instância de
     * processo.
     * </p>
     *
     * @return {@code true} caso haja algum usuário alocado; {@code false} caso
     * contrário.
     */
    public boolean hasAllocatedUser() {
        return getEntity().getTasks().stream().anyMatch(tarefa -> tarefa.isActive() && isUserNotNull(tarefa.getAllocatedUser()));
    }

    /**
     * <p>
     * Retorna os usuários alocados nas tarefas ativas
     * </p>
     *
     * @return a lista de usuários (<i>null safe</i>).
     */
    public Set<SUser> getAllocatedUsers() {
        return getEntity().getTasks().stream().filter(tarefa -> tarefa.isActive() && isUserNotNull(tarefa.getAllocatedUser())).map(tarefa -> tarefa.getAllocatedUser()).collect(Collectors.toSet());
    }

    /**
     * <p>
     * Verifica se o usuário especificado está alocado em alguma tarefa desta
     * instância de processo.
     * </p>
     *
     * @param codPessoa o código usuário especificado.
     * @return {@code true} caso o usuário esteja alocado; {@code false} caso
     * contrário.
     */
    public boolean isAllocated(Integer codPessoa) {
        return getEntity().getTasks().stream().anyMatch(tarefa -> tarefa.isActive() && isUserNotNull(tarefa.getAllocatedUser())
                && tarefa.getAllocatedUser().getCod().equals(codPessoa));
    }

    /**
     * Retorna a lista de todas as tarefas ordenadas da mais antiga para a mais nova.
     */
    @Nonnull
    public List<TaskInstance> getTasksOlderFirst() {
        return getTasksOlderFirstAsStream().collect(Collectors.toList());
    }

    /**
     * Retorna a lista de todas as tarefas ordenadas da mais antiga para a mais nova.
     */
    @Nonnull
    public Stream<TaskInstance> getTasksOlderFirstAsStream() {
        IEntityProcessInstance demanda = getEntity();
        return demanda.getTasks().stream().map(this::getTaskInstance);
    }

    /**
     * Retorna a lista de todas as tarefas ordenadas da mais nova para a mais antiga.
     */
    public Stream<TaskInstance> getTasksNewerFirstAsStream() {
        IEntityProcessInstance demanda = getEntity();
        return Lists.reverse(demanda.getTasks()).stream().map(this::getTaskInstance);
    }

    public Stream<TaskInstance> getTasksNewerFirstAsStream(ITaskDefinition... tasksTypes) {
        return getTasksNewerFirstAsStream().filter(TaskPredicates.simpleTaskType(tasksTypes));
    }

    public Stream<TaskInstance> getTasksNewerFirstAsStream(List<ITaskDefinition> tasksTypes) {
        return getTasksNewerFirstAsStream().filter(TaskPredicates.simpleTaskType(tasksTypes));
    }

    /**
     * Retorna a mais nova tarefa que atende a condição informada.
     *
     * @param condicao a condição informada.
     */
    @Nonnull
    public Optional<TaskInstance> getTaskNewer(@Nonnull Predicate<TaskInstance> condicao) {
        Objects.requireNonNull(condicao);
        List<? extends IEntityTaskInstance> lista = getEntity().getTasks();
        for (int i = lista.size() - 1; i != -1; i--) {
            TaskInstance task = getTaskInstance(lista.get(i));
            if (condicao.test(task)) {
                return Optional.of(task);
            }
        }
        return Optional.empty();
    }

    /**
     * Retorna a tarefa atual (tarefa ativa).
     */
    @Nonnull
    public Optional<TaskInstance> getCurrentTask() {
        return getTaskNewer(t -> t.isActive());
    }

    /**
     * Retorna a tarefa atual (tarefa ativa) ou dispara exception senão existir.
     */
    @Nonnull
    public TaskInstance getCurrentTaskOrException() {
        return getCurrentTask().orElseThrow(
                () -> new SingularFlowException(createErrorMsg("Não há tarefa atual para essa instancia de processo"),
                        this));
    }

    /**
     * Retorna a mais nova tarefa encerrada ou ativa.
     */
    @Nonnull
    public Optional<TaskInstance> getTaskNewer() {
        return getTaskNewer(t -> true);
    }

    /**
     * Retorna a mais nova tarefa encerrada ou ativa.
     */
    @Nonnull
    public TaskInstance getLatestTaskOrException() {
        return getTaskNewer().orElseThrow(
                () -> new SingularFlowException(createErrorMsg("Não há nenhuma tarefa no processo"), this));
    }

    /**
     * Encontra a mais nova tarefa encerrada ou ativa com a sigla da referência.
     *
     * @param taskRef a referência.
     */
    @Nonnull
    public Optional<TaskInstance> getTaskNewer(@Nonnull ITaskDefinition taskRef) {
        return getTaskNewer(TaskPredicates.simpleTaskType(taskRef));
    }

    /**
     * Encontra a mais nova tarefa encerrada ou ativa do tipo informado.
     *
     * @param tipo o tipo informado.
     */
    @Nonnull
    public Optional<TaskInstance> getTaskNewer(@Nonnull STask<?> tipo) {
        return getTaskNewer(TaskPredicates.simpleTaskType(tipo));
    }

    /**
     * Encontra a mais nova tarefa encerrada e com a mesma sigla da referência.
     *
     * @param taskRef a referência.
     */
    @Nonnull
    public Optional<TaskInstance> getFinishedTask(@Nonnull ITaskDefinition taskRef) {
        return getTaskNewer(TaskPredicates.simpleFinished().and(TaskPredicates.simpleTaskType(taskRef)));
    }

    /**
     * Encontra a mais nova tarefa encerrada e com a mesma sigla do tipo.
     *
     * @param tipo o tipo.
     */
    @Nonnull
    public Optional<TaskInstance> getFinishedTask(@Nonnull STask<?> tipo) {
        return getTaskNewer(TaskPredicates.simpleFinished().and(TaskPredicates.simpleTaskType(tipo)));
    }

    protected IPersistenceService<IEntityCategory, IEntityProcessDefinition, IEntityProcessVersion, IEntityProcessInstance, IEntityTaskInstance, IEntityTaskDefinition, IEntityTaskVersion, IEntityVariableInstance, IEntityRoleDefinition, IEntityRoleInstance> getPersistenceService() {
        return getProcessDefinition().getPersistenceService();
    }

    /**
     * Configura o contexto de execução.
     *
     * @param execucaoTask o novo contexto de execução.
     */
    final void setExecutionContext(@Nullable ExecutionContext execucaoTask) {
        if (this.executionContext != null && execucaoTask != null) {
            throw new SingularFlowException(createErrorMsg("A instancia já está com um tarefa em processo de execução"),
                    this);
        }
        this.executionContext = execucaoTask;
    }

}
