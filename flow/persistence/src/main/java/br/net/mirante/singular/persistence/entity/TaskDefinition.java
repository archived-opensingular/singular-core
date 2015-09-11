package br.net.mirante.singular.persistence.entity;

import br.net.mirante.singular.flow.core.entity.IEntityProcessDefinition;
import br.net.mirante.singular.flow.core.entity.IEntityTask;
import br.net.mirante.singular.flow.core.entity.IEntityTaskDefinition;

import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the TB_DEFINICAO_TAREFA database table.
 * 
 */
@Entity
@Table(name="TB_DEFINICAO_TAREFA")
@NamedQuery(name="DefinicaoTarefa.findAll", query="SELECT d FROM DefinicaoTarefa d")
public class TaskDefinition implements IEntityTaskDefinition {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="CO_DEFINICAO_TAREFA")
	private Long cod;

	@Column(name="SG_TAREFA")
	private String sigla;

	//bi-directional many-to-one association to PermissaoTarefa
	@OneToMany(mappedBy="definicaoTarefa")
	private List<PermissaoTarefa> permissoesTarefas;

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

	public List<PermissaoTarefa> getPermissoesTarefas() {
		return this.permissoesTarefas;
	}

	public void setPermissoesTarefas(List<PermissaoTarefa> permissoesTarefas) {
		this.permissoesTarefas = permissoesTarefas;
	}

	public PermissaoTarefa addPermissoesTarefa(PermissaoTarefa permissoesTarefa) {
		getPermissoesTarefas().add(permissoesTarefa);
		permissoesTarefa.setTaskDefinition(this);

		return permissoesTarefa;
	}

	public PermissaoTarefa removePermissoesTarefa(PermissaoTarefa permissoesTarefa) {
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