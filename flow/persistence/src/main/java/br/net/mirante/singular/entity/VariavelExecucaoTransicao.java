package br.net.mirante.singular.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the TB_VARIAVEL_EXECUCAO_TRANSICAO database table.
 * 
 */
@Entity
@Table(name="TB_VARIAVEL_EXECUCAO_TRANSICAO")
@NamedQuery(name="VariavelExecucaoTransicao.findAll", query="SELECT v FROM VariavelExecucaoTransicao v")
public class VariavelExecucaoTransicao  {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="CO_VARIAVEL_EXECUCAO_TRANSICAO")
	private long cod;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DT_HISTORICO")
	private Date dataHistorico;

	@Column(name="NO_VARIAVEL")
	private String nome;

	@Column(name="VL_NOVO")
	private String valor;

	//bi-directional many-to-one association to InstanciaProcesso
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

	//bi-directional many-to-one association to Variavel
	@ManyToOne
	@JoinColumn(name="CO_VARIAVEL_INSTANCIA_PROCESSO")
	private Variavel variavel;

	public VariavelExecucaoTransicao() {
	}

	public long getCod() {
		return this.cod;
	}

	public void setCod(long cod) {
		this.cod = cod;
	}

	public Date getDataHistorico() {
		return this.dataHistorico;
	}

	public void setDataHistorico(Date dataHistorico) {
		this.dataHistorico = dataHistorico;
	}

	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getValor() {
		return this.valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
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

}