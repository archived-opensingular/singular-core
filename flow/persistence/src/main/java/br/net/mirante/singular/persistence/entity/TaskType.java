package br.net.mirante.singular.persistence.entity;

import br.net.mirante.singular.flow.core.IEntityTaskType;
import br.net.mirante.singular.persistence.util.Constants;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the TB_TIPO_TAREFA database table.
 * 
 */
@Entity
@Table(name="TB_TIPO_TAREFA", schema = Constants.SCHEMA)
public class TaskType implements IEntityTaskType {
	private static final long serialVersionUID = 1L;

	public static final Long AUTOMATICA = 1L;
	public static final Long TAREFA_DE_USUARIO = 2L;
	public static final Long ESPERA = 3L;
	public static final Long FIM = 4L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="CO_TIPO_TAREFA")
	private Long cod;

	@Column(name="DS_TIPO_TAREFA")
	private String name;

	public TaskType() {
	}

	public TaskType(Long cod) {
		this.cod = cod;
	}

	public Long getCod() {
		return this.cod;
	}

	public void setCod(Long cod) {
		this.cod = cod;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getImage() {
		return br.net.mirante.singular.flow.core.TaskType.valueOf(getAbbreviation()).getImage();
	}

	@Override
	public boolean isEnd() {
		return br.net.mirante.singular.flow.core.TaskType.valueOf(getAbbreviation()).isEnd();
	}

	@Override
	public boolean isJava() {
		return br.net.mirante.singular.flow.core.TaskType.valueOf(getAbbreviation()).isJava();
	}

	@Override
	public boolean isPeople() {
		return br.net.mirante.singular.flow.core.TaskType.valueOf(getAbbreviation()).isPeople();
	}

	@Override
	public boolean isWait() {
		return br.net.mirante.singular.flow.core.TaskType.valueOf(getAbbreviation()).isWait();
	}

	@Override
	public String getAbbreviation() {
		if (getCod().equals(AUTOMATICA)) {
			return br.net.mirante.singular.flow.core.TaskType.Java.getAbbreviation();
		} else if (getCod().equals(TAREFA_DE_USUARIO)) {
			return br.net.mirante.singular.flow.core.TaskType.People.getAbbreviation();
		} else if (getCod().equals(ESPERA)) {
			return br.net.mirante.singular.flow.core.TaskType.Wait.getAbbreviation();
		} else if (getCod().equals(FIM)) {
			return br.net.mirante.singular.flow.core.TaskType.End.getAbbreviation();
		}

		return null;
	}
}