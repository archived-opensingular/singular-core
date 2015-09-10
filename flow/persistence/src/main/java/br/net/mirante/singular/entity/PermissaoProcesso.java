package br.net.mirante.singular.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the RL_PERMISSAO_PROCESSO database table.
 * 
 */
@Entity
@Table(name="RL_PERMISSAO_PROCESSO")
@NamedQuery(name="PermissaoProcesso.findAll", query="SELECT p FROM PermissaoProcesso p")
public class PermissaoProcesso  {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private PermissaoProcessoPK id;

	//bi-directional many-to-one association to DefinicaoProcesso
	@ManyToOne
	@JoinColumn(name="CO_DEFINICAO_PROCESSO")
	private DefinicaoProcesso definicaoProcesso;

	public PermissaoProcesso() {
	}

	public PermissaoProcessoPK getId() {
		return this.id;
	}

	public void setId(PermissaoProcessoPK id) {
		this.id = id;
	}

	public DefinicaoProcesso getDefinicaoProcesso() {
		return this.definicaoProcesso;
	}

	public void setDefinicaoProcesso(DefinicaoProcesso definicaoProcesso) {
		this.definicaoProcesso = definicaoProcesso;
	}

}