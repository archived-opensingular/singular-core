package br.net.mirante.singular.persistence.entity;

import br.net.mirante.singular.flow.core.entity.IEntityProcessDefinition;
import br.net.mirante.singular.flow.core.entity.IEntityProcessRole;

import javax.persistence.*;


/**
 * The persistent class for the TB_PAPEL database table.
 * 
 */
@Entity
@Table(name="TB_PAPEL")
@NamedQuery(name="Papel.findAll", query="SELECT p FROM Papel p")
public class Role implements IEntityProcessRole {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="CO_PAPEL")
	private Long cod;

	@Column(name="NO_PAPEL")
	private String nome;

	@Column(name="SG_PAPEL")
	private String sigla;

	//bi-directional many-to-one association to ProcessDefinition
	@ManyToOne
	@JoinColumn(name="CO_DEFINICAO_PROCESSO")
	private ProcessDefinition processDefinition;

	public Role() {
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

	public ProcessDefinition getProcessDefinition() {
		return this.processDefinition;
	}

	public void setProcessDefinition(ProcessDefinition processDefinition) {
		this.processDefinition = processDefinition;
	}

	@Override
	public String getAbbreviation() {
		return null;
	}

	@Override
	public void setAbbreviation(String abbreviation) {

	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public void setName(String name) {

	}

}