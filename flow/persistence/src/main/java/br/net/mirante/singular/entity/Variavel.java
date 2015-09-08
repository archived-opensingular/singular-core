package br.net.mirante.singular.entity;

import br.net.mirante.singular.flow.core.entity.IEntityProcessInstance;
import br.net.mirante.singular.flow.core.entity.IEntityVariableInstance;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the TB_VARIAVEL database table.
 * 
 */
@Entity
@Table(name="TB_VARIAVEL")
@NamedQuery(name="Variavel.findAll", query="SELECT v FROM Variavel v")
public class Variavel implements EntidadeBasica, IEntityVariableInstance {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="CO_VARIAVEL")
	private Integer cod;

	@Column(name="DS_NOME")
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

	public Variavel() {
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

	@Override
	public String getTextoValor() {
		return null;
	}

	@Override
	public void setTextoValor(String textoValor) {

	}

	@Override
	public IEntityProcessInstance getDemanda() {
		return null;
	}
}