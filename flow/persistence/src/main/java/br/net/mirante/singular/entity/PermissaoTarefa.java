package br.net.mirante.singular.entity;

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
 * The persistent class for the RL_PERMISSAO_TAREFA database table.
 * 
 */
@Entity
@Table(name="RL_PERMISSAO_TAREFA")
@NamedQuery(name="PermissaoTarefa.findAll", query="SELECT p FROM PermissaoTarefa p")
public class PermissaoTarefa implements EntidadeBasica {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="CO_TAREFA")
	private Integer cod;

	//bi-directional many-to-one association to DefinicaoTarefa
	@ManyToOne
	@JoinColumn(name="CO_DEFINICAO_TAREFA")
	private DefinicaoTarefa definicaoTarefa;

	public PermissaoTarefa() {
	}

	public Integer getCod() {
		return this.cod;
	}

	public void setCod(Integer cod) {
		this.cod = cod;
	}

	public DefinicaoTarefa getDefinicaoTarefa() {
		return this.definicaoTarefa;
	}

	public void setDefinicaoTarefa(DefinicaoTarefa definicaoTarefa) {
		this.definicaoTarefa = definicaoTarefa;
	}

}