package br.net.mirante.singular.persistence.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import br.net.mirante.singular.flow.core.entity.IEntityTaskVersion;
import br.net.mirante.singular.persistence.util.Constants;


/**
 * The persistent class for the TB_TAREFA database table.
 */
@Entity
@Table(name = "TB_TAREFA", schema = Constants.SCHEMA)
public class Task extends BaseEntity implements IEntityTaskVersion {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CO_TAREFA")
    private Integer cod;

    @Column(name = "NO_TAREFA", nullable = false)
    private String name;

    //uni-directional many-to-one association to TaskDefinition
    @ManyToOne
    @JoinColumn(name = "CO_DEFINICAO_TAREFA", nullable = false)
    private TaskDefinition taskDefinition;

    //uni-directional many-to-one association to Processo
    @ManyToOne
    @JoinColumn(name = "CO_PROCESSO", nullable = false)
    private Process process;

    //uni-directional many-to-one association to TaskType
    @ManyToOne
    @JoinColumn(name = "CO_TIPO_TAREFA", nullable = false)
    private TaskType type;

    @OneToMany(mappedBy = "originTask")
    private List<Transition> transitions;

    public Task() {
    }

    @Override
    public Integer getCod() {
        return this.cod;
    }

    public void setCod(Integer cod) {
        this.cod = cod;
    }

    @Override
    public TaskDefinition getTaskDefinition() {
        return this.taskDefinition;
    }

    public void setTaskDefinition(TaskDefinition taskDefinition) {
        this.taskDefinition = taskDefinition;
    }

    @Override
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
    public TaskType getType() {
        return type;
    }

    public void setType(TaskType type) {
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