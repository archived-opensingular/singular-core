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

import org.opensingular.flow.core.builder.ITaskDefinition;
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
import org.opensingular.flow.core.variable.ValidationResult;
import org.opensingular.flow.core.variable.VarInstanceMap;
import org.opensingular.flow.core.view.Lnk;
import org.opensingular.lib.commons.base.SingularException;
import org.opensingular.flow.core.service.IPersistenceService;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * <p>
 * Esta é a classe responsável por manter os dados de instância de um
 * determinado processo.
 * </p>
 *
 * @author Daniel Bordin
 */
@SuppressWarnings({ "serial", "unchecked" })
public class ProcessInstance implements Serializable {

    private RefProcessDefinition processDefinitionRef;

    private Integer codEntity;

    private transient IEntityProcessInstance entity;

    private transient MTask<?> estadoAtual;

    private transient ExecutionContext executionContext;

    private transient VarInstanceMap<?> variables;

    final void setProcessDefinition(ProcessDefinition<?> processDefinition) {
        if (processDefinitionRef != null) {
            throw new SingularException("Erro Interno");
        }
        processDefinitionRef = RefProcessDefinition.of(processDefinition);
    }

    /**
     * <p>
     * Retorna a definição de processo desta instância.
     * </p>
     *
     * @param <K> o tipo da definição de processo.
     * @return a definição de processo desta instância.
     */
    public <K extends ProcessDefinition<?>> K getProcessDefinition() {
        if (processDefinitionRef == null) {
            throw new SingularException(
                    "A instância não foi inicializada corretamente, pois não tem uma referência a ProcessDefinition! Tente chamar o método newInstance() a partir da definição do processo.");
        }
        return (K) processDefinitionRef.get();
    }

    /**
     * <p>
     * Inicia esta instância de processo.
     * </p>
     *
     * @return A tarefa atual da instância depois da inicialização.
     */
    public TaskInstance start() {
        return start(getVariaveis());
    }

    /**
     * @deprecated Esse método deve ser renomeado pois possui um comportamente
     * implicito não evidente em comparação à outra versão sobrecarregada do
     * mesmo: "getPersistedDescription"
     */
    @Deprecated
    public TaskInstance start(VarInstanceMap<?> varInstanceMap) {
        getPersistedDescription(); // Força a geração da descrição
        return FlowEngine.start(this, varInstanceMap);
    }

    /**
     * <p>
     * Executa a próxima transição desta instância de processo.
     * </p>
     */
    public void executeTransition() {
        FlowEngine.executeTransition(this, null, null);
    }

    /**
     * <p>
     * Executa a transição especificada desta instância de processo.
     * </p>
     *
     * @param transitionName a transição especificada.
     */
    public void executeTransition(String transitionName) {
        FlowEngine.executeTransition(this, transitionName, null);
    }

    /**
     * <p>
     * Executa a transição especificada desta instância de processo passando as
     * variáveis fornecidas.
     * </p>
     *
     * @param transitionName a transição especificada.
     * @param param as variáveis fornecidas.
     */
    public void executeTransition(String transitionName, VarInstanceMap<?> param) {
        FlowEngine.executeTransition(this, transitionName, param);
    }

    /**
     * <p>
     * Realiza a montagem necessária para execução da transição especificada a
     * partir da tarefa atual desta instância.
     * </p>
     *
     * @param transitionName a transição especificada.
     * @return a montagem resultante.
     */
    public TransitionCall prepareTransition(String transitionName) {
        return getCurrentTask().prepareTransition(transitionName);
    }

    final IEntityProcessInstance getInternalEntity() {
        if (entity == null) {
            if (codEntity != null) {
                IEntityProcessInstance newfromDB = getPersistenceService().retrieveProcessInstanceByCod(codEntity);
                if (newfromDB != null) {
                    if (!getProcessDefinition().getEntityProcessDefinition().equals(newfromDB.getProcessVersion().getProcessDefinition())) {
                        throw new SingularException(getProcessDefinition().getName() + " id=" + codEntity
                            + " se refere a definição de processo " + newfromDB.getProcessVersion().getProcessDefinition().getKey()
                            + " mas era esperado que referenciasse " + getProcessDefinition().getEntityProcessDefinition());

                    }
                    entity = newfromDB;
                }
            }
            if (entity == null) {
                throw new SingularException(
                    getClass().getName() + " is not binded to a new and neither to a existing database intance process entity.");
            }
        }
        return entity;
    }

