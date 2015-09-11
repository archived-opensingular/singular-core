package br.net.mirante.singular.persistence.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the TB_TIPO_TAREFA database table.
 * 
 */
@Entity
@Table(name="TB_TIPO_TAREFA")
@NamedQuery(name="TipoTarefa.findAll", query="SELECT t FROM TipoTarefa t")
public class TipoTarefa  {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="CO_TIPO_TAREFA")
	private Long cod;

	@Column(name="DS_TIPO_TAREFA")
	private String descricao;

	public TipoTarefa() {
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

}