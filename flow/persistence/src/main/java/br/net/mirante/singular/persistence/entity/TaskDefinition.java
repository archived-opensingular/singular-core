package br.net.mirante.singular.persistence.entity;

import br.net.mirante.singular.flow.core.entity.IEntityTaskDefinition;

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
 * The persistent class for the TB_DEFINICAO_TAREFA database table.
 */
@Entity
@Table(name = "TB_DEFINICAO_TAREFA")
@NamedQuery(name = "DefinicaoTarefa.findAll", query = "SELECT d FROM DefinicaoTarefa d")
public class TaskDefinition implements IEntityTaskDefinition {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CO_DEFINICAO_TAREFA")
    private Long cod;

    @Column(name = "SG_TAREFA")
    private String abbreviation;

    //bi-directional many-to-one association to TaskRight
    @OneToMany(mappedBy = "definicaoTarefa")
    private List<TaskRight> permissoesTarefas;

    //bi-directional many-to-one association to ProcessDefinition
    @ManyToOne
    @JoinColumn(name = "CO_DEFINICAO_PROCESSO")
    private ProcessDefinition processDefinition;

    @OneToMany(mappedBy = "taskDefinition")
    private List<Task> versions;


    public TaskDefinition() {
    }

    public Long getCod() {
        return this.cod;
    }

    public void setCod(Long cod) {
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