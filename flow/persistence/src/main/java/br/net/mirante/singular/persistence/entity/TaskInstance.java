package br.net.mirante.singular.persistence.entity;

import br.net.mirante.singular.flow.core.entity.IEntityExecutionVariable;
import br.net.mirante.singular.flow.core.entity.IEntityTaskInstance;
import br.net.mirante.singular.flow.core.entity.IEntityTaskInstanceHistory;
import br.net.mirante.singular.persistence.util.Constants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the TB_INSTANCIA_TAREFA database table.
 * 
 */
@Entity
@Table(name="TB_INSTANCIA_TAREFA", schema = Constants.SCHEMA)
public class TaskInstance implements IEntityTaskInstance {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="CO_INSTANCIA_TAREFA")
	private Long cod;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA_ALVO_FIM")
	private Date targetEndDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA_ALVO_SUSPENSAO")
	private Date suspensionTargetDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DT_ESPERADA_FIM")
	private Date expectedEndDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DT_FIM")
	private Date endDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DT_INICIO")
	private Date beginDate;

	@Column(name="SE_SUSPENSA")
	private Boolean suspended;

	@OneToMany(mappedBy="parentTask")
	private List<ProcessInstance> childProcesses;

	@ManyToOne
	@JoinColumn(name="CO_ATOR_ALOCADO")
	private Actor allocatedUser;

	@ManyToOne
	@JoinColumn(name="CO_ATOR_CONCLUSAO")
	private Actor responsibleUser;

	@ManyToOne
	@JoinColumn(name="CO_INSTANCIA_PROCESSO")
	private ProcessInstance processInstance;

	@ManyToOne
	@JoinColumn(name="CO_TAREFA")
	private Task task;

	@ManyToOne
	@JoinColumn(name="CO_TRANSICAO_EXECUTADA")
	private Transition executedTransition;

	public TaskInstance() {
	}

	public Long getCod() {
		return this.cod;
	}

	public void setCod(Long cod) {
		this.cod = cod;
	}

	@Override
	public Date getTargetEndDate() {
		return targetEndDate;
	}

	@Override
	public void setTargetEndDate(Date targetEndDate) {
		this.targetEndDate = targetEndDate;
	}

	public Date getSuspensionTargetDate() {
		return suspensionTargetDate;
	}

	public void setSuspensionTargetDate(Date suspensionTargetDate) {
		this.suspensionTargetDate = suspensionTargetDate;
	}

	public Date getExpectedEndDate() {
		return expectedEndDate;
	}

	public void setExpectedEndDate(Date expectedEndDate) {
		this.expectedEndDate = expectedEndDate;
	}

	@Override
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Override
	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Boolean getSuspended() {
		return suspended;
	}

	public void setSuspended(Boolean suspended) {
		this.suspended = suspended;
	}

	@Override
	public List<ProcessInstance> getChildProcesses() {
		return childProcesses;
	}

	public void setChildProcesses(List<ProcessInstance> childProcesses) {
		this.childProcesses = childProcesses;
	}

	@Override
	public Actor getAllocatedUser() {
		return allocatedUser;
	}

	public void setAllocatedUser(Actor allocatedUser) {
		this.allocatedUser = allocatedUser;
	}

	@Override
	public Actor getResponsibleUser() {
		return responsibleUser;
	}

	public void setResponsibleUser(Actor responsibleUser) {
		this.responsibleUser = responsibleUser;
	}

	@Override
	public ProcessInstance getProcessInstance() {
		return processInstance;
	}

	public void setProcessInstance(ProcessInstance processInstance) {
		this.processInstance = processInstance;
	}

	@Override
	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	@Override
	public Transition getExecutedTransition() {
		return executedTransition;
	}

	public void setExecutedTransition(Transition executedTransition) {
		this.executedTransition = executedTransition;
	}

	@Override
	public List<? extends IEntityExecutionVariable> getInputVariables() {
		throw new UnsupportedOperationException("Método não implementado");
	}

	@Override
	public List<? extends IEntityExecutionVariable> getOutputVariables() {
		throw new UnsupportedOperationException("Método não implementado");
	}

	@Override
	public List<? extends IEntityTaskInstanceHistory> getTaskHistoric() {
		throw new UnsupportedOperationException("Método não implementado");
	}

}