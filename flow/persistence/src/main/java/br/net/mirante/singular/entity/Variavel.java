package br.net.mirante.singular.entity;

import br.net.mirante.singular.flow.core.entity.IEntityProcessInstance;
import br.net.mirante.singular.flow.core.entity.IEntityVariableInstance;
import br.net.mirante.singular.flow.core.entity.IEntityVariableType;

import java.io.Serializable;
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

	//bi-directional many-to-one association to InstanciaProcesso
	@ManyToOne
	@JoinColumn(name="CO_INSTANCIA_PROCESSO")
	private InstanciaProcesso instanciaProcesso;

	//uni-directional many-to-one association to TipoVariavel
	@ManyToOne
	@JoinColumn(name="CO_TIPO_VARIAVEL")
	private TipoVariavel tipoVariavel;

	//bi-directional many-to-one association to VariavelExecucaoTransicao
	@OneToMany(mappedBy="variavel")
	private List<VariavelExecucaoTransicao> variaveisExecucaoTransicao;

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

	public InstanciaProcesso getInstanciaProcesso() {
		return this.instanciaProcesso;
	}

	public void setInstanciaProcesso(InstanciaProcesso instanciaProcesso) {
		this.instanciaProcesso = instanciaProcesso;
	}

	public TipoVariavel getTipoVariavel() {
		return this.tipoVariavel;
	}

	public void setTipoVariavel(TipoVariavel tipoVariavel) {
		this.tipoVariavel = tipoVariavel;
	}

	public List<VariavelExecucaoTransicao> getVariaveisExecucaoTransicao() {
		return this.variaveisExecucaoTransicao;
	}

	public void setVariaveisExecucaoTransicao(List<VariavelExecucaoTransicao> variaveisExecucaoTransicao) {
		this.variaveisExecucaoTransicao = variaveisExecucaoTransicao;
	}

	public VariavelExecucaoTransicao addVariaveisExecucaoTransicao(VariavelExecucaoTransicao variaveisExecucaoTransicao) {
		getVariaveisExecucaoTransicao().add(variaveisExecucaoTransicao);
		variaveisExecucaoTransicao.setVariavel(this);

		return variaveisExecucaoTransicao;
	}

	public VariavelExecucaoTransicao removeVariaveisExecucaoTransicao(VariavelExecucaoTransicao variaveisExecucaoTransicao) {
		getVariaveisExecucaoTransicao().remove(variaveisExecucaoTransicao);
		variaveisExecucaoTransicao.setVariavel(null);

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

	@Override
	public IEntityProcessInstance getProcessInstance() {
		return null;
	}
}