    private TaskInstance getCurrentTaskOrException() {
        TaskInstance current = getCurrentTask();
        if (current == null) {
            throw new SingularException(createErrorMsg("Não há um task atual para essa instancia"));
        }
        return current;
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
     * <p>
     * Retorna o tarefa corrente desta instância de processo.
     * </p>
     *
     * @return a tarefa corrente.
     */
    public MTask<?> getEstado() {
        if (estadoAtual == null) {
            TaskInstance current = getCurrentTask();
            if (current != null) {
                estadoAtual = getProcessDefinition().getFlowMap().getTaskBybbreviation(current.getAbbreviation());
            } else if (isFinished()) {
                current = getLatestTask();
                if (current != null && current.isFinished()) {
                    estadoAtual = getProcessDefinition().getFlowMap().getTaskBybbreviation(current.getAbbreviation());
                } else {
                    throw new SingularException(createErrorMsg(
                        "incossitencia: o estado final está null, mas deveria ter um estado do tipo final por estar finalizado"));
                }
            } else {
                throw new SingularException(createErrorMsg("getEstado() não pode ser invocado para essa instância"));
            }
        }
        return estadoAtual;
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
     * <p>
     * Retorna o nome da tarefa atual desta instância de processo.
     * </p>
     *
     * @return o nome da tarefa atual; ou {@code null} caso não haja uma tarefa
     * atual.
     */
    public String getCurrentTaskName() {
        if (getEstado() != null) {
            return getEstado().getName();
        }
        TaskInstance tarefaAtual = getCurrentTask();
        if (tarefaAtual != null) {
            // Uma situação legada, que não existe mais no fluxo mapeado
            return tarefaAtual.getName();
        }
        return null;
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
    public final boolean canExecuteTask(MUser user) {
        if (getEstado() == null) {
            return false;
        }
        IEntityTaskType tt = getEstado().getTaskType();
        if (tt.isPeople() || tt.isWait()) {
            return (isAllocated(user.getCod()))
                || (getAccessStrategy() != null && getAccessStrategy().canExecute(this, user));

        }
        return false;
    }

    /**
     * <p>
     * Verifica de o usuário especificado pode visualizar a tarefa corrente
     * desta instância de processo.
     * </p>
     *
     * @param user o usuário especificado.
     * @return {@code true} caso o usuário possa visualizar a tarefa corrente;
     * {@code false} caso contrário.
     */
    public boolean canVisualize(MUser user) {
        MTask<?> tt = getLatestTask().getFlowTask();
        if (tt.isPeople() || tt.isWait()) {
            if (hasAllocatedUser() && isAllocated(user.getCod())) {
                return true;
            }

        }
        return getAccessStrategy() != null && getAccessStrategy().canVisualize(this, user);
    }

    /**
     * <p>
     * Retorna os códigos de usuários com direito de execução da tarefa corrente
     * desta instância de processo.
     * </p>
     *
     * @return os códigos de usuários com direitos de execução.
     */
    public Set<Integer> getFirstLevelUsersCodWithAccess() {
        return getAccessStrategy().getFirstLevelUsersCodWithAccess(this);
    }

    /**
     * <p>
     * Retorna os usuários com direito de execução da tarefa corrente desta
     * instância de processo.
     * </p>
     *
     * @return os usuários com direitos de execução.
     */
    public List<MUser> listAllocableUsers() {
        return getAccessStrategy().listAllocableUsers(this);
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
    private TaskAccessStrategy getAccessStrategy() {
        return getEstado().getAccessStrategy();
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
        entity = getPersistenceService().retrieveProcessInstanceByCod(codEntity);
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
    public final MUser getUserWithRole(String roleAbbreviation) {
        final IEntityRoleInstance entityRole = getEntity().getRoleUserByAbbreviation(roleAbbreviation);
        if (entityRole != null) {
            return entityRole.getUser();
        }
        return null;
    }

    /**
     * <p>
     * Recupera a lista de papeis da entidade persistente correspondente a esta
     * instância.
     * </p>
     *
     * @return os papeis.
     */
    // TODO Daniel deveria retornar um objeto que isolasse da persistência
    @Deprecated
    public final List<? extends IEntityRoleInstance> getUserRoles() {
        return getEntity().getRoles();
    }

    /**
     * <p>
     * Recupera a lista de papeis com a sigla especificada da entidade
     * persistente correspondente a esta instância.
     * </p>
     *
     * @param roleAbbreviation a sigla especificada.
     * @return os papeis.
     */
    public final IEntityRoleInstance getRoleUserByAbbreviation(String roleAbbreviation) {
        return getEntity().getRoleUserByAbbreviation(roleAbbreviation);
    }

    /**
     * <p>
     * Verifica se há papeis definidos.
     * </p>
     *
     * @return {@code true} caso haja pelo menos um papel definido;
     * {@code false} caso contrário.
     */
    public final boolean hasUserRoles() {
        return !getEntity().getRoles().isEmpty();
    }

    /**
     * <p>
     * Retorna o usuário que criou esta instância de processo.
     * </p>
     *
     * @return o usuário criador.
     */
    public final MUser getUserCreator() {
        return getInternalEntity().getUserCreator();
    }

    /**
     * <p>
     * Altera a descrição desta instância de processo.
     * </p>
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
     * <p>
     * Persiste esta instância de processo.
     * </p>
     *
     * @param <K> o tipo da entidade desta instância.
     * @return a entidade persistida.
     */
    public final <K extends IEntityProcessInstance> K saveEntity() {
        setInternalEntity(getPersistenceService().saveProcessInstance(getInternalEntity()));
        return (K) getInternalEntity();
    }

    /**
     * <p>
     * Realiza uma transição manual da tarefa atual para a tarefa especificada.
     * </p>
     *
     * @param task a tarefa especificada.
     */
    public final void forceStateUpdate(MTask<?> task) {
        final TaskInstance tarefaOrigem = getLatestTask();
        List<MUser> pessoasAnteriores = getResponsaveisDiretos();
        final Date agora = new Date();
        TaskInstance tarefaNova = updateState(tarefaOrigem, null, task, agora);
        if (tarefaOrigem != null) {
            tarefaOrigem.log("Alteração Manual de Estado", "de '" + tarefaOrigem.getName() + "' para '" + task.getName() + "'",
                null, Flow.getUserIfAvailable(), agora).sendEmail(pessoasAnteriores);
        }
        FlowEngine.initTask(this, task, tarefaNova);
        ExecutionContext execucaoMTask = new ExecutionContext(this, tarefaNova, null);
        task.notifyTaskStart(getLatestTask(task), execucaoMTask);
        if (task.isImmediateExecution()) {
            executeTransition();
        }
    }

    /**
     * <p>
     * Realiza uma transição da tarefa de origiem para a tarefa alvo
     * especificadas.
     * </p>
     *
     * @param tarefaOrigem a tarefa de origem.
     * @param transicaoOrigem a transição disparada.
     * @param task a tarefa alvo.
     * @param agora o momento da transição.
     * @return a tarefa corrente depois da transição.
     */
    protected final TaskInstance updateState(TaskInstance tarefaOrigem, MTransition transicaoOrigem, MTask<?> task, Date agora) {
        synchronized (this) {
            if (tarefaOrigem != null) {
                tarefaOrigem.endLastAllocation();
                String transitionName = null;
                if (transicaoOrigem != null) {
                    transitionName = transicaoOrigem.getAbbreviation();
                }
                getPersistenceService().completeTask(tarefaOrigem.getEntityTaskInstance(), transitionName, Flow.getUserIfAvailable());
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
    public final Date getBeginDate() {
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

    private TaskInstance getTaskInstance(final IEntityTaskInstance tarefa) {
        return tarefa != null ? new TaskInstance(this, tarefa) : null;
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
     * <p>
     * Retorna os responsáveis diretos.
     * </p>
     *
     * @return os responsáveis diretos.
     */
    public List<MUser> getResponsaveisDiretos() {
        TaskInstance tarefa = getCurrentTask();
        if (tarefa != null) {
            return tarefa.getDirectlyResponsibles();
        }
        return Collections.emptyList();
    }

    private void addUserRole(MProcessRole mProcessRole, MUser user) {
        if (getUserWithRole(mProcessRole.getAbbreviation()) == null) {
            getPersistenceService().setInstanceUserRole(getEntity(),
                getProcessDefinition().getEntityProcessDefinition().getRole(mProcessRole.getAbbreviation()), user);
        }
    }

    /**
     * <p>
     * Atribui ou substitui o usuário para o papel especificado.
     * </p>
     *
     * @param roleAbbreviation o papel especificado.
     * @param newUser o novo usuário atribuído ao papel.
     */
    public final void addOrReplaceUserRole(final String roleAbbreviation, MUser newUser) {
        MProcessRole mProcessRole = getProcessDefinition().getFlowMap().getRoleWithAbbreviation(roleAbbreviation);
        if (mProcessRole == null) {
            throw new SingularFlowException("Não foi possível encontrar a role: " + roleAbbreviation);
        }
        MUser previousUser = getUserWithRole(mProcessRole.getAbbreviation());

        if (previousUser == null) {
            if (newUser != null) {
                addUserRole(mProcessRole, newUser);
                getProcessDefinition().getFlowMap().notifyRoleChange(this, mProcessRole, null, newUser);

                final TaskInstance latestTask = getLatestTask();
                if (latestTask != null) {
                    latestTask.log("Papel definido", String.format("%s: %s", mProcessRole.getName(), newUser.getSimpleName()));
                }
            }
        } else if (newUser == null || !previousUser.equals(newUser)) {
            IEntityProcessInstance entityTmp = getEntity();
            getPersistenceService().removeInstanceUserRole(entityTmp, entityTmp.getRoleUserByAbbreviation(mProcessRole.getAbbreviation()));
            if (newUser != null) {
                addUserRole(mProcessRole, newUser);
            }

            getProcessDefinition().getFlowMap().notifyRoleChange(this, mProcessRole, previousUser, newUser);
            final TaskInstance latestTask = getLatestTask();
            if (latestTask != null) {
                if (newUser != null) {
                    latestTask.log("Papel alterado", String.format("%s: %s", mProcessRole.getName(), newUser.getSimpleName()));
                } else {
                    latestTask.log("Papel removido", mProcessRole.getName());
                }
            }
        }
    }

    /**
     * <p>
     * Configura o valor variável especificada.
     * </p>
     *
     * @param nomeVariavel o nome da variável especificada.
     * @param valor o valor a ser configurado.
     */
    public void setVariavel(String nomeVariavel, Object valor) {
        getVariaveis().setValor(nomeVariavel, valor);
    }

    /**
     * <p>
     * Retorna o valor da variável do tipo {@link Date} especificada.
     * </p>
     *
     * @param nomeVariavel o nome da variável especificada.
     * @return o valor da variável.
     */
    public final Date getValorVariavelData(String nomeVariavel) {
        return getVariaveis().getValorData(nomeVariavel);
    }

    /**
     * <p>
     * Retorna o valor da variável do tipo {@link Boolean} especificada.
     * </p>
     *
     * @param nomeVariavel o nome da variável especificada.
     * @return o valor da variável.
     */
    public final Boolean getValorVariavelBoolean(String nomeVariavel) {
        return getVariaveis().getValorBoolean(nomeVariavel);
    }

    /**
     * <p>
     * Retorna o valor da variável do tipo {@link String} especificada.
     * </p>
     *
     * @param nomeVariavel o nome da variável especificada.
     * @return o valor da variável.
     */
    public final String getValorVariavelString(String nomeVariavel) {
        return getVariaveis().getValorString(nomeVariavel);
    }

    /**
     * <p>
     * Retorna o valor da variável do tipo {@link Integer} especificada.
     * </p>
     *
     * @param nomeVariavel o nome da variável especificada.
     * @return o valor da variável.
     */
    public final Integer getValorVariavelInteger(String nomeVariavel) {
        return getVariaveis().getValorInteger(nomeVariavel);
    }

    /**
     * <p>
     * Retorna o valor da variável especificada.
     * </p>
     *
     * @param <T> o tipo da variável especificada.
     * @param nomeVariavel o nome da variável especificada.
     * @return o valor da variável.
     */
    public final <T> T getValorVariavel(String nomeVariavel) {
        return getVariaveis().getValor(nomeVariavel);
    }

    /**
     * <p>
     * Retorna o mapa das variáveis desta instância de processo.
     * </p>
     *
     * @return o mapa das variáveis.
     */
    public final VarInstanceMap<?> getVariaveis() {
        if (variables == null) {
            variables = new VarInstanceTableProcess(this);
        }
        return variables;
    }

    /**
     * <p>
     * Valida esta instância de processo.
     * </p>
     *
     * @throws SingularFlowException caso a validação falhe.
     */
    protected void validadeStart() {
        if (variables == null && !getProcessDefinition().getVariables().hasRequired()) {
            return;
        }

        ValidationResult result = getVariaveis().validar();
        if (result.hasErros()) {
            throw new SingularFlowException(createErrorMsg("Erro ao iniciar processo '" + getProcessName() + "': " + result));
        }
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
        return getEntity().getTasks().stream().anyMatch(tarefa -> tarefa.isActive() && tarefa.getAllocatedUser() != null);
    }

    /**
     * <p>
     * Retorna os usuários alocados nas tarefas ativas
     * </p>
     * 
     * @return a lista de usuários (<i>null safe</i>).
     */
    public Set<MUser> getAllocatedUsers() {
        return getEntity().getTasks().stream().filter(tarefa -> tarefa.isActive() && tarefa.getAllocatedUser() != null).map(tarefa -> tarefa.getAllocatedUser()).collect(Collectors.toSet());
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
        return getEntity().getTasks().stream().anyMatch(tarefa -> tarefa.isActive() && tarefa.getAllocatedUser() != null
            && tarefa.getAllocatedUser().getCod().equals(codPessoa));
    }

    /**
     * <p>
     * Retorna a lista de todas as tarefas. Ordena da mais antiga para a mais
     * nova.
     * </p>
     *
     * @return a lista de tarefas (<i>null safe</i>).
     */
    public List<TaskInstance> getTasks() {
        IEntityProcessInstance demanda = getEntity();
        return demanda.getTasks().stream().map(this::getTaskInstance).collect(Collectors.toList());
    }

    /**
     * <p>
     * Retorna a mais nova tarefa que atende a condição informada.
     * </p>
     *
     * @param condicao a condição informada.
     * @return a tarefa; ou {@code null} caso não encontre a tarefa.
     */
    public TaskInstance getLatestTask(Predicate<TaskInstance> condicao) {
        List<? extends IEntityTaskInstance> lista = getEntity().getTasks();
        for (int i = lista.size() - 1; i != -1; i--) {
            TaskInstance task = getTaskInstance(lista.get(i));
            if (condicao.test(task)) {
                return task;
            }
        }
        return null;
    }

    /**
     * <p>
     * Retorna a tarefa atual.
     * </p>
     *
     * @return a tarefa atual.
     */
    public TaskInstance getCurrentTask() {
        return getLatestTask(t -> t.isActive());
    }

    /**
     * <p>
     * Retorna a mais nova tarefa encerrada ou ativa.
     * </p>
     *
     * @return a mais nova tarefa encerrada ou ativa.
     */
    public TaskInstance getLatestTask() {
        return getLatestTask(t -> true);
    }

    private TaskInstance getLatestTask(String abbreviation) {
        return getLatestTask(t -> t.getAbbreviation().equalsIgnoreCase(abbreviation));
    }

    /**
     * <p>
     * Encontra a mais nova tarefa encerrada ou ativa com a sigla da referência.
     * </p>
     *
     * @param taskRef a referência.
     * @return a tarefa; ou {@code null} caso não encotre a tarefa.
     */
    public TaskInstance getLatestTask(ITaskDefinition taskRef) {
        return getLatestTask(taskRef.getKey());
    }

    /**
     * <p>
     * Encontra a mais nova tarefa encerrada ou ativa do tipo informado.
     * </p>
     *
     * @param tipo o tipo informado.
     * @return a tarefa; ou {@code null} caso não encotre a tarefa.
     */
    public TaskInstance getLatestTask(MTask<?> tipo) {
        return getLatestTask(tipo.getAbbreviation());
    }

    private TaskInstance getFinishedTask(String abbreviation) {
        return getLatestTask(t -> t.isFinished() && t.getAbbreviation().equalsIgnoreCase(abbreviation));
    }

    /**
     * <p>
     * Encontra a mais nova tarefa encerrada e com a mesma sigla da referência.
     * </p>
     *
     * @param taskRef a referência.
     * @return a tarefa; ou {@code null} caso não encotre a tarefa.
     */
    public TaskInstance getFinishedTask(ITaskDefinition taskRef) {
        return getFinishedTask(taskRef.getKey());
    }

    /**
     * <p>
     * Encontra a mais nova tarefa encerrada e com a mesma sigla do tipo.
     * </p>
     *
     * @param tipo o tipo.
     * @return a tarefa; ou {@code null} caso não encotre a tarefa.
     */
    public TaskInstance getFinishedTask(MTask<?> tipo) {
        return getFinishedTask(tipo.getAbbreviation());
    }

    protected IPersistenceService<IEntityCategory, IEntityProcessDefinition, IEntityProcessVersion, IEntityProcessInstance, IEntityTaskInstance, IEntityTaskDefinition, IEntityTaskVersion, IEntityVariableInstance, IEntityRoleDefinition, IEntityRoleInstance> getPersistenceService() {
        return getProcessDefinition().getPersistenceService();
    }

    /**
     * <p>
     * Configura o contexto de execução.
     * </p>
     *
     * @param execucaoTask o novo contexto de execução.
     */
    final void setExecutionContext(ExecutionContext execucaoTask) {
        if (this.executionContext != null && execucaoTask != null) {
            throw new SingularFlowException(createErrorMsg("A instancia já está com um tarefa em processo de execução"));
        }
        this.executionContext = execucaoTask;
    }

}
