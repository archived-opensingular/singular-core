package br.net.mirante.singular.entity;

import br.net.mirante.singular.flow.core.TaskType;
import br.net.mirante.singular.flow.core.entity.IEntityProcess;
import br.net.mirante.singular.flow.core.entity.IEntityTask;
import br.net.mirante.singular.flow.core.entity.IEntityTaskDefinition;
import br.net.mirante.singular.flow.core.entity.IEntityTaskTransition;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;


/**
 * The persistent class for the TB_TAREFA database table.
 * 
 */
@Entity
@Table(name="TB_TAREFA")
@NamedQuery(name="Tarefa.findAll", query="SELECT t FROM Tarefa t")
public class Tarefa implements IEntityTask {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="CO_TAREFA")
	private Long cod;

	@Column(name="NO_TAREFA")
	private String nome;

	//uni-directional many-to-one association to DefinicaoTarefa
	@ManyToOne
	@JoinColumn(name="CO_DEFINICAO_TAREFA")
	private DefinicaoTarefa definicaoTarefa;

	//uni-directional many-to-one association to Processo
	@ManyToOne
	@JoinColumn(name="CO_PROCESSO")
	private Processo processo;

	//uni-directional many-to-one association to TipoTarefa
	@ManyToOne
	@JoinColumn(name="CO_TIPO_TAREFA")
	private TipoTarefa tipoTarefa;

	public Tarefa() {
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

	public DefinicaoTarefa getDefinicaoTarefa() {
		return this.definicaoTarefa;
	}

	public void setDefinicaoTarefa(DefinicaoTarefa definicaoTarefa) {
		this.definicaoTarefa = definicaoTarefa;
	}

	public Processo getProcesso() {
		return this.processo;
	}

	public void setProcesso(Processo processo) {
		this.processo = processo;
	}

	public TipoTarefa getTipoTarefa() {
		return this.tipoTarefa;
	}

	public void setTipoTarefa(TipoTarefa tipoTarefa) {
		this.tipoTarefa = tipoTarefa;
	}

	@Override
	public IEntityProcess getProcess() {
		return null;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public TaskType getType() {
		return null;
	}

	@Override
	public IEntityTaskDefinition getTaskDefinition() {
		return null;
	}

	@Override
	public List<? extends IEntityTaskTransition> getTransitions() {
		return null;
	}
}