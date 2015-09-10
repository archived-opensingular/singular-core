package br.net.mirante.singular.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the TB_ATOR database table.
 * 
 */
@Entity
@Table(name="TB_ATOR")
@NamedQuery(name="Ator.findAll", query="SELECT a FROM Ator a")
public class Ator  {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="CO_ATOR")
	private Long cod;

	public Ator() {
	}

	public Long getCod() {
		return this.cod;
	}

	public void setCod(Long cod) {
		this.cod = cod;
	}

}