package br.net.mirante.singular.persistence.entity;

import br.net.mirante.singular.flow.core.entity.IEntityTask;
import br.net.mirante.singular.flow.core.entity.IEntityTaskDefinition;
import br.net.mirante.singular.persistence.util.Constants;

import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the TB_DEFINICAO_TAREFA database table.
 * 
 */
@Entity
@Table(name="TB_DEFINICAO_TAREFA", schema = Constants.SCHEMA)
public class TaskDefinition implements IEntityTaskDefinition {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="CO_DEFINICAO_TAREFA")
	private Long cod;

	@Column(name="SG_TAREFA")
	private String sigla;

	//bi-directional many-to-one association to TaskRight
	@OneToMany(mappedBy="taskDefinition")
	private List<TaskRight> permissoesTarefas;

	//bi-directional many-to-one association to ProcessDefinition
	@ManyToOne
	@JoinColumn(name="CO_DEFINICAO_PROCESSO")
	private ProcessDefinition processDefinition;

	public TaskDefinition() {
	}

	public Long getCod() {
		return this.cod;
	}

	public void setCod(Long cod) {
		this.cod = cod;
	}

	public String getSigla() {
		return this.sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public List<TaskRight> getPermissoesTarefas() {
		return this.permissoesTarefas;
	}

	public void setPermissoesTarefas(List<TaskRight> permissoesTarefas) {
		this.permissoesTarefas = permissoesTarefas;
	}

	public TaskRight addPermissoesTarefa(TaskRight permissoesTarefa) {
		getPermissoesTarefas().add(permissoesTarefa);
		permissoesTarefa.setTaskDefinition(this);

		return permissoesTarefa;
	}

	public TaskRight removePermissoesTarefa(TaskRight permissoesTarefa) {
		getPermissoesTarefas().remove(permissoesTarefa);
		permissoesTarefa.setTaskDefinition(null);

		return permissoesTarefa;
	}

	public ProcessDefinition getProcessDefinition() {
		return this.processDefinition;
	}

	public void setProcessDefinition(ProcessDefinition processDefinition) {
		this.processDefinition = processDefinition;
	}


	@Override
	public String getAbbreviation() {
		return null;
	}

	@Override
	public List<? extends IEntityTask> getVersions() {
		return null;
	}
}