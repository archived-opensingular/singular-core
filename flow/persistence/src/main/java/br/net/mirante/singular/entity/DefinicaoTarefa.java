package br.net.mirante.singular.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the TB_DEFINICAO_TAREFA database table.
 * 
 */
@Entity
@Table(name="TB_DEFINICAO_TAREFA")
@NamedQuery(name="DefinicaoTarefa.findAll", query="SELECT d FROM DefinicaoTarefa d")
public class DefinicaoTarefa  {
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

	//bi-directional many-to-one association to DefinicaoProcesso
	@ManyToOne
	@JoinColumn(name="CO_DEFINICAO_PROCESSO")
	private DefinicaoProcesso definicaoProcesso;

	public DefinicaoTarefa() {
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