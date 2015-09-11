package br.net.mirante.singular.persistence.entity;

import br.net.mirante.singular.flow.core.MUser;
import br.net.mirante.singular.flow.core.entity.IEntityProcessInstance;
import br.net.mirante.singular.flow.core.entity.IEntityProcessRole;
import br.net.mirante.singular.flow.core.entity.IEntityRole;

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

	//uni-directional many-to-one association to Actor
	@ManyToOne
	@JoinColumn(name="CO_ATOR")
	private Actor actor;

	//uni-directional many-to-one association to Actor
	@ManyToOne
	@JoinColumn(name="CO_ATOR_ALOCADOR")
	private Actor actorAlocador;

	//uni-directional many-to-one association to ProcessInstance
	@ManyToOne
	@JoinColumn(name="CO_INSTANCIA_PROCESSO")
	private ProcessInstance processInstance;

	//uni-directional many-to-one association to Role
	@ManyToOne
	@JoinColumn(name="CO_PAPEL")
	private Role role;

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

	public Actor getActor() {
		return this.actor;
	}

	public void setActor(Actor actor) {
		this.actor = actor;
	}

	public Actor getActorAlocador() {
		return this.actorAlocador;
	}

	public void setActorAlocador(Actor actorAlocador) {
		this.actorAlocador = actorAlocador;
	}

	public ProcessInstance getProcessInstance() {
		return this.processInstance;
	}

	public void setProcessInstance(ProcessInstance processInstance) {
		this.processInstance = processInstance;
	}

	public Role getRole() {
		return this.role;
	}

	public void setRole(Role role) {
		this.role = role;
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



}