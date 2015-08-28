package br.net.mirante.singular.flow.core;

import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import br.net.mirante.singular.flow.core.entity.IEntityCategory;
import br.net.mirante.singular.flow.core.entity.IEntityProcess;
import br.net.mirante.singular.flow.core.entity.IEntityProcessInstance;
import br.net.mirante.singular.flow.core.entity.IEntityProcessRole;
import br.net.mirante.singular.flow.core.entity.IEntityRole;
import br.net.mirante.singular.flow.core.entity.IEntityTaskDefinition;
import br.net.mirante.singular.flow.core.entity.IEntityTaskInstance;
import br.net.mirante.singular.flow.core.entity.IEntityVariableInstance;
import br.net.mirante.singular.flow.core.service.IPersistenceService;
import br.net.mirante.singular.flow.util.vars.VarInstanceMap;
import br.net.mirante.singular.flow.util.view.Lnk;

import com.google.common.collect.ImmutableList;

public class TaskInstance {

    private final IEntityTaskInstance entityTask;

    private ProcessInstance processInstance;

    private transient MTask<?> tipo;

    TaskInstance(ProcessInstance processInstance, IEntityTaskInstance task) {
        if (!processInstance.getEntity().equals(task.getDemanda())) {
            throw new SingularFlowException(processInstance.createErrorMsg("O objeto IDadosTarefa " + task + " não é filho do objeto IDadosInstancia em questão"));
        }
        this.processInstance = processInstance;
        this.entityTask = task;
    }

    TaskInstance(IEntityTaskInstance task) {
        this.entityTask = task;
    }

    @SuppressWarnings("unchecked")
    public <X extends ProcessInstance> X getProcessInstance() {
        if (processInstance == null) {
            processInstance = MBPM.getMbpmBean().getProcessInstance(entityTask.getDemanda());
        }
        return (X) processInstance;
    }

    public MTask<?> getTipo() {
        if (tipo == null) {
            tipo = getProcessInstance().getDefinicao().getFlowMap().getTaskWithAbbreviation(entityTask.getSituacao().getSigla());
        }
        return tipo;
    }

    public Integer getId() {
        return entityTask.getCod();
    }

    public String getFullId() {
        return MBPM.generateID(this);
    }

    public Lnk getHrefPadrao() {
        return MBPM.getDefaultHrefFor(this);
    }

    public MUser getPessoaAlocada() {
        return entityTask.getPessoaAlocada();
    }

    public MUser getAutorFim() {
        return entityTask.getAutorFim();
    }

    public Date getDataAlvoFim() {
        return entityTask.getDataAlvoFim();
    }

    @SuppressWarnings("unchecked")
    public final <X extends IEntityTaskInstance> X getEntityTaskInstance() {
        return (X) entityTask;
    }

    public String getNome() {
        if (getTipo() != null) {
            return getTipo().getName();
        }
        return entityTask.getSituacao().getNome();
    }

    public String getNomeProcesso() {
        return getProcessInstance().getNomeProcesso();
    }

    public String getNomeTarefa() {
        return getNome();
    }

    public String getDescricao() {
        return getProcessInstance().getDescricao();
    }

    public Date getDataInicio() {
        return entityTask.getDataInicio();
    }

    public Date getDataFim() {
        return entityTask.getDataFim();
    }

    public boolean isFim() {
        if (getTipo() != null) {
            return getTipo().isEnd();
        }
        return entityTask.getSituacao().isFim();
    }

    public boolean isPessoa() {
        if (getTipo() != null) {
            return getTipo().isPeople();
        }
        return entityTask.getSituacao().isPessoa();
    }

    public boolean isWait() {
        if (getTipo() != null) {
            return getTipo().isWait();
        }
        return entityTask.getSituacao().isWait();
    }

    public TransitionCall prepareTransition(String transitionName) {
        return new TransitionCallImpl(getTransition(transitionName));
    }

    public TransitionRef getTransition(String transitionName) {
        return new TransitionRef(this, getTipo().getTransicaoOrException(transitionName));
    }

    public final void suspend(Date targetDate, String cause) {
        if (Objects.equals(entityTask.getDataAlvoSuspensao(), targetDate) && cause == null) {
            return;
        }
        entityTask.setDataAlvoSuspensao(targetDate);
        getPersistenceService().updateTask(entityTask);

        String sData = DateFormat.getDateInstance(DateFormat.MEDIUM).format(targetDate);
        String log = (targetDate == null ? "Retirado de suspensão" : "Suspenso até " + sData);
        if (!StringUtils.isBlank(cause)) {
            log += "\nExplicação=" + cause.trim();
        }

        log("Colocado em Espera", log);

        notificarUpdateEstado();
    }

