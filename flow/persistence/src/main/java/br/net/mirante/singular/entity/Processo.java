package br.net.mirante.singular.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the TB_PROCESSO database table.
 * 
 */
@Entity
@Table(name="TB_PROCESSO")
@NamedQuery(name="Processo.findAll", query="SELECT p FROM Processo p")
public class Processo  {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="CO_PROCESSO")
	private Long cod;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DT_VERSAO")
	private Date dataVersao;

	//uni-directional many-to-one association to DefinicaoProcesso
	@ManyToOne
	@JoinColumn(name="CO_DEFINICAO_PROCESSO")
	private DefinicaoProcesso definicaoProcesso;

	public Processo() {
	}

	public Long getCod() {
		return this.cod;
	}

	public void setCod(Long cod) {
		this.cod = cod;
	}

	public Date getDataVersao() {
		return this.dataVersao;
	}

	public void setDataVersao(Date dataVersao) {
		this.dataVersao = dataVersao;
	}

	public DefinicaoProcesso getDefinicaoProcesso() {
		return this.definicaoProcesso;
	}

	public void setDefinicaoProcesso(DefinicaoProcesso definicaoProcesso) {
		this.definicaoProcesso = definicaoProcesso;
	}

}