package br.net.mirante.singular.entity;

import br.net.mirante.singular.flow.core.entity.IEntityCategory;

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
 * The persistent class for the TB_CATEGORIA database table.
 * 
 */
@Entity
@Table(name="TB_CATEGORIA")
@NamedQuery(name="Categoria.findAll", query="SELECT c FROM Categoria c")
public class Categoria implements EntidadeBasica, IEntityCategory {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="CO_CATEGORIA")
	private Integer cod;

	@Column(name="NO_CATEGORIA")
	private String nome;

	//uni-directional many-to-one association to Categoria
	@ManyToOne
	@JoinColumn(name="CO_CATEGORIA_PAI")
	private Categoria categoriaPai;

	public Categoria() {
	}

	public Integer getCod() {
		return this.cod;
	}

	public void setCod(Integer cod) {
		this.cod = cod;
	}

	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Categoria getCategoriaPai() {
		return this.categoriaPai;
	}

	public void setCategoriaPai(Categoria categoriaPai) {
		this.categoriaPai = categoriaPai;
	}

	@Override
	public String getNomeAbsoluto() {
		return null;
	}

	@Override
	public void setNomeAbsoluto(String nomeAbsoluto) {

	}

	@Override
	public IEntityCategory getPai() {
		return null;
	}
}