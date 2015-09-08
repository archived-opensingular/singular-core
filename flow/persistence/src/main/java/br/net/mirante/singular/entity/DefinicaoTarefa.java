package br.net.mirante.singular.entity;

import br.net.mirante.singular.flow.core.TaskType;
import br.net.mirante.singular.flow.core.entity.IEntityProcess;
import br.net.mirante.singular.flow.core.entity.IEntityTaskDefinition;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;


/**
 * The persistent class for the TB_DEFINICAO_TAREFA database table.
 * 
 */
@Entity
@Table(name="TB_DEFINICAO_TAREFA")
@NamedQuery(name="DefinicaoTarefa.findAll", query="SELECT d FROM DefinicaoTarefa d")
public class DefinicaoTarefa implements EntidadeBasica {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="CO_DEFINICAO_TAREFA")
	private Integer cod;

	//bi-directional many-to-one association to PermissaoTarefa
	@OneToMany(mappedBy="definicaoTarefa")
	private List<PermissaoTarefa> permissoesTarefas;

	//bi-directional many-to-one association to DefinicaoProcesso
	@ManyToOne
	@JoinColumn(name="CO_DEFINICAO_PROCESSO")
	private DefinicaoProcesso definicaoProcesso;

	public DefinicaoTarefa() {
	}

	public Integer getCod() {
		return this.cod;
	}

	public void setCod(Integer cod) {
		this.cod = cod;
	}

	public List<PermissaoTarefa> getPermissoesTarefas() {
		return this.permissoesTarefas;
	}

	public void setPermissoesTarefas(List<PermissaoTarefa> permissoesTarefas) {
		this.permissoesTarefas = permissoesTarefas;
	}

	public PermissaoTarefa addPermissoesTarefa(PermissaoTarefa permissoesTarefa) {
		getPermissoesTarefas().add(permissoesTarefa);
		permissoesTarefa.setDefinicaoTarefa(this);

		return permissoesTarefa;
	}

	public PermissaoTarefa removePermissoesTarefa(PermissaoTarefa permissoesTarefa) {
		getPermissoesTarefas().remove(permissoesTarefa);
		permissoesTarefa.setDefinicaoTarefa(null);

		return permissoesTarefa;
	}

	public DefinicaoProcesso getDefinicaoProcesso() {
		return this.definicaoProcesso;
	}

	public void setDefinicaoProcesso(DefinicaoProcesso definicaoProcesso) {
		this.definicaoProcesso = definicaoProcesso;
	}

}