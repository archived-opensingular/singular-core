package br.net.mirante.singular.persistence.entity;

import br.net.mirante.singular.flow.core.IEntityTaskType;
import br.net.mirante.singular.flow.core.entity.IEntityTask;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;


/**
 * The persistent class for the TB_TAREFA database table.
 */
@Entity
@Table(name = "TB_TAREFA")
@NamedQuery(name = "Tarefa.findAll", query = "SELECT t FROM Tarefa t")
public class Task implements IEntityTask {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CO_TAREFA")
    private Long cod;

    @Column(name = "NO_TAREFA")
    private String name;

    //uni-directional many-to-one association to TaskDefinition
    @ManyToOne
    @JoinColumn(name = "CO_DEFINICAO_TAREFA")
    private TaskDefinition taskDefinition;

    //uni-directional many-to-one association to Processo
    @ManyToOne
    @JoinColumn(name = "CO_PROCESSO")
    private Process process;

    //uni-directional many-to-one association to TaskType
    @ManyToOne
    @JoinColumn(name = "CO_TIPO_TAREFA")
    private IEntityTaskType type;

    @OneToMany(mappedBy = "originTask")
    private List<Transition> transitions;

    public Task() {
    }

    public Long getCod() {
        return this.cod;
    }

    public void setCod(Long cod) {
        this.cod = cod;
    }

    public TaskDefinition getTaskDefinition() {
        return this.taskDefinition;
    }

    public void setTaskDefinition(TaskDefinition taskDefinition) {
        this.taskDefinition = taskDefinition;
    }

    public Process getProcess() {
        return this.process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public IEntityTaskType getType() {
        return type;
    }

    public void setType(IEntityTaskType type) {
        this.type = type;
    }

    @Override
    public List<Transition> getTransitions() {
        return transitions;
    }

    public void setTransitions(List<Transition> transitions) {
        this.transitions = transitions;
    }
}