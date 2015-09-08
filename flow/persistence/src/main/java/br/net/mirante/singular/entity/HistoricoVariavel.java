package br.net.mirante.singular.entity;

import br.net.mirante.singular.flow.core.entity.IEntityProcessInstance;
import br.net.mirante.singular.flow.core.entity.IEntityTaskInstance;
import br.net.mirante.singular.flow.core.entity.IEntityVariable;

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
 * The persistent class for the TB_HISTORICO_VARIAVEL database table.
 * 
 */
@Entity
@Table(name="TB_HISTORICO_VARIAVEL")
@NamedQuery(name="HistoricoVariavel.findAll", query="SELECT h FROM HistoricoVariavel h")
public class HistoricoVariavel implements EntidadeBasica, IEntityVariable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="CO_HISTORICO_VARIAVEL")
	private Integer cod;

	@Column(name="DS_NOME")
	private String nome;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DT_HISTORICO")
	private Date data;

	@Column(name="VL_ANTERIOR_VARIAVEL")
	private String valorAnterior;

	//uni-directional many-to-one association to InstanciaProcesso
	@ManyToOne
	@JoinColumn(name="CO_INSTANCIA_PROCESSO")
	private InstanciaProcesso instanciaProcesso;

	//uni-directional many-to-one association to InstanciaTarefa
	@ManyToOne
	@JoinColumn(name="CO_INSTANCIA_TAREFA_DESTINO")
	private InstanciaTarefa instanciaTarefaDestino;

	//uni-directional many-to-one association to InstanciaTarefa
	@ManyToOne
	@JoinColumn(name="CO_INSTANCIA_TAREFA_ORIGEM")
	private InstanciaTarefa instanciaTarefaOrigem;

	//uni-directional many-to-one association to TipoVariavel
	@ManyToOne
	@JoinColumn(name="CO_TIPO_VARIAVEL")
	private TipoVariavel tipoVariavel;

	//uni-directional many-to-one association to Variavel
	@ManyToOne
	@JoinColumn(name="CO_VARIAVEL")
	private Variavel variavel;

	public HistoricoVariavel() {
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

	public Date getData() {
		return this.data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public String getValorAnterior() {
		return this.valorAnterior;
	}

	public void setValorAnterior(String valorAnterior) {
		this.valorAnterior = valorAnterior;
	}

	public InstanciaProcesso getInstanciaProcesso() {
		return this.instanciaProcesso;
	}

	public void setInstanciaProcesso(InstanciaProcesso instanciaProcesso) {
		this.instanciaProcesso = instanciaProcesso;
	}

	public InstanciaTarefa getInstanciaTarefaDestino() {
		return this.instanciaTarefaDestino;
	}

	public void setInstanciaTarefaDestino(InstanciaTarefa instanciaTarefaDestino) {
		this.instanciaTarefaDestino = instanciaTarefaDestino;
	}

	public InstanciaTarefa getInstanciaTarefaOrigem() {
		return this.instanciaTarefaOrigem;
	}

	public void setInstanciaTarefaOrigem(InstanciaTarefa instanciaTarefaOrigem) {
		this.instanciaTarefaOrigem = instanciaTarefaOrigem;
	}

	public TipoVariavel getTipoVariavel() {
		return this.tipoVariavel;
	}

	public void setTipoVariavel(TipoVariavel tipoVariavel) {
		this.tipoVariavel = tipoVariavel;
	}

	public Variavel getVariavel() {
		return this.variavel;
	}

	public void setVariavel(Variavel variavel) {
		this.variavel = variavel;
	}

	@Override
	public String getTextoValor() {
		return null;
	}

	@Override
	public void setTextoValor(String textoValor) {

	}

	@Override
	public IEntityTaskInstance getTarefaOrigem() {
		return null;
	}

	@Override
	public IEntityTaskInstance getTarefaDestino() {
		return null;
	}

	@Override
	public IEntityProcessInstance getDemanda() {
		return null;
	}
}