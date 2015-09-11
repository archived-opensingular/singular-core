package br.net.mirante.singular.persistence.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the RL_PERMISSAO_PROCESSO database table.
 * 
 */
@Embeddable
public class PermissaoProcessoPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="CO_DEFINICAO_PROCESSO", insertable=false, updatable=false)
	private long coDefinicaoProcesso;

	@Column(name="TP_PERMISSAO")
	private String tipoPermissao;

	public PermissaoProcessoPK() {
	}
	public long getCoDefinicaoProcesso() {
		return this.coDefinicaoProcesso;
	}
	public void setCoDefinicaoProcesso(long coDefinicaoProcesso) {
		this.coDefinicaoProcesso = coDefinicaoProcesso;
	}
	public String getTipoPermissao() {
		return this.tipoPermissao;
	}
	public void setTipoPermissao(String tipoPermissao) {
		this.tipoPermissao = tipoPermissao;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof PermissaoProcessoPK)) {
			return false;
		}
		PermissaoProcessoPK castOther = (PermissaoProcessoPK)other;
		return 
			(this.coDefinicaoProcesso == castOther.coDefinicaoProcesso)
			&& this.tipoPermissao.equals(castOther.tipoPermissao);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.coDefinicaoProcesso ^ (this.coDefinicaoProcesso >>> 32)));
		hash = hash * prime + this.tipoPermissao.hashCode();
		
		return hash;
	}
}