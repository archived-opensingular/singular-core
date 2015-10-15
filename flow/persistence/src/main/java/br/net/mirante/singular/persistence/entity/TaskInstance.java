package br.net.mirante.singular.persistence.entity;

import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.net.mirante.singular.flow.core.MUser;
import br.net.mirante.singular.flow.core.entity.IEntityExecutionVariable;
import br.net.mirante.singular.flow.core.entity.IEntityTaskInstance;
import br.net.mirante.singular.flow.core.entity.IEntityTaskInstanceHistory;
import br.net.mirante.singular.flow.core.entity.IEntityTaskTransitionVersion;
import br.net.mirante.singular.persistence.util.Constants;

/**
 * The persistent class for the TB_INSTANCIA_TAREFA database table.
 */
@Entity
@Table(name = "TB_INSTANCIA_TAREFA", schema = Constants.SCHEMA)
public class TaskInstance implements IEntityTaskInstance {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CO_INSTANCIA_TAREFA")
    private Integer cod;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATA_ALVO_FIM")
    private Date targetEndDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATA_ALVO_SUSPENSAO")
    private Date suspensionTargetDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_ESPERADA_FIM")
    private Date expectedEndDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_FIM")
    private Date endDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_INICIO", nullable = false)
    private Date beginDate;

    @Column(name = "SE_SUSPENSA")
    private Boolean suspended;

    @OneToMany(mappedBy = "parentTask")
    private List<ProcessInstance> childProcesses;

    @ManyToOne
    @JoinColumn(name = "CO_ATOR_ALOCADO")
    private Actor allocatedUser;

    @ManyToOne
    @JoinColumn(name = "CO_ATOR_CONCLUSAO")
    private Actor responsibleUser;

    @ManyToOne
    @JoinColumn(name = "CO_INSTANCIA_PROCESSO", nullable = false)
    private ProcessInstance processInstance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_TAREFA", nullable = false)
    private Task task;

    @ManyToOne
    @JoinColumn(name = "CO_TRANSICAO_EXECUTADA")
    private Transition executedTransition;

    public TaskInstance() {
    }

    @Override
    public Integer getCod() {
        return this.cod;
    }

    public void setCod(Integer cod) {
        this.cod = cod;
    }

    @Override
    public Date getTargetEndDate() {
        return targetEndDate;
    }

    @Override
    public void setTargetEndDate(Date targetEndDate) {
        this.targetEndDate = targetEndDate;
    }

    public Date getSuspensionTargetDate() {
        return suspensionTargetDate;
    }

    @Override
    public void setSuspensionTargetDate(Date suspensionTargetDate) {
        this.suspensionTargetDate = suspensionTargetDate;
    }

    public Date getExpectedEndDate() {
        return expectedEndDate;
    }

    public void setExpectedEndDate(Date expectedEndDate) {
        this.expectedEndDate = expectedEndDate;
    }

    @Override
    public Date getEndDate() {
        return endDate;
    }

    @Override
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public Date getBeginDate() {
        return beginDate;
    }

    @Override
    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Boolean getSuspended() {
        return suspended;
    }

    public void setSuspended(Boolean suspended) {
        this.suspended = suspended;
    }

    @Override
    public List<ProcessInstance> getChildProcesses() {
        return childProcesses;
    }

    public void setChildProcesses(List<ProcessInstance> childProcesses) {
        this.childProcesses = childProcesses;
    }

    @Override
    public Actor getAllocatedUser() {
        return allocatedUser;
    }

    @Override
    public void setAllocatedUser(MUser allocatedUser) {
        setAllocatedUser((Actor) allocatedUser);
    }

    public void setAllocatedUser(Actor allocatedUser) {
        this.allocatedUser = allocatedUser;
    }

    @Override
    public Actor getResponsibleUser() {
        return responsibleUser;
    }

    @Override
    public void setResponsibleUser(MUser responsibleUser) {
        setResponsibleUser((Actor) responsibleUser);
    }

    public void setResponsibleUser(Actor responsibleUser) {
        this.responsibleUser = responsibleUser;
    }

    @Override
    public ProcessInstance getProcessInstance() {
        return processInstance;
    }

    public void setProcessInstance(ProcessInstance processInstance) {
        this.processInstance = processInstance;
    }

    @Override
    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    @Override
    public Transition getExecutedTransition() {
        return executedTransition;
    }

    @Override
    public void setExecutedTransition(IEntityTaskTransitionVersion transition) {
        setExecutedTransition((Transition) executedTransition);
    }

    public void setExecutedTransition(Transition executedTransition) {
        this.executedTransition = executedTransition;
    }

    @Override
    public List<? extends IEntityExecutionVariable> getInputVariables() {
        throw new UnsupportedOperationException("Método não implementado");
    }

    @Override
    public List<? extends IEntityExecutionVariable> getOutputVariables() {
        throw new UnsupportedOperationException("Método não implementado");
    }

    @Override
    public List<? extends IEntityTaskInstanceHistory> getTaskHistoric() {
        throw new UnsupportedOperationException("Método não implementado");
    }

}