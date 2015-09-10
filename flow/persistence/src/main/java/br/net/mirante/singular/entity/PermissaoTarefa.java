package br.net.mirante.singular.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the RL_PERMISSAO_TAREFA database table.
 * 
 */
@Entity
@Table(name="RL_PERMISSAO_TAREFA")
@NamedQuery(name="PermissaoTarefa.findAll", query="SELECT p FROM PermissaoTarefa p")
public class PermissaoTarefa  {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="CO_TAREFA")
	private Long cod;

	//bi-directional many-to-one association to DefinicaoTarefa
	@ManyToOne
	@JoinColumn(name="CO_DEFINICAO_TAREFA")
	private DefinicaoTarefa definicaoTarefa;

	public PermissaoTarefa() {
	}

	public Long getCod() {
		return this.cod;
	}

	public void setCod(Long cod) {
		this.cod = cod;
	}

	public DefinicaoTarefa getDefinicaoTarefa() {
		return this.definicaoTarefa;
	}

	public void setDefinicaoTarefa(DefinicaoTarefa definicaoTarefa) {
		this.definicaoTarefa = definicaoTarefa;
	}

}