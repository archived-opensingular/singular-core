package br.net.mirante.singular.persistence.entity;

import br.net.mirante.singular.flow.core.MUser;
import br.net.mirante.singular.persistence.util.Constants;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the TB_ATOR database table.
 * 
 */
@Entity
@Table(name="TB_ATOR", schema = Constants.SCHEMA)
public class Actor {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="CO_ATOR")
	private Long cod;

	public Actor() {
	}

	public Long getCod() {
		return this.cod;
	}

	public void setCod(Long cod) {
		this.cod = cod;
	}

}