    public void relocateTask(MUser author, MUser user, boolean notify, String relocationCause) {
        if (user != null && !isPessoa()) {
            throw new SingularFlowException(getProcessInstance().createErrorMsg("A tarefa '" + getNome() + "' não pode ser realocada, pois não é do tipo pessoa"));
        }
        MUser pessoaAlocadaAntes = getPessoaAlocada();
        if (Objects.equals(user, pessoaAlocadaAntes)) {
            return;
        }

        getEntityTaskInstance().setPessoaAlocada(user);
        getEntityTaskInstance().setDataAlvoSuspensao(null);

        getPersistenceService().updateTask(getEntityTaskInstance());

        relocationCause = StringUtils.trimToNull(relocationCause);

        String acao = (user == null) ? "Desalocação" : "Alocação";
        if (author == null) {
            log(acao + " Automática", relocationCause, user, null, new Date());
        } else {
            log(acao, relocationCause, user, author, new Date());
        }

        if (notify) {
            MBPM.getNotifiers().notifyUserTaskRelocation(this, author, pessoaAlocadaAntes, user, pessoaAlocadaAntes);
            MBPM.getNotifiers().notifyUserTaskAllocation(this, author, user, user, pessoaAlocadaAntes, relocationCause);
        }

        notificarUpdateEstado();
    }

    public void setDataAlvoFim(Date dataAlvo) {
        getEntityTaskInstance().setDataAlvoFim(dataAlvo);
        getPersistenceService().updateTask(getEntityTaskInstance());
    }

    public void createSubTask(String historyType, ProcessInstance childProcessInstance) {
        getPersistenceService().setParentTask(childProcessInstance.getEntity(), entityTask);

        if (historyType != null) {
            log(historyType, childProcessInstance.getEntity().getDescricao(), childProcessInstance.getTarefaAtual().getPessoaAlocada())
                    .sendEmail();
        }
        notificarUpdateEstado();
    }

    private void notificarUpdateEstado() {
        MBPM.getMbpmBean().notifyStateUpdate(getProcessInstance());
    }

    public TaskHistoricLog log(String tipoHistorico, String detalhamento) {
        return log(tipoHistorico, detalhamento, null, MBPM.getUserIfAvailable(), null);
    }

    public TaskHistoricLog log(String tipoHistorico, String detalhamento, MUser alocada) {
        return log(tipoHistorico, detalhamento, alocada, MBPM.getUserIfAvailable(), null);
    }

    public TaskHistoricLog log(String tipoHistorico, String detalhamento, MUser alocada, MUser autor, Date dataHora) {
        return log(tipoHistorico, detalhamento, alocada, autor, dataHora, null);
    }

    public TaskHistoricLog log(String tipoHistorico, String detalhamento, MUser alocada, MUser autor, Date dataHora,
            IEntityProcessInstance demandaFilha) {
        return getPersistenceService().saveTaskHistoricLog(entityTask, tipoHistorico, detalhamento, alocada, autor, dataHora, demandaFilha);
    }

    private IPersistenceService<IEntityCategory, IEntityProcess, IEntityProcessInstance, IEntityTaskInstance, IEntityTaskDefinition, IEntityVariableInstance, IEntityProcessRole, IEntityRole> getPersistenceService() {
        return getProcessInstance().getDefinicao().getPersistenceService();
    }

    public StringBuilder getDescricaoExtendida(boolean adicionarAlocado) {
        StringBuilder sb = new StringBuilder(250);
        sb.append(getProcessInstance().getNomeProcesso()).append(" - ").append(getNome());
        String descricao = getProcessInstance().getDescricao();
        if (descricao != null) {
            sb.append(" - ").append(descricao);
        }
        if (adicionarAlocado) {
            MUser p = getPessoaAlocada();
            if (p != null) {
                sb.append(" (").append(p.getNomeGuerra()).append(")");
            }
        }
        return sb;

    }

    @SuppressWarnings("unchecked")
    public List<MUser> getDirectlyResponsible() {
        if (getPessoaAlocada() != null) {
            return ImmutableList.of(getPessoaAlocada());
        }
        if (getTipo() != null && (getTipo().isPeople() || (getTipo().isWait() && getTipo().getAccessStrategy() != null))) {
            Set<Integer> codPessoas = getFirstLevelUsersCodWithAccess();
            return (List<MUser>) getPersistenceService().retrieveUsersByCod(codPessoas);
        }
        return Collections.emptyList();
    }

    private Set<Integer> getFirstLevelUsersCodWithAccess() {
        Objects.requireNonNull(getTipo(), "Task com a sigla " + entityTask.getSituacao().getSigla() + " não encontrada na definição "
                + getProcessInstance().getDefinicao().getName());
        Objects.requireNonNull(getTipo().getAccessStrategy(),
                "Estratégia de acesso da task " + entityTask.getSituacao().getSigla() + " não foi definida");
        return getTipo().getAccessStrategy().getFirstLevelUsersCodWithAccess(getProcessInstance());
    }

    public void executarTransicao() {
        EngineProcessamentoMBPM.executeTransition(this, null, null);
    }

    public void executarTransicao(String destino) {
        EngineProcessamentoMBPM.executeTransition(this, destino, null);
    }

    public void executarTransicao(String destino, VarInstanceMap<?> param) {
        EngineProcessamentoMBPM.executeTransition(this, destino, param);
    }
}
