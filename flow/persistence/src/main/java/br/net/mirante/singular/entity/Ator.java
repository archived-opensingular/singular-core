package br.net.mirante.singular.entity;

import br.net.mirante.singular.flow.core.MUser;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the TB_ATOR database table.
 * 
 */
@Entity
@Table(name="TB_ATOR")
@NamedQuery(name="Ator.findAll", query="SELECT a FROM Ator a")
public class Ator implements EntidadeBasica, MUser {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="CO_ATOR")
	private Integer cod;

	public Ator() {
	}

	public Integer getCod() {
		return this.cod;
	}

	public void setCod(Integer cod) {
		this.cod = cod;
	}

	@Override
	public String getNomeGuerra() {
		return null;
	}

	@Override
	public String getEmail() {
		return null;
	}
}