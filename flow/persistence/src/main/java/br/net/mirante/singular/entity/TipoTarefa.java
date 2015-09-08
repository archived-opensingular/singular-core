package br.net.mirante.singular.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the TB_TIPO_TAREFA database table.
 * 
 */
@Entity
@Table(name="TB_TIPO_TAREFA")
@NamedQuery(name="TipoTarefa.findAll", query="SELECT t FROM TipoTarefa t")
public class TipoTarefa implements EntidadeBasica {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="CO_TIPO_TAREFA")
	private Integer cod;

	@Column(name="DS_TIPO_TAREFA")
	private String descricao;

	public TipoTarefa() {
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

}