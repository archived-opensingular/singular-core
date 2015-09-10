package br.net.mirante.singular.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the TB_HISTORICO_INSTANCIA_TAREFA database table.
 * 
 */
@Entity
@Table(name="TB_HISTORICO_INSTANCIA_TAREFA")
@NamedQuery(name="HistoricoInstanciaTarefa.findAll", query="SELECT h FROM HistoricoInstanciaTarefa h")
public class HistoricoInstanciaTarefa  {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="CO_HISTORICO_ALOCACAO")
	private Long cod;

	@Column(name="DS_COMPLEMENTO")
	private String complemento;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DT_FIM_ALOCACAO")
	private Date dataFimAlocacao;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DT_INICIO_ALOCACAO")
	private Date dataInicioAlocacao;

	//uni-directional many-to-one association to Ator
	@ManyToOne
	@JoinColumn(name="CO_ATOR_ALOCADO")
	private Ator atorAlocado;

	//uni-directional many-to-one association to Ator
	@ManyToOne
	@JoinColumn(name="CO_ATOR_ALOCADOR")
	private Ator atorAlocador;

	//uni-directional many-to-one association to InstanciaTarefa
	@ManyToOne
	@JoinColumn(name="CO_INSTANCIA_TAREFA")
	private InstanciaTarefa instanciaTarefa;

	//uni-directional many-to-one association to TipoHistoricoTarefa
	@ManyToOne
	@JoinColumn(name="CO_TIPO_HISTORICO_TAREFA")
	private TipoHistoricoTarefa tipoHistoricoTarefa;

	public HistoricoInstanciaTarefa() {
	}

	public Long getCod() {
		return this.cod;
	}

	public void setCod(Long cod) {
		this.cod = cod;
	}

	public String getComplemento() {
		return this.complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	public Date getDataFimAlocacao() {
		return this.dataFimAlocacao;
	}

	public void setDataFimAlocacao(Date dataFimAlocacao) {
		this.dataFimAlocacao = dataFimAlocacao;
	}

	public Date getDataInicioAlocacao() {
		return this.dataInicioAlocacao;
	}

	public void setDataInicioAlocacao(Date dataInicioAlocacao) {
		this.dataInicioAlocacao = dataInicioAlocacao;
	}

	public Ator getAtorAlocado() {
		return this.atorAlocado;
	}

	public void setAtorAlocado(Ator atorAlocado) {
		this.atorAlocado = atorAlocado;
	}

	public Ator getAtorAlocador() {
		return this.atorAlocador;
	}

	public void setAtorAlocador(Ator atorAlocador) {
		this.atorAlocador = atorAlocador;
	}

	public InstanciaTarefa getInstanciaTarefa() {
		return this.instanciaTarefa;
	}

	public void setInstanciaTarefa(InstanciaTarefa instanciaTarefa) {
		this.instanciaTarefa = instanciaTarefa;
	}

	public TipoHistoricoTarefa getTipoHistoricoTarefa() {
		return this.tipoHistoricoTarefa;
	}

	public void setTipoHistoricoTarefa(TipoHistoricoTarefa tipoHistoricoTarefa) {
		this.tipoHistoricoTarefa = tipoHistoricoTarefa;
	}

}