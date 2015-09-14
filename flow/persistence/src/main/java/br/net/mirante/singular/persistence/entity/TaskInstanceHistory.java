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
	private String complemento;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DT_FIM_ALOCACAO")
	private Date dataFimAlocacao;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DT_INICIO_ALOCACAO")
	private Date dataInicioAlocacao;

	//uni-directional many-to-one association to Actor
	@ManyToOne
	@JoinColumn(name="CO_ATOR_ALOCADO")
	private Actor actorAlocado;

	//uni-directional many-to-one association to Actor
	@ManyToOne
	@JoinColumn(name="CO_ATOR_ALOCADOR")
	private Actor actorAlocador;

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

	public String getComplemento() {
		return this.complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	public Date getDataFimAlocacao() {
		return this.dataFimAlocacao;
	}

	public void setDataFimAlocacao(Date dataFimAlocacao) {
		this.dataFimAlocacao = dataFimAlocacao;
	}

	public Date getDataInicioAlocacao() {
		return this.dataInicioAlocacao;
	}

	public void setDataInicioAlocacao(Date dataInicioAlocacao) {
		this.dataInicioAlocacao = dataInicioAlocacao;
	}

	public Actor getActorAlocado() {
		return this.actorAlocado;
	}

	public void setActorAlocado(Actor actorAlocado) {
		this.actorAlocado = actorAlocado;
	}

	public Actor getActorAlocador() {
		return this.actorAlocador;
	}

	public void setActorAlocador(Actor actorAlocador) {
		this.actorAlocador = actorAlocador;
	}



	public void setTaskInstance(TaskInstance taskInstance) {
		this.taskInstance = taskInstance;
	}

	public TaskHistoryType getTaskHistoryType() {
		return this.taskHistoryType;
	}

	public void setTaskHistoryType(TaskHistoryType taskHistoryType) {
		this.taskHistoryType = taskHistoryType;
	}

	@Override
	public IEntityTaskInstance getTaskInstance() {
		return null;
	}

	@Override
	public Date getBeginDateAllocation() {
		return null;
	}

	@Override
	public Date getEndDateAllocation() {
		return null;
	}

	@Override
	public MUser getAllocatedUser() {
		return null;
	}

	@Override
	public MUser getAllocatorUser() {
		return null;
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public IEntityTaskHistoricType getType() {
		return null;
	}
}