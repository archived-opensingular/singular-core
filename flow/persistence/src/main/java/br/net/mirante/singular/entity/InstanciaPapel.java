package br.net.mirante.singular.entity;

import br.net.mirante.singular.flow.core.MUser;
import br.net.mirante.singular.flow.core.entity.IEntityProcessInstance;
import br.net.mirante.singular.flow.core.entity.IEntityProcessRole;
import br.net.mirante.singular.flow.core.entity.IEntityRole;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the TB_INSTANCIA_PAPEL database table.
 * 
 */
@Entity
@Table(name="TB_INSTANCIA_PAPEL")
@NamedQuery(name="InstanciaPapel.findAll", query="SELECT i FROM InstanciaPapel i")
public class InstanciaPapel implements IEntityRole {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="CO_INSTANCIA_PAPEL")
	private Long cod;

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

	public Long getCod() {
		return this.cod;
	}

	public void setCod(Long cod) {
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
	public IEntityProcessRole getRole() {
		return null;
	}

	@Override
	public MUser getUser() {
		return null;
	}

	@Override
	public Date getCreateDate() {
		return null;
	}

	@Override
	public MUser getAllocatorUser() {
		return null;
	}

	@Override
	public IEntityProcessInstance getProcessInstance() {
		return null;
	}
}