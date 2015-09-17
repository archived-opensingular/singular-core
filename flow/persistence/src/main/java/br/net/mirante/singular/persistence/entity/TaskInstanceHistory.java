package br.net.mirante.singular.persistence.entity;

import br.net.mirante.singular.flow.core.MUser;
import br.net.mirante.singular.flow.core.entity.IEntityTaskInstanceHistory;
import br.net.mirante.singular.flow.core.entity.IEntityTaskHistoricType;
import br.net.mirante.singular.flow.core.entity.IEntityTaskInstance;
import br.net.mirante.singular.persistence.util.Constants;

import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the TB_HISTORICO_INSTANCIA_TAREFA database table.
 * 
 */
@Entity
@Table(name="TB_HISTORICO_INSTANCIA_TAREFA", schema = Constants.SCHEMA)
public class TaskInstanceHistory implements IEntityTaskInstanceHistory {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="CO_HISTORICO_ALOCACAO")
	private Long cod;

	@Column(name="DS_COMPLEMENTO")
	private String description;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DT_FIM_ALOCACAO")
	private Date endDateAllocation;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DT_INICIO_ALOCACAO")
	private Date beginDateAllocation;

	//uni-directional many-to-one association to Actor
	@ManyToOne
	@JoinColumn(name="CO_ATOR_ALOCADO")
	private Actor allocatedUser;

	//uni-directional many-to-one association to Actor
	@ManyToOne
	@JoinColumn(name="CO_ATOR_ALOCADOR")
	private Actor allocatorUser;

	//uni-directional many-to-one association to TaskInstance
	@ManyToOne
	@JoinColumn(name="CO_INSTANCIA_TAREFA")
	private TaskInstance taskInstance;

	//uni-directional many-to-one association to TaskHistoryType
	@ManyToOne
	@JoinColumn(name="CO_TIPO_HISTORICO_TAREFA")
	private TaskHistoryType taskHistoryType;

	public TaskInstanceHistory() {
	}

	public Long getCod() {
		return this.cod;
	}

	public void setCod(Long cod) {
		this.cod = cod;
	}

	@Override
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public Date getEndDateAllocation() {
		return endDateAllocation;
	}

	public void setEndDateAllocation(Date endDateAllocation) {
		this.endDateAllocation = endDateAllocation;
	}

	@Override
	public Date getBeginDateAllocation() {
		return beginDateAllocation;
	}

	public void setBeginDateAllocation(Date beginDateAllocation) {
		this.beginDateAllocation = beginDateAllocation;
	}

	public Actor getAllocatedUser() {
		return allocatedUser;
	}

	public void setAllocatedUser(Actor allocatedUser) {
		this.allocatedUser = allocatedUser;
	}

	@Override
	public Actor getAllocatorUser() {
		return allocatorUser;
	}

	public void setAllocatorUser(Actor allocatorUser) {
		this.allocatorUser = allocatorUser;
	}

	@Override
	public TaskInstance getTaskInstance() {
		return taskInstance;
	}

	public void setTaskInstance(TaskInstance taskInstance) {
		this.taskInstance = taskInstance;
	}

	public TaskHistoryType getTaskHistoryType() {
		return taskHistoryType;
	}

	public void setTaskHistoryType(TaskHistoryType taskHistoryType) {
		this.taskHistoryType = taskHistoryType;
	}

	@Override
	public IEntityTaskHistoricType getType() {
		throw new UnsupportedOperationException("Método não implementado");
	}

}