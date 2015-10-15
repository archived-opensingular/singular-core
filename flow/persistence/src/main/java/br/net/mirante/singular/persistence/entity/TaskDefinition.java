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

import br.net.mirante.singular.flow.core.entity.IEntityTaskDefinition;
import br.net.mirante.singular.persistence.util.Constants;

/**
 * The persistent class for the TB_DEFINICAO_TAREFA database table.
 */
@Entity
@Table(name = "TB_DEFINICAO_TAREFA", schema = Constants.SCHEMA)
public class TaskDefinition extends BaseEntity implements IEntityTaskDefinition {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CO_DEFINICAO_TAREFA")
    private Integer cod;

    @Column(name = "SG_TAREFA", nullable = false)
    private String abbreviation;

    //bi-directional many-to-one association to TaskRight
    @OneToMany(mappedBy = "taskDefinition")
    private List<TaskRight> permissoesTarefas;

    //bi-directional many-to-one association to ProcessDefinition
    @ManyToOne
    @JoinColumn(name = "CO_DEFINICAO_PROCESSO", nullable = false)
    private ProcessDefinition processDefinition;

    @OneToMany(mappedBy = "taskDefinition")
    private List<Task> versions;

    public TaskDefinition() {
    }

    @Override
    public Integer getCod() {
        return this.cod;
    }

    public void setCod(Integer cod) {
        this.cod = cod;
    }

    @Override
    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public List<TaskRight> getPermissoesTarefas() {
        return permissoesTarefas;
    }

    public void setPermissoesTarefas(List<TaskRight> permissoesTarefas) {
        this.permissoesTarefas = permissoesTarefas;
    }

    @Override
    public ProcessDefinition getProcessDefinition() {
        return processDefinition;
    }

    public void setProcessDefinition(ProcessDefinition processDefinition) {
        this.processDefinition = processDefinition;
    }

    @Override
    public List<Task> getVersions() {
        return versions;
    }

    public void setVersions(List<Task> versions) {
        this.versions = versions;
    }
}