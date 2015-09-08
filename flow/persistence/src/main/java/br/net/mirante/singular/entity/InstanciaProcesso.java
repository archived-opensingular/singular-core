package br.net.mirante.singular.entity;

import br.net.mirante.singular.flow.core.MUser;
import br.net.mirante.singular.flow.core.entity.IEntityProcess;
import br.net.mirante.singular.flow.core.entity.IEntityProcessInstance;
import br.net.mirante.singular.flow.core.entity.IEntityRole;
import br.net.mirante.singular.flow.core.entity.IEntityTaskDefinition;
import br.net.mirante.singular.flow.core.entity.IEntityTaskInstance;
import br.net.mirante.singular.flow.core.entity.IEntityVariable;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * The persistent class for the TB_INSTANCIA_PROCESSO database table.
 * 
 */
@Entity
@Table(name="TB_INSTANCIA_PROCESSO")
@NamedQuery(name="InstanciaProcesso.findAll", query="SELECT i FROM InstanciaProcesso i")
public class InstanciaProcesso implements EntidadeBasica, IEntityProcessInstance {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="CO_INSTANCIA_PROCESSO")
	private Integer cod;

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

	//bi-directional many-to-one association to InstanciaTarefa
	@OneToMany(mappedBy="instanciaProcesso")
	private List<InstanciaTarefa> instanciasTarefa;

	//bi-directional many-to-one association to Variavel
	@OneToMany(mappedBy="instanciaProcesso")
	private List<Variavel> variaveis;

	public InstanciaProcesso() {
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

	@Override
	public MUser getPessoaCriadora() {
		return null;
	}

	@Override
	public void setPessoaCriadora(MUser pessoaCriadora) {

	}

	@Override
	public Date getDataSituacaoAtual() {
		return null;
	}

	@Override
	public void setDataSituacaoAtual(Date dataSituacaoAtual) {

	}

	@Override
	public IEntityTaskInstance getTarefaPai() {
		return null;
	}

	@Override
	public List<? extends IEntityVariable> getHistoricoVariaveis() {
		return null;
	}

	@Override
	public List<? extends IEntityRole> getPapeis() {
		return null;
	}

	@Override
	public IEntityProcessInstance getDemandaPai() {
		return null;
	}

	@Override
	public IEntityTaskDefinition getSituacao() {
		return null;
	}

	@Override
	public List<? extends IEntityTaskInstance> getTarefas() {
		return null;
	}

	@Override
	public IEntityProcess getDefinicao() {
		return null;
	}

	@Override
	public List<? extends IEntityProcessInstance> getDemandasFilhas() {
		return null;
	}
}