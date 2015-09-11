package br.net.mirante.singular.entity;

import br.net.mirante.singular.flow.core.entity.IEntityProcessDefinition;
import br.net.mirante.singular.flow.core.entity.IEntityProcessRole;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the TB_PAPEL database table.
 * 
 */
@Entity
@Table(name="TB_PAPEL")
@NamedQuery(name="Papel.findAll", query="SELECT p FROM Papel p")
public class Papel implements IEntityProcessRole {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="CO_PAPEL")
	private Long cod;

	@Column(name="NO_PAPEL")
	private String nome;

	@Column(name="SG_PAPEL")
	private String sigla;

	//bi-directional many-to-one association to DefinicaoProcesso
	@ManyToOne
	@JoinColumn(name="CO_DEFINICAO_PROCESSO")
	private DefinicaoProcesso definicaoProcesso;

	public Papel() {
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

	public DefinicaoProcesso getDefinicaoProcesso() {
		return this.definicaoProcesso;
	}

	public void setDefinicaoProcesso(DefinicaoProcesso definicaoProcesso) {
		this.definicaoProcesso = definicaoProcesso;
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

	@Override
	public IEntityProcessDefinition getProcessDefinition() {
		return null;
	}
}