package br.net.mirante.singular.persistence.entity;

import br.net.mirante.singular.flow.core.entity.IEntityExecutionVariable;

import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the TB_VARIAVEL_EXECUCAO_TRANSICAO database table.
 * 
 */
@Entity
@Table(name="TB_VARIAVEL_EXECUCAO_TRANSICAO")
@NamedQuery(name="VariavelExecucaoTransicao.findAll", query="SELECT v FROM VariavelExecucaoTransicao v")
public class ExecutionVariable implements IEntityExecutionVariable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="CO_VARIAVEL_EXECUCAO_TRANSICAO")
	private long cod;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DT_HISTORICO")
	private Date date;

	@Column(name="NO_VARIAVEL")
	private String name;

	@Column(name="VL_NOVO")
	private String value;

	//bi-directional many-to-one association to ProcessInstance
	@ManyToOne
	@JoinColumn(name="CO_INSTANCIA_PROCESSO")
	private ProcessInstance processInstance;

	//uni-directional many-to-one association to TaskInstance
	@ManyToOne
	@JoinColumn(name="CO_INSTANCIA_TAREFA_DESTINO")
	private TaskInstance destinationTask;

	//uni-directional many-to-one association to TaskInstance
	@ManyToOne
	@JoinColumn(name="CO_INSTANCIA_TAREFA_ORIGEM")
	private TaskInstance originTask;

	//uni-directional many-to-one association to VariableType
	@ManyToOne
	@JoinColumn(name="CO_TIPO_VARIAVEL")
	private VariableType variableType;

	//bi-directional many-to-one association to Variable
	@ManyToOne
	@JoinColumn(name="CO_VARIAVEL_INSTANCIA_PROCESSO")
	private Variable variable;

	public ExecutionVariable() {
	}

	public Long getCod() {
		return this.cod;
	}

	public void setCod(long cod) {
		this.cod = cod;
	}



	public ProcessInstance getProcessInstance() {
		return this.processInstance;
	}

	@Override
	public String getName() {
		return null;
	}

	public void setProcessInstance(ProcessInstance processInstance) {
		this.processInstance = processInstance;
	}

	public TaskInstance getDestinationTask() {
		return this.destinationTask;
	}

	@Override
	public String getValue() {
		return null;
	}

	@Override
	public Date getDate() {
		return null;
	}

	public void setDestinationTask(TaskInstance destinationTask) {
		this.destinationTask = destinationTask;
	}

	public TaskInstance getOriginTask() {
		return this.originTask;
	}

	public void setOriginTask(TaskInstance originTask) {
		this.originTask = originTask;
	}

	public VariableType getVariableType() {
		return this.variableType;
	}

	public void setVariableType(VariableType variableType) {
		this.variableType = variableType;
	}

	public Variable getVariable() {
		return this.variable;
	}

	public void setVariable(Variable variable) {
		this.variable = variable;
	}

    public void setDate(Date date) {
        this.date = date;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }
}