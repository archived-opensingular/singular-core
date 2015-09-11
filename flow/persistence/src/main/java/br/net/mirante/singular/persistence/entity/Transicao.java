package br.net.mirante.singular.persistence.entity;

import br.net.mirante.singular.flow.core.entity.IEntityTask;
import br.net.mirante.singular.flow.core.entity.IEntityTaskTransition;
import br.net.mirante.singular.flow.core.entity.TransitionType;

import javax.persistence.*;


/**
 * The persistent class for the TB_TRANSICAO database table.
 * 
 */
@Entity
@Table(name="TB_TRANSICAO")
@NamedQuery(name="Transicao.findAll", query="SELECT t FROM Transicao t")
public class Transicao implements IEntityTaskTransition {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="CO_TRANSICAO")
	private Long cod;

	@Column(name="NO_TRANSICAO")
	private String nome;

	@Column(name="SG_TRANSICAO")
	private String sigla;

	@Column(name="TP_TRANSICAO")
	private String tipoTransicao;

	//uni-directional many-to-one association to Task
	@ManyToOne
	@JoinColumn(name="CO_TAREFA_DESTINO")
	private Task taskDestino;

	//uni-directional many-to-one association to Task
	@ManyToOne
	@JoinColumn(name="CO_TAREFA_ORIGEM")
	private Task taskOrigem;

	public Transicao() {
	}

	public Long getCod() {
		return this.cod;
	}

	public void setCod(Long cod) {
		this.cod = cod;
	}

	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getSigla() {
		return this.sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public String getTipoTransicao() {
		return this.tipoTransicao;
	}

	public void setTipoTransicao(String tipoTransicao) {
		this.tipoTransicao = tipoTransicao;
	}

	public Task getTaskDestino() {
		return this.taskDestino;
	}

	public void setTaskDestino(Task taskDestino) {
		this.taskDestino = taskDestino;
	}

	public Task getTaskOrigem() {
		return this.taskOrigem;
	}

	public void setTaskOrigem(Task taskOrigem) {
		this.taskOrigem = taskOrigem;
	}

	@Override
	public IEntityTask getOriginTask() {
		return null;
	}

	@Override
	public IEntityTask getDestinationTask() {
		return null;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public String getAbbreviation() {
		return null;
	}

	@Override
	public TransitionType getType() {
		return null;
	}
}