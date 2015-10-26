package br.net.mirante.singular.persistence.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
@Table(name = "TB_VERSAO_TAREFA", schema = Constants.SCHEMA)
public class TaskVersionEntity extends BaseEntity implements IEntityTaskVersion {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CO_VERSAO_TAREFA")
    private Integer cod;

    @Column(name = "NO_TAREFA", nullable = false)
    private String name;

    //uni-directional many-to-one association to TaskDefinition
    @ManyToOne
    @JoinColumn(name = "CO_DEFINICAO_TAREFA", nullable = false)
    private TaskDefinitionEntity taskDefinition;

    //uni-directional many-to-one association to Processo
    @ManyToOne
    @JoinColumn(name = "CO_VERSAO_PROCESSO", nullable = false)
    private ProcessVersionEntity process;

    //uni-directional many-to-one association to TaskType
    @ManyToOne
    @JoinColumn(name = "CO_TIPO_TAREFA", nullable = false)
    private TaskTypeEntity type;

    @OneToMany(mappedBy = "originTask")
    private List<TaskTransitionVersionEntity> transitions;

    public TaskVersionEntity() {
    }

    @Override
    public Integer getCod() {
        return this.cod;
    }

    public void setCod(Integer cod) {
        this.cod = cod;
    }

    @Override
    public TaskDefinitionEntity getTaskDefinition() {
        return this.taskDefinition;
    }

    public void setTaskDefinition(TaskDefinitionEntity taskDefinition) {
        this.taskDefinition = taskDefinition;
    }

    @Override
    public ProcessVersionEntity getProcess() {
        return this.process;
    }

    public void setProcess(ProcessVersionEntity process) {
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
    public TaskTypeEntity getType() {
        return type;
    }

    public void setType(TaskTypeEntity type) {
        this.type = type;
    }

    @Override
    public List<TaskTransitionVersionEntity> getTransitions() {
        return transitions;
    }

    public void setTransitions(List<TaskTransitionVersionEntity> transitions) {
        this.transitions = transitions;
    }
}