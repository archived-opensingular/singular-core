package br.net.mirante.singular.entity;

import br.net.mirante.singular.flow.core.entity.IEntityVariableType;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the TB_TIPO_VARIAVEL database table.
 * 
 */
@Entity
@Table(name="TB_TIPO_VARIAVEL")
@NamedQuery(name="TipoVariavel.findAll", query="SELECT t FROM TipoVariavel t")
public class TipoVariavel implements IEntityVariableType {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="CO_TIPO_VARIAVEL")
	private Long cod;

	@Column(name="DS_TIPO_VARIAVEL")
	private String descricao;

	@Column(name="NO_CLASSE_JAVA")
	private String nome;

	public TipoVariavel() {
	}

	public Long getCod() {
		return this.cod;
	}

	public void setCod(Long cod) {
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

	@Override
	public String getTypeClassName() {
		return null;
	}

	@Override
	public String getDescription() {
		return null;
	}
}