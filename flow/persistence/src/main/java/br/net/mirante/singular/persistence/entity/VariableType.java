package br.net.mirante.singular.persistence.entity;

import br.net.mirante.singular.flow.core.entity.IEntityVariableType;
import br.net.mirante.singular.persistence.util.Constants;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the TB_TIPO_VARIAVEL database table.
 * 
 */
@Entity
@Table(name="TB_TIPO_VARIAVEL", schema = Constants.SCHEMA)
public class VariableType implements IEntityVariableType {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="CO_TIPO_VARIAVEL")
	private Long cod;

	@Column(name="DS_TIPO_VARIAVEL", nullable = false)
	private String description;

	@Column(name="NO_CLASSE_JAVA", nullable = false)
	private String typeClassName;

	public VariableType() {
	}

	public Long getCod() {
		return this.cod;
	}

	public void setCod(Long cod) {
		this.cod = cod;
	}

	@Override
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String getTypeClassName() {
		return typeClassName;
	}

	public void setTypeClassName(String typeClassName) {
		this.typeClassName = typeClassName;
	}
}