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

import br.net.mirante.singular.flow.core.entity.IEntityExecutionVariable;
import br.net.mirante.singular.persistence.util.Constants;

/**
 * The persistent class for the TB_VARIAVEL_EXECUCAO_TRANSICAO database table.
 */
@Entity
@Table(name = "TB_VARIAVEL_EXECUCAO_TRANSICAO", schema = Constants.SCHEMA)
public class ExecutionVariable implements IEntityExecutionVariable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CO_VARIAVEL_EXECUCAO_TRANSICAO")
    private long cod;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_HISTORICO", nullable = false)
    private Date date;

    @Column(name = "NO_VARIAVEL", nullable = false)
    private String name;

    @Column(name = "VL_NOVO")
    private String value;

    //bi-directional many-to-one association to ProcessInstance
    @ManyToOne
    @JoinColumn(name = "CO_INSTANCIA_PROCESSO", nullable = false)
    private ProcessInstance processInstance;

    //uni-directional many-to-one association to TaskInstance
    @ManyToOne
    @JoinColumn(name = "CO_INSTANCIA_TAREFA_DESTINO", nullable = false)
    private TaskInstance destinationTask;

    //uni-directional many-to-one association to TaskInstance
    @ManyToOne
    @JoinColumn(name = "CO_INSTANCIA_TAREFA_ORIGEM", nullable = false)
    private TaskInstance originTask;

    //uni-directional many-to-one association to VariableType
    @ManyToOne
    @JoinColumn(name = "CO_TIPO_VARIAVEL", nullable = false)
    private VariableType variableType;

    //bi-directional many-to-one association to Variable
    @ManyToOne
    @JoinColumn(name = "CO_VARIAVEL_INSTANCIA_PROCESSO")
    private Variable variable;

    public ExecutionVariable() {
    }

    public Long getCod() {
        return this.cod;
    }

    public void setCod(long cod) {
        this.cod = cod;
    }

    public ProcessInstance getProcessInstance() {
        return this.processInstance;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setProcessInstance(ProcessInstance processInstance) {
        this.processInstance = processInstance;
    }

    public TaskInstance getDestinationTask() {
        return this.destinationTask;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public Date getDate() {
        return this.date;
    }

    public void setDestinationTask(TaskInstance destinationTask) {
        this.destinationTask = destinationTask;
    }

    public TaskInstance getOriginTask() {
        return this.originTask;
    }

    public void setOriginTask(TaskInstance originTask) {
        this.originTask = originTask;
    }

    public VariableType getVariableType() {
        return this.variableType;
    }

    public void setVariableType(VariableType variableType) {
        this.variableType = variableType;
    }

    public Variable getVariable() {
        return this.variable;
    }

    public void setVariable(Variable variable) {
        this.variable = variable;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }
}