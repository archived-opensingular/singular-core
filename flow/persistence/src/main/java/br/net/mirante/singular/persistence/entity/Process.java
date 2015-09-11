package br.net.mirante.singular.persistence.entity;

import br.net.mirante.singular.flow.core.entity.IEntityProcess;

import javax.persistence.*;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the TB_PROCESSO database table.
 * 
 */
@Entity
@Table(name="TB_PROCESSO")
@NamedQuery(name="Process.findAll", query="SELECT p FROM Process p")
public class Process implements IEntityProcess {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="CO_PROCESSO")
	private Long cod;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DT_VERSAO")
	private Date versionDate;

	//uni-directional many-to-one association to ProcessDefinition
	@ManyToOne
	@JoinColumn(name="CO_DEFINICAO_PROCESSO")
	private ProcessDefinition processDefinition;

	@OneToMany(mappedBy = "process")
	private List<Task> tasks;

    //TODO mapear essa parada
	private List<TaskDefinition> taskDefinitions;

	public Process() {
	}

	public Long getCod() {
		return this.cod;
	}

	public void setCod(Long cod) {
		this.cod = cod;
	}

    @Override
    public Date getVersionDate() {
        return versionDate;
    }

    public void setVersionDate(Date versionDate) {
        this.versionDate = versionDate;
    }

    @Override
    public ProcessDefinition getProcessDefinition() {
        return processDefinition;
    }

    public void setProcessDefinition(ProcessDefinition processDefinition) {
        this.processDefinition = processDefinition;
    }

    @Override
    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public List<TaskDefinition> getTaskDefinitions() {
        return taskDefinitions;
    }

    public void setTaskDefinitions(List<TaskDefinition> taskDefinitions) {
        this.taskDefinitions = taskDefinitions;
    }
}