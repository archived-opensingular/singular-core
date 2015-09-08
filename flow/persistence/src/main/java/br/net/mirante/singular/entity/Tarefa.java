package br.net.mirante.singular.entity;

import br.net.mirante.singular.flow.core.TaskType;
import br.net.mirante.singular.flow.core.entity.IEntityProcess;
import br.net.mirante.singular.flow.core.entity.IEntityTaskDefinition;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the TB_TAREFA database table.
 * 
 */
@Entity
@Table(name="TB_TAREFA")
@NamedQuery(name="Tarefa.findAll", query="SELECT t FROM Tarefa t")
public class Tarefa implements EntidadeBasica, IEntityTaskDefinition {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="CO_TAREFA")
	private Integer cod;

	@Column(name="NO_TAREFA")
	private String nome;

	@Column(name="SG_TAREFA")
	private String sigla;

	//uni-directional many-to-one association to DefinicaoTarefa
	@ManyToOne
	@JoinColumn(name="CO_DEFINICAO_TAREFA")
	private DefinicaoTarefa definicaoTarefa;

	//uni-directional many-to-one association to Processo
	@ManyToOne
	@JoinColumn(name="CO_PROCESSO")
	private Processo processo;

	//uni-directional many-to-one association to TipoTarefa
	@Column(name="CO_TIPO_TAREFA")
	private TaskType tipoTarefa;

	public Tarefa() {
	}

	public Integer getCod() {
		return this.cod;
	}

	public void setCod(Integer cod) {
		this.cod = cod;
	}

	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getSigla() {
		return this.sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public DefinicaoTarefa getDefinicaoTarefa() {
		return this.definicaoTarefa;
	}

	public void setDefinicaoTarefa(DefinicaoTarefa definicaoTarefa) {
		this.definicaoTarefa = definicaoTarefa;
	}

	public Processo getProcesso() {
		return this.processo;
	}

	public void setProcesso(Processo processo) {
		this.processo = processo;
	}

	public TaskType getTipoTarefa() {
		return this.tipoTarefa;
	}

	public void setTipoTarefa(TaskType tipoTarefa) {
		this.tipoTarefa = tipoTarefa;
	}

	@Override
	public IEntityProcess getDefinicao() {
		return null;
	}

	@Override
	public boolean isFim() {
		return false;
	}

	@Override
	public boolean isPessoa() {
		return false;
	}

	@Override
	public boolean isWait() {
		return false;
	}

	@Override
	public boolean isJava() {
		return false;
	}

	@Override
	public String getDescricao() {
		return null;
	}
}