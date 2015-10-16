package br.net.mirante.singular.persistence.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.net.mirante.singular.flow.core.entity.IEntityTaskHistoricType;
import br.net.mirante.singular.flow.core.entity.IEntityTaskInstanceHistory;
import br.net.mirante.singular.persistence.util.Constants;

/**
 * The persistent class for the TB_HISTORICO_INSTANCIA_TAREFA database table.
 */
@Entity
@Table(name = "TB_HISTORICO_INSTANCIA_TAREFA", schema = Constants.SCHEMA)
public class TaskInstanceHistoryEntity extends BaseEntity implements IEntityTaskInstanceHistory {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CO_HISTORICO_ALOCACAO")
    private Integer cod;

    @Column(name = "DS_COMPLEMENTO")
    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_FIM_ALOCACAO")
    private Date endDateAllocation;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_INICIO_ALOCACAO")
    private Date beginDateAllocation;

    //uni-directional many-to-one association to Actor
    @ManyToOne
    @JoinColumn(name = "CO_ATOR_ALOCADO")
    private Actor allocatedUser;

    //uni-directional many-to-one association to Actor
    @ManyToOne
    @JoinColumn(name = "CO_ATOR_ALOCADOR")
    private Actor allocatorUser;

    //uni-directional many-to-one association to TaskInstance
    @ManyToOne
    @JoinColumn(name = "CO_INSTANCIA_TAREFA", nullable = false)
    private TaskInstanceEntity taskInstance;

    //uni-directional many-to-one association to TaskHistoryType
    @ManyToOne
    @JoinColumn(name = "CO_TIPO_HISTORICO_TAREFA", nullable = false)
    private TaskHistoricTypeEntity taskHistoryType;

    public TaskInstanceHistoryEntity() {
    }

    @Override
    public Integer getCod() {
        return this.cod;
    }

    public void setCod(Integer cod) {
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
    public Date getEndDateAllocation() {
        return endDateAllocation;
    }

    public void setEndDateAllocation(Date endDateAllocation) {
        this.endDateAllocation = endDateAllocation;
    }

    @Override
    public Date getBeginDateAllocation() {
        return beginDateAllocation;
    }

    public void setBeginDateAllocation(Date beginDateAllocation) {
        this.beginDateAllocation = beginDateAllocation;
    }

    @Override
    public Actor getAllocatedUser() {
        return allocatedUser;
    }

    public void setAllocatedUser(Actor allocatedUser) {
        this.allocatedUser = allocatedUser;
    }

    @Override
    public Actor getAllocatorUser() {
        return allocatorUser;
    }

    public void setAllocatorUser(Actor allocatorUser) {
        this.allocatorUser = allocatorUser;
    }

    @Override
    public TaskInstanceEntity getTaskInstance() {
        return taskInstance;
    }

    public void setTaskInstance(TaskInstanceEntity taskInstance) {
        this.taskInstance = taskInstance;
    }

    public TaskHistoricTypeEntity getTaskHistoryType() {
        return taskHistoryType;
    }

    public void setTaskHistoryType(TaskHistoricTypeEntity taskHistoryType) {
        this.taskHistoryType = taskHistoryType;
    }

    @Override
    public IEntityTaskHistoricType getType() {
        throw new UnsupportedOperationException("Método não implementado");
    }

}