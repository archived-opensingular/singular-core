package br.net.mirante.singular.entity;

import br.net.mirante.singular.flow.core.MUser;
import br.net.mirante.singular.flow.core.entity.IEntityProcessInstance;
import br.net.mirante.singular.flow.core.entity.IEntityRole;

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
 * The persistent class for the TB_INSTANCIA_PAPEL database table.
 * 
 */
@Entity
@Table(name="TB_INSTANCIA_PAPEL")
@NamedQuery(name="InstanciaPapel.findAll", query="SELECT i FROM InstanciaPapel i")
public class InstanciaPapel implements EntidadeBasica, IEntityRole {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="CO_INSTANCIA_PAPEL")
	private Integer cod;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DT_CRIACAO")
	private Date dataCriacao;

	//uni-directional many-to-one association to Ator
	@ManyToOne
	@JoinColumn(name="CO_ATOR")
	private Ator ator;

	//uni-directional many-to-one association to Ator
	@ManyToOne
	@JoinColumn(name="CO_ATOR_ALOCADOR")
	private Ator atorAlocador;

	//uni-directional many-to-one association to InstanciaProcesso
	@ManyToOne
	@JoinColumn(name="CO_INSTANCIA_PROCESSO")
	private InstanciaProcesso instanciaProcesso;

	//uni-directional many-to-one association to Papel
	@ManyToOne
	@JoinColumn(name="CO_PAPEL")
	private Papel papel;

	public InstanciaPapel() {
	}

	public Integer getCod() {
		return this.cod;
	}

	public void setCod(Integer cod) {
		this.cod = cod;
	}

	public Date getDataCriacao() {
		return this.dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public Ator getAtor() {
		return this.ator;
	}

	public void setAtor(Ator ator) {
		this.ator = ator;
	}

	public Ator getAtorAlocador() {
		return this.atorAlocador;
	}

	public void setAtorAlocador(Ator atorAlocador) {
		this.atorAlocador = atorAlocador;
	}

	public InstanciaProcesso getInstanciaProcesso() {
		return this.instanciaProcesso;
	}

	public void setInstanciaProcesso(InstanciaProcesso instanciaProcesso) {
		this.instanciaProcesso = instanciaProcesso;
	}

	public Papel getPapel() {
		return this.papel;
	}

	public void setPapel(Papel papel) {
		this.papel = papel;
	}

	@Override
	public MUser getPessoa() {
		return null;
	}

	@Override
	public void setPessoa(MUser pessoa) {

	}

	@Override
	public MUser getPessoaAtribuidora() {
		return null;
	}

	@Override
	public void setPessoaAtribuidora(MUser pessoaAtribuidora) {

	}

	@Override
	public IEntityProcessInstance getDemanda() {
		return null;
	}
}