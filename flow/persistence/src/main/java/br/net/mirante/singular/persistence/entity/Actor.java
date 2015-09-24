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
public class Actor implements MUser {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="CO_ATOR")
	private Long cod;

	@Column(name="NO_ATOR", nullable = false)
	private String nome;

	@Column(name="DS_EMAIL", nullable = false)
	private String email;

	public Actor() {
	}

	public Actor(Long cod, String nome, String email) {
		this.cod = cod;
		this.nome = nome;
		this.email = email;
	}

	public Long getCod() {
		return this.cod;
	}

	public void setCod(Long cod) {
		this.cod = cod;
	}

	@Override
	public String getNomeGuerra() {
		return getNome();
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Override
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}