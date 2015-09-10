package br.net.mirante.singular.entity;

import br.net.mirante.singular.flow.core.entity.IEntityCategory;
import br.net.mirante.singular.flow.core.entity.IEntityProcessDefinition;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;


/**
 * The persistent class for the TB_CATEGORIA database table.
 * 
 */
@Entity
@Table(name="TB_CATEGORIA")
@NamedQuery(name="Categoria.findAll", query="SELECT c FROM Categoria c")
public class Categoria implements IEntityCategory {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="CO_CATEGORIA")
	private Long cod;

	@Column(name="NO_CATEGORIA")
	private String nome;

	public Categoria() {
	}

	public Long getCod() {
		return this.cod;
	}

	public void setCod(Long cod) {
		this.cod = cod;
	}

	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public List<? extends IEntityProcessDefinition> getProcessDefinitions() {
		return null;
	}
}