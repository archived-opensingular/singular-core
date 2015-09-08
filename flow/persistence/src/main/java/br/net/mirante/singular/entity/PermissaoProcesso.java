package br.net.mirante.singular.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the RL_PERMISSAO_PROCESSO database table.
 * 
 */
@Entity
@Table(name="RL_PERMISSAO_PROCESSO")
@NamedQuery(name="PermissaoProcesso.findAll", query="SELECT p FROM PermissaoProcesso p")
public class PermissaoProcesso {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private PermissaoProcessoPK cod;

	//bi-directional many-to-one association to DefinicaoProcesso
	@ManyToOne
	@JoinColumn(name="CO_DEFINICAO_PROCESSO")
	private DefinicaoProcesso definicaoProcesso;

	public PermissaoProcesso() {
	}

	public PermissaoProcessoPK getCod() {
		return this.cod;
	}

	public void setCod(PermissaoProcessoPK cod) {
		this.cod = cod;
	}

	public DefinicaoProcesso getDefinicaoProcesso() {
		return this.definicaoProcesso;
	}

	public void setDefinicaoProcesso(DefinicaoProcesso definicaoProcesso) {
		this.definicaoProcesso = definicaoProcesso;
	}

}