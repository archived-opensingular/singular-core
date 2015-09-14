package br.net.mirante.singular.persistence.entity;

import javax.persistence.*;


/**
 * The persistent class for the RL_PERMISSAO_TAREFA database table.
 * 
 */
@Entity
@Table(name="RL_PERMISSAO_TAREFA")
@NamedQuery(name="PermissaoTarefa.findAll", query="SELECT p FROM PermissaoTarefa p")
public class TaskRight {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="CO_TAREFA")
	private Long cod;

	//bi-directional many-to-one association to TaskDefinition
	@ManyToOne
	@JoinColumn(name="CO_DEFINICAO_TAREFA")
	private TaskDefinition taskDefinition;

	public TaskRight() {
	}

	public Long getCod() {
		return this.cod;
	}

	public void setCod(Long cod) {
		this.cod = cod;
	}

	public TaskDefinition getTaskDefinition() {
		return this.taskDefinition;
	}

	public void setTaskDefinition(TaskDefinition taskDefinition) {
		this.taskDefinition = taskDefinition;
	}

}