package br.net.mirante.singular.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the TB_INSTANCIA_PROCESSO database table.
 * 
 */
@Entity
@Table(name="TB_INSTANCIA_PROCESSO")
@NamedQuery(name="InstanciaProcesso.findAll", query="SELECT i FROM InstanciaProcesso i")
public class InstanciaProcesso  {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="CO_INSTANCIA_PROCESSO")
	private Long cod;

	@Column(name="DS_INSTANCIA_PROCESSO")
	private String descricao;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DT_FIM")
	private Date dataFim;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DT_INICIO")
	private Date dataInicio;

	//uni-directional many-to-one association to Ator
	@ManyToOne
	@JoinColumn(name="CO_ATOR_CRIADOR")
	private Ator ator;

	//bi-directional many-to-one association to InstanciaTarefa
	@ManyToOne
	@JoinColumn(name="CO_INSTANCIA_TAREFA_PAI")
	private InstanciaTarefa instanciaTarefaPai;

	//bi-directional many-to-one association to InstanciaTarefa
	@ManyToOne
	@JoinColumn(name="CO_TAREFA_ATUAL")
	private InstanciaTarefa instanciaTarefa;

	//uni-directional many-to-one association to Processo
	@ManyToOne
	@JoinColumn(name="CO_PROCESSO")
	private Processo processo;

	//uni-directional many-to-one association to Tarefa
	@ManyToOne
	@JoinColumn(name="CO_TAREFA_ATUAL")
	private Tarefa tarefa;

	//bi-directional many-to-one association to InstanciaTarefa
	@OneToMany(mappedBy="instanciaProcesso")
	private List<InstanciaTarefa> instanciasTarefa;

	//bi-directional many-to-one association to Variavel
	@OneToMany(mappedBy="instanciaProcesso")
	private List<Variavel> variaveis;

	//bi-directional many-to-one association to VariavelExecucaoTransicao
	@OneToMany(mappedBy="instanciaProcesso")
	private List<VariavelExecucaoTransicao> variaveisExecucaoTransicao;

	public InstanciaProcesso() {
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

	public Date getDataFim() {
		return this.dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public Date getDataInicio() {
		return this.dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Ator getAtor() {
		return this.ator;
	}

	public void setAtor(Ator ator) {
		this.ator = ator;
	}

	public InstanciaTarefa getInstanciaTarefaPai() {
		return this.instanciaTarefaPai;
	}

	public void setInstanciaTarefaPai(InstanciaTarefa instanciaTarefaPai) {
		this.instanciaTarefaPai = instanciaTarefaPai;
	}

	public InstanciaTarefa getInstanciaTarefa() {
		return this.instanciaTarefa;
	}

	public void setInstanciaTarefa(InstanciaTarefa instanciaTarefa) {
		this.instanciaTarefa = instanciaTarefa;
	}

	public Processo getProcesso() {
		return this.processo;
	}

	public void setProcesso(Processo processo) {
		this.processo = processo;
	}

	public Tarefa getTarefa() {
		return this.tarefa;
	}

	public void setTarefa(Tarefa tarefa) {
		this.tarefa = tarefa;
	}

	public List<InstanciaTarefa> getInstanciasTarefa() {
		return this.instanciasTarefa;
	}

	public void setInstanciasTarefa(List<InstanciaTarefa> instanciasTarefa) {
		this.instanciasTarefa = instanciasTarefa;
	}

	public InstanciaTarefa addInstanciasTarefa(InstanciaTarefa instanciasTarefa) {
		getInstanciasTarefa().add(instanciasTarefa);
		instanciasTarefa.setInstanciaProcesso(this);

		return instanciasTarefa;
	}

	public InstanciaTarefa removeInstanciasTarefa(InstanciaTarefa instanciasTarefa) {
		getInstanciasTarefa().remove(instanciasTarefa);
		instanciasTarefa.setInstanciaProcesso(null);

		return instanciasTarefa;
	}

	public List<Variavel> getVariaveis() {
		return this.variaveis;
	}

	public void setVariaveis(List<Variavel> variaveis) {
		this.variaveis = variaveis;
	}

	public Variavel addVariavei(Variavel variavei) {
		getVariaveis().add(variavei);
		variavei.setInstanciaProcesso(this);

		return variavei;
	}

	public Variavel removeVariavei(Variavel variavei) {
		getVariaveis().remove(variavei);
		variavei.setInstanciaProcesso(null);

		return variavei;
	}

	public List<VariavelExecucaoTransicao> getVariaveisExecucaoTransicao() {
		return this.variaveisExecucaoTransicao;
	}

	public void setVariaveisExecucaoTransicao(List<VariavelExecucaoTransicao> variaveisExecucaoTransicao) {
		this.variaveisExecucaoTransicao = variaveisExecucaoTransicao;
	}

	public VariavelExecucaoTransicao addVariaveisExecucaoTransicao(VariavelExecucaoTransicao variaveisExecucaoTransicao) {
		getVariaveisExecucaoTransicao().add(variaveisExecucaoTransicao);
		variaveisExecucaoTransicao.setInstanciaProcesso(this);

		return variaveisExecucaoTransicao;
	}

	public VariavelExecucaoTransicao removeVariaveisExecucaoTransicao(VariavelExecucaoTransicao variaveisExecucaoTransicao) {
		getVariaveisExecucaoTransicao().remove(variaveisExecucaoTransicao);
		variaveisExecucaoTransicao.setInstanciaProcesso(null);

		return variaveisExecucaoTransicao;
	}

}