package br.net.mirante.singular.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the TB_TIPO_VARIAVEL database table.
 * 
 */
@Entity
@Table(name="TB_TIPO_VARIAVEL")
@NamedQuery(name="TipoVariavel.findAll", query="SELECT t FROM TipoVariavel t")
public class TipoVariavel implements EntidadeBasica {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="CO_TIPO_VARIAVEL")
	private Integer cod;

	@Column(name="DS_TIPO_VARIAVEL")
	private String descricao;

	@Column(name="NO_CLASSE_JAVA")
	private String nome;

	public TipoVariavel() {
	}

	public Integer getCod() {
		return this.cod;
	}

	public void setCod(Integer cod) {
		this.cod = cod;
	}

	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

}