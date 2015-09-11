package br.net.mirante.singular.persistence.entity;

import br.net.mirante.singular.flow.core.MUser;
import br.net.mirante.singular.flow.core.entity.IEntityExecutionVariable;
import br.net.mirante.singular.flow.core.entity.IEntityProcessInstance;
import br.net.mirante.singular.flow.core.entity.IEntityTask;
import br.net.mirante.singular.flow.core.entity.IEntityTaskInstanceHistory;
import br.net.mirante.singular.flow.core.entity.IEntityTaskInstance;
import br.net.mirante.singular.flow.core.entity.IEntityTaskTransition;

import javax.persistence.*;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the TB_INSTANCIA_TAREFA database table.
 * 
 */
@Entity
@Table(name="TB_INSTANCIA_TAREFA")
@NamedQuery(name="TaskInstance.findAll", query="SELECT i FROM TaskInstance i")
public class TaskInstance implements IEntityTaskInstance {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="CO_INSTANCIA_TAREFA")
	private Long cod;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA_ALVO_FIM")
	private Date dataAlvoFim;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA_ALVO_SUSPENSAO")
	private Date dataAlvoSuspensao;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DT_ESPERADA_FIM")
	private Date dataEsperadaFim;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DT_FIM")
	private Date dataFim;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DT_INICIO")
	private Date dataInicio;

	@Column(name="SE_SUSPENSA")
	private Boolean suspensa;

	//bi-directional many-to-one association to ProcessInstance
	@OneToMany(mappedBy="instanciaTarefaPai")
	private List<ProcessInstance> instanciasTarefaFilhas;

	//bi-directional many-to-one association to ProcessInstance
	@OneToMany(mappedBy="instanciaTarefa")
	private List<ProcessInstance> instanciasProcesso;

	//uni-directional many-to-one association to Actor
	@ManyToOne
	@JoinColumn(name="CO_ATOR_ALOCADO")
	private Actor actorAlocado;

	//uni-directional many-to-one association to Actor
	@ManyToOne
	@JoinColumn(name="CO_ATOR_CONCLUSAO")
	private Actor actorConclusao;

	//bi-directional many-to-one association to ProcessInstance
	@ManyToOne
	@JoinColumn(name="CO_INSTANCIA_PROCESSO")
	private ProcessInstance processInstance;

	//uni-directional many-to-one association to Task
	@ManyToOne
	@JoinColumn(name="CO_TAREFA")
	private Task task;

	//uni-directional many-to-one association to Transicao
	@ManyToOne
	@JoinColumn(name="CO_TRANSICAO_EXECUTADA")
	private Transicao transicao;

	public TaskInstance() {
	}

	public Long getCod() {
		return this.cod;
	}

	public void setCod(Long cod) {
		this.cod = cod;
	}

	public Date getDataAlvoFim() {
		return this.dataAlvoFim;
	}

	public void setDataAlvoFim(Date dataAlvoFim) {
		this.dataAlvoFim = dataAlvoFim;
	}

	public Date getDataAlvoSuspensao() {
		return this.dataAlvoSuspensao;
	}

	public void setDataAlvoSuspensao(Date dataAlvoSuspensao) {
		this.dataAlvoSuspensao = dataAlvoSuspensao;
	}

	public Date getDataEsperadaFim() {
		return this.dataEsperadaFim;
	}

	public void setDataEsperadaFim(Date dataEsperadaFim) {
		this.dataEsperadaFim = dataEsperadaFim;
	}

	public Date getDataFim() {
		return this.dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public Date getDataInicio() {
		return this.dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Boolean getSuspensa() {
		return this.suspensa;
	}

	public void setSuspensa(Boolean suspensa) {
		this.suspensa = suspensa;
	}

	public List<ProcessInstance> getInstanciasTarefaFilhas() {
		return this.instanciasTarefaFilhas;
	}

	public void setInstanciasTarefaFilhas(List<ProcessInstance> instanciasTarefaFilhas) {
		this.instanciasTarefaFilhas = instanciasTarefaFilhas;
	}

	public List<ProcessInstance> getInstanciasProcesso() {
		return this.instanciasProcesso;
	}

	public void setInstanciasProcesso(List<ProcessInstance> instanciasProcesso) {
		this.instanciasProcesso = instanciasProcesso;
	}


	public Actor getActorAlocado() {
		return this.actorAlocado;
	}

	public void setActorAlocado(Actor actorAlocado) {
		this.actorAlocado = actorAlocado;
	}

	public Actor getActorConclusao() {
		return this.actorConclusao;
	}

	public void setActorConclusao(Actor actorConclusao) {
		this.actorConclusao = actorConclusao;
	}



	public void setProcessInstance(ProcessInstance processInstance) {
		this.processInstance = processInstance;
	}



	public void setTask(Task task) {
		this.task = task;
	}

	public Transicao getTransicao() {
		return this.transicao;
	}

	public void setTransicao(Transicao transicao) {
		this.transicao = transicao;
	}

	@Override
	public IEntityProcessInstance getProcessInstance() {
		return null;
	}

	@Override
	public IEntityTask getTask() {
		return null;
	}

	@Override
	public Date getBeginDate() {
		return null;
	}

	@Override
	public Date getEndDate() {
		return null;
	}

	@Override
	public Date getTargetEndDate() {
		return null;
	}

	@Override
	public void setTargetEndDate(Date targetEndDate) {

	}

	@Override
	public MUser getAllocatedUser() {
		return null;
	}

	@Override
	public MUser getResponsibleUser() {
		return null;
	}

	@Override
	public IEntityTaskTransition getExecutedTransition() {
		return null;
	}

	@Override
	public List<? extends IEntityExecutionVariable> getInputVariables() {
		return null;
	}

	@Override
	public List<? extends IEntityExecutionVariable> getOutputVariables() {
		return null;
	}

	@Override
	public List<? extends IEntityTaskInstanceHistory> getTaskHistoric() {
		return null;
	}

	@Override
	public List<? extends IEntityProcessInstance> getChildProcesses() {
		return null;
	}
}