package br.net.mirante.singular.entity;

import br.net.mirante.singular.flow.core.MUser;
import br.net.mirante.singular.flow.core.entity.IEntityProcessInstance;
import br.net.mirante.singular.flow.core.entity.IEntityTaskHistoric;
import br.net.mirante.singular.flow.core.entity.IEntityTaskInstance;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * The persistent class for the TB_HISTORICO_INSTANCIA_TAREFA database table.
 * 
 */
@Entity
@Table(name="TB_HISTORICO_INSTANCIA_TAREFA")
@NamedQuery(name="HistoricoInstanciaTarefa.findAll", query="SELECT h FROM HistoricoInstanciaTarefa h")
public class HistoricoInstanciaTarefa implements EntidadeBasica, IEntityTaskHistoric {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="CO_HISTORICO_ALOCACAO")
	private Integer cod;

	@Column(name="DS_HISTORICO_INSTANCIA_TAREFA")
	private String descricao;

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

	@Override
	public Date getData() {
		return null;
	}

	@Override
	public void setData(Date data) {

	}

	@Override
	public MUser getPessoaAlocada() {
		return null;
	}

	@Override
	public void setPessoaAlocada(MUser pessoaAlocada) {

	}

	@Override
	public MUser getPessoaAlocadora() {
		return null;
	}

	@Override
	public void setPessoaAlocadora(MUser pessoaAlocadora) {

	}

	@Override
	public String getTextoDetalhamento() {
		return null;
	}

	@Override
	public void setTextoDetalhamento(String textoDetalhamento) {

	}

	@Override
	public IEntityTaskInstance getTarefa() {
		return null;
	}

	@Override
	public IEntityProcessInstance getDemanda() {
		return null;
	}

	@Override
	public String getDescricaoTipo() {
		return null;
	}
}