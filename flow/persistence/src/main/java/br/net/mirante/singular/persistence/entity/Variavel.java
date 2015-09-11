package br.net.mirante.singular.persistence.entity;

import br.net.mirante.singular.flow.core.entity.IEntityProcessInstance;
import br.net.mirante.singular.flow.core.entity.IEntityVariableInstance;
import br.net.mirante.singular.flow.core.entity.IEntityVariableType;

import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the TB_VARIAVEL database table.
 * 
 */
@Entity
@Table(name="TB_VARIAVEL")
@NamedQuery(name="Variavel.findAll", query="SELECT v FROM Variavel v")
public class Variavel implements IEntityVariableInstance {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="CO_VARIAVEL")
	private Long cod;

	@Column(name="NO_VARIAVEL")
	private String nome;

	@Column(name="VL_VARIAVEL")
	private String valor;

	//bi-directional many-to-one association to ProcessInstance
	@ManyToOne
	@JoinColumn(name="CO_INSTANCIA_PROCESSO")
	private ProcessInstance processInstance;

	//uni-directional many-to-one association to VariableType
	@ManyToOne
	@JoinColumn(name="CO_TIPO_VARIAVEL")
	private VariableType variableType;

	//bi-directional many-to-one association to ExecutionVariable
	@OneToMany(mappedBy="variavel")
	private List<ExecutionVariable> variaveisExecucaoTransicao;

	public Variavel() {
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

	public String getValor() {
		return this.valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public ProcessInstance getProcessInstance() {
		return this.processInstance;
	}

	public void setProcessInstance(ProcessInstance processInstance) {
		this.processInstance = processInstance;
	}

	public VariableType getVariableType() {
		return this.variableType;
	}

	public void setVariableType(VariableType variableType) {
		this.variableType = variableType;
	}

	public List<ExecutionVariable> getVariaveisExecucaoTransicao() {
		return this.variaveisExecucaoTransicao;
	}

	public void setVariaveisExecucaoTransicao(List<ExecutionVariable> variaveisExecucaoTransicao) {
		this.variaveisExecucaoTransicao = variaveisExecucaoTransicao;
	}

	public ExecutionVariable addVariaveisExecucaoTransicao(ExecutionVariable variaveisExecucaoTransicao) {
		getVariaveisExecucaoTransicao().add(variaveisExecucaoTransicao);
		variaveisExecucaoTransicao.setVariable(this);

		return variaveisExecucaoTransicao;
	}

	public ExecutionVariable removeVariaveisExecucaoTransicao(ExecutionVariable variaveisExecucaoTransicao) {
		getVariaveisExecucaoTransicao().remove(variaveisExecucaoTransicao);
		variaveisExecucaoTransicao.setVariable(null);

		return variaveisExecucaoTransicao;
	}

	@Override
	public IEntityVariableType getType() {
		return null;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public String getValue() {
		return null;
	}

}