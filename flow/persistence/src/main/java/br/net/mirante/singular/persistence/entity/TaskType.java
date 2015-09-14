package br.net.mirante.singular.persistence.entity;

import br.net.mirante.singular.flow.core.IEntityTaskType;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the TB_TIPO_TAREFA database table.
 * 
 */
@Entity
@Table(name="TB_TIPO_TAREFA")
@NamedQuery(name="TipoTarefa.findAll", query="SELECT t FROM TipoTarefa t")
public class TaskType implements IEntityTaskType {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="CO_TIPO_TAREFA")
	private Long cod;

	@Column(name="DS_TIPO_TAREFA")
	private String abbreviation;

	public TaskType() {
	}

	public Long getCod() {
		return this.cod;
	}

	public void setCod(Long cod) {
		this.cod = cod;
	}

	@Override
	public String getImage() {
		return br.net.mirante.singular.flow.core.TaskType.valueOf(abbreviation).getImage();
	}

	@Override
	public boolean isEnd() {
		return br.net.mirante.singular.flow.core.TaskType.valueOf(abbreviation).isEnd();
	}

	@Override
	public boolean isJava() {
		return br.net.mirante.singular.flow.core.TaskType.valueOf(abbreviation).isJava();
	}

	@Override
	public boolean isPeople() {
		return br.net.mirante.singular.flow.core.TaskType.valueOf(abbreviation).isPeople();
	}

	@Override
	public boolean isWait() {
		return br.net.mirante.singular.flow.core.TaskType.valueOf(abbreviation).isWait();
	}

	@Override
	public String getAbbreviation() {
		return abbreviation;
	}

	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}
}