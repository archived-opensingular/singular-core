package br.net.mirante.singular.persistence.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.net.mirante.singular.flow.core.entity.IEntityTaskTransitionVersion;
import br.net.mirante.singular.flow.core.entity.TransitionType;
import br.net.mirante.singular.persistence.util.Constants;


/**
 * The persistent class for the TB_TRANSICAO database table.
 */
@Entity
@Table(name = "TB_VERSAO_TRANSICAO", schema = Constants.SCHEMA)
public class TaskTransitionVersionEntity extends BaseEntity implements IEntityTaskTransitionVersion {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CO_VERSAO_TRANSICAO")
    private Integer cod;

    @Column(name = "NO_TRANSICAO", nullable = false)
    private String name;

    @Column(name = "SG_TRANSICAO", nullable = false)
    private String abbreviation;

    @Enumerated(EnumType.STRING)
    @Column(name = "TP_TRANSICAO", nullable = false)
    private TransitionType type;

    //uni-directional many-to-one association to Task
    @ManyToOne
    @JoinColumn(name = "CO_VERSAO_TAREFA_DESTINO", nullable = false)
    private TaskVersionEntity destinationTask;

    //uni-directional many-to-one association to Task
    @ManyToOne
    @JoinColumn(name = "CO_VERSAO_TAREFA_ORIGEM", nullable = false)
    private TaskVersionEntity originTask;

    public TaskTransitionVersionEntity() {
    }

    @Override
    public Integer getCod() {
        return cod;
    }

    public void setCod(Integer cod) {
        this.cod = cod;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    @Override
    public TransitionType getType() {
        return type;
    }

    public void setType(TransitionType type) {
        this.type = type;
    }

    @Override
    public TaskVersionEntity getDestinationTask() {
        return destinationTask;
    }

    public void setDestinationTask(TaskVersionEntity destinationTask) {
        this.destinationTask = destinationTask;
    }

    @Override
    public TaskVersionEntity getOriginTask() {
        return originTask;
    }

    public void setOriginTask(TaskVersionEntity originTask) {
        this.originTask = originTask;
    }
}