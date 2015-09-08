package br.net.mirante.singular.entity;

import br.net.mirante.singular.flow.core.entity.IEntityProcess;

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
 * The persistent class for the TB_PROCESSO database table.
 * 
 */
@Entity
@Table(name="TB_PROCESSO")
@NamedQuery(name="Processo.findAll", query="SELECT p FROM Processo p")
public class Processo implements EntidadeBasica {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="CO_PROCESSO")
	private Integer cod;

	//uni-directional many-to-one association to DefinicaoProcesso
	@ManyToOne
	@JoinColumn(name="CO_DEFINICAO_PROCESSO")
	private DefinicaoProcesso definicaoProcesso;

	public Processo() {
	}

	public Integer getCod() {
		return this.cod;
	}

	public void setCod(Integer cod) {
		this.cod = cod;
	}

	public DefinicaoProcesso getDefinicaoProcesso() {
		return this.definicaoProcesso;
	}

	public void setDefinicaoProcesso(DefinicaoProcesso definicaoProcesso) {
		this.definicaoProcesso = definicaoProcesso;
	}

}