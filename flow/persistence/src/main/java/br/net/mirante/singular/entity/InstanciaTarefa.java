package br.net.mirante.singular.entity;

import br.net.mirante.singular.flow.core.MUser;
import br.net.mirante.singular.flow.core.entity.IEntityProcessInstance;
import br.net.mirante.singular.flow.core.entity.IEntityTaskDefinition;
import br.net.mirante.singular.flow.core.entity.IEntityTaskHistoric;
import br.net.mirante.singular.flow.core.entity.IEntityTaskInstance;
import br.net.mirante.singular.flow.core.entity.IEntityVariable;

import java.sql.Time;
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
 * The persistent class for the TB_INSTANCIA_TAREFA database table.
 * 
 */
@Entity
@Table(name="TB_INSTANCIA_TAREFA")
@NamedQuery(name="InstanciaTarefa.findAll", query="SELECT i FROM InstanciaTarefa i")
public class InstanciaTarefa implements EntidadeBasica, IEntityTaskInstance {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="CO_INSTANCIA_TAREFA")
	private Integer cod;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA_ALVO_FIM")
	private Date dataAlvoFim;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA_ALVO_SUSPENSAO")
	private Date dataAlvoSuspensao;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DT_FIM")
	private Date dataFim;

	@Column(name="DT_INICIO")
	private Time dataInicio;

	@Column(name="SE_SUSPENSA")
	private Boolean suspensa;

	//bi-directional many-to-one association to InstanciaProcesso
	@OneToMany(mappedBy="instanciaTarefaPai")
	private List<InstanciaProcesso> instanciasTarefaFilhas;

	//bi-directional many-to-one association to InstanciaProcesso
	@OneToMany(mappedBy="instanciaTarefa")
	private List<InstanciaProcesso> instanciasProcesso;

	//uni-directional many-to-one association to Ator
	@ManyToOne
	@JoinColumn(name="CO_ATOR_ALOCADO")
	private Ator atorAlocado;

	//uni-directional many-to-one association to Ator
	@ManyToOne
	@JoinColumn(name="CO_ATOR_CONCLUSAO")
	private Ator atorConclusao;

	//bi-directional many-to-one association to InstanciaProcesso
	@ManyToOne
	@JoinColumn(name="CO_INSTANCIA_PROCESSO")
	private InstanciaProcesso instanciaProcesso;

	//uni-directional many-to-one association to Tarefa
	@ManyToOne
	@JoinColumn(name="CO_TAREFA")
	private Tarefa tarefa;

	//uni-directional many-to-one association to Transicao
	@ManyToOne
	@JoinColumn(name="CO_TRANSICAO_EXECUTADA")
	private Transicao transicao;

	public InstanciaTarefa() {
	}

	public Integer getCod() {
		return this.cod;
	}

	public void setCod(Integer cod) {
		this.cod = cod;
	}

	public Date getDataAlvoFim() {
		return this.dataAlvoFim;
	}

	public void setDataAlvoFim(Date dataAlvoFim) {
		this.dataAlvoFim = dataAlvoFim;
	}

	public Date getDataAlvoSuspensao() {
		return this.dataAlvoSuspensao;
	}

	public void setDataAlvoSuspensao(Date dataAlvoSuspensao) {
		this.dataAlvoSuspensao = dataAlvoSuspensao;
	}

	public Date getDataFim() {
		return this.dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public Time getDataInicio() {
		return this.dataInicio;
	}

	public void setDataInicio(Time dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Boolean getSuspensa() {
		return this.suspensa;
	}

	public void setSuspensa(Boolean suspensa) {
		this.suspensa = suspensa;
	}

	public List<InstanciaProcesso> getInstanciasTarefaFilhas() {
		return this.instanciasTarefaFilhas;
	}

	public void setInstanciasTarefaFilhas(List<InstanciaProcesso> instanciasTarefaFilhas) {
		this.instanciasTarefaFilhas = instanciasTarefaFilhas;
	}

	public InstanciaProcesso addInstanciasTarefaFilha(InstanciaProcesso instanciasTarefaFilha) {
		getInstanciasTarefaFilhas().add(instanciasTarefaFilha);
		instanciasTarefaFilha.setInstanciaTarefaPai(this);

		return instanciasTarefaFilha;
	}

	public InstanciaProcesso removeInstanciasTarefaFilha(InstanciaProcesso instanciasTarefaFilha) {
		getInstanciasTarefaFilhas().remove(instanciasTarefaFilha);
		instanciasTarefaFilha.setInstanciaTarefaPai(null);

		return instanciasTarefaFilha;
	}

	public List<InstanciaProcesso> getInstanciasProcesso() {
		return this.instanciasProcesso;
	}

	public void setInstanciasProcesso(List<InstanciaProcesso> instanciasProcesso) {
		this.instanciasProcesso = instanciasProcesso;
	}

	public InstanciaProcesso addInstanciasProcesso(InstanciaProcesso instanciasProcesso) {
		getInstanciasProcesso().add(instanciasProcesso);
		instanciasProcesso.setInstanciaTarefa(this);

		return instanciasProcesso;
	}

	public InstanciaProcesso removeInstanciasProcesso(InstanciaProcesso instanciasProcesso) {
		getInstanciasProcesso().remove(instanciasProcesso);
		instanciasProcesso.setInstanciaTarefa(null);

		return instanciasProcesso;
	}

	public Ator getAtorAlocado() {
		return this.atorAlocado;
	}

	public void setAtorAlocado(Ator atorAlocado) {
		this.atorAlocado = atorAlocado;
	}

	public Ator getAtorConclusao() {
		return this.atorConclusao;
	}

	public void setAtorConclusao(Ator atorConclusao) {
		this.atorConclusao = atorConclusao;
	}

	public InstanciaProcesso getInstanciaProcesso() {
		return this.instanciaProcesso;
	}

	public void setInstanciaProcesso(InstanciaProcesso instanciaProcesso) {
		this.instanciaProcesso = instanciaProcesso;
	}

	public Tarefa getTarefa() {
		return this.tarefa;
	}

	public void setTarefa(Tarefa tarefa) {
		this.tarefa = tarefa;
	}

	public Transicao getTransicao() {
		return this.transicao;
	}

	public void setTransicao(Transicao transicao) {
		this.transicao = transicao;
	}

	@Override
	public boolean isSuspensa() {
		return false;
	}

	@Override
	public void setDataInicio(Date dataInicio) {

	}

	@Override
	public MUser getPessoaAlocada() {
		return null;
	}

	@Override
	public void setPessoaAlocada(MUser pessoaAlocada) {

	}

	@Override
	public MUser getAutorFim() {
		return null;
	}

	@Override
	public void setAutorFim(MUser autorFim) {

	}

	@Override
	public String getSiglaTransicaoResultado() {
		return null;
	}

	@Override
	public void setSiglaTransicaoResultado(String siglaTransicaoResultado) {

	}

	@Override
	public List<? extends IEntityTaskHistoric> getHistoricoAlocacao() {
		return null;
	}

	@Override
	public List<? extends IEntityVariable> getVariaveisGeradas() {
		return null;
	}

	@Override
	public List<? extends IEntityVariable> getVariaveisEntrada() {
		return null;
	}

	@Override
	public IEntityProcessInstance getDemanda() {
		return null;
	}

	@Override
	public IEntityTaskDefinition getSituacao() {
		return null;
	}
}