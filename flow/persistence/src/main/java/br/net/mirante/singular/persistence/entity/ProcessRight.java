package br.net.mirante.singular.persistence.entity;

import br.net.mirante.singular.persistence.util.Constants;

import javax.persistence.*;


/**
 * The persistent class for the RL_PERMISSAO_PROCESSO database table.
 * 
 */
@Entity
@Table(name="RL_PERMISSAO_PROCESSO", schema = Constants.SCHEMA)
public class ProcessRight {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private ProcessRightPK id;

	//bi-directional many-to-one association to ProcessDefinition
	@ManyToOne
	@JoinColumn(name="CO_DEFINICAO_PROCESSO", insertable=false, updatable=false)
	private ProcessDefinition processDefinition;

	public ProcessRight() {
	}

	public ProcessRightPK getId() {
		return this.id;
	}

	public void setId(ProcessRightPK id) {
		this.id = id;
	}

	public ProcessDefinition getProcessDefinition() {
		return this.processDefinition;
	}

	public void setProcessDefinition(ProcessDefinition processDefinition) {
		this.processDefinition = processDefinition;
	}

}