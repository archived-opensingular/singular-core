package br.net.mirante.singular.persistence.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.OrderBy;

import br.net.mirante.singular.flow.core.MUser;
import br.net.mirante.singular.flow.core.entity.IEntityExecutionVariable;
import br.net.mirante.singular.flow.core.entity.IEntityProcessInstance;
import br.net.mirante.singular.flow.core.entity.IEntityProcessVersion;
import br.net.mirante.singular.flow.core.entity.IEntityRole;
import br.net.mirante.singular.flow.core.entity.IEntityTaskInstance;
import br.net.mirante.singular.flow.core.entity.IEntityVariableInstance;

@MappedSuperclass
public abstract class ProcessInstanceBase<USER extends MUser, PROCESS_VERSION extends IEntityProcessVersion, TASK_INSTANCE extends IEntityTaskInstance, VARIABLE_INSTANCE extends IEntityVariableInstance, ROLE_USER extends IEntityRole, EXECUTION_VAR extends IEntityExecutionVariable> extends BaseEntity implements IEntityProcessInstance {

    @Id
    @Column(name = "CO_INSTANCIA_PROCESSO")
    @GeneratedValue(generator = "generated_demanda_id")
    private Integer cod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_VERSAO_PROCESSO", nullable = false)
    private PROCESS_VERSION process;

    @Column(name = "DS_INSTANCIA_PROCESSO", length = 250)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_ATOR_CRIADOR")
    private USER userCreator;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_INICIO", nullable = false)
    private Date beginDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_FIM")
    private Date endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_INSTANCIA_TAREFA_PAI")
    private TASK_INSTANCE parentTask;

    @OneToMany(mappedBy = "processInstance", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private List<VARIABLE_INSTANCE> variables;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "processInstance", cascade = CascadeType.REMOVE)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private List<ROLE_USER> roles;

    @OrderBy(clause = "DT_INICIO asc")
    @OneToMany(mappedBy = "processInstance", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private List<TASK_INSTANCE> tasks;

    @OrderBy(clause = "DT_HISTORICO asc")
    @OneToMany(mappedBy = "processInstance", fetch = FetchType.LAZY)
    private List<EXECUTION_VAR> historicalVariables;

    @Override
    public ROLE_USER getRoleUserByAbbreviation(String siglaPapel) {
        return (ROLE_USER) IEntityProcessInstance.super.getRoleUserByAbbreviation(siglaPapel);
    }

    @Override
    public void addTask(IEntityTaskInstance taskInstance) {
        if (getTasks() == null) {
            setTasks(new ArrayList<>());
        }

        getTasks().add((TASK_INSTANCE) taskInstance);
    }

    @Override
    public Integer getCod() {
        return cod;
    }

    public void setCod(Integer cod) {
        this.cod = cod;
    }

    @Override
    public PROCESS_VERSION getProcess() {
        return process;
    }

    public void setProcess(PROCESS_VERSION process) {
        this.process = process;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public USER getUserCreator() {
        return userCreator;
    }

    public void setUserCreator(USER userCreator) {
        this.userCreator = userCreator;
    }

    @Override
    public Date getBeginDate() {
        return beginDate;
    }

    @Override
    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
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
    public TASK_INSTANCE getParentTask() {
        return parentTask;
    }

    @Override
    public void setParentTask(IEntityTaskInstance parentTask) {
        this.parentTask = (TASK_INSTANCE) parentTask;
    }

    @Override
    public List<VARIABLE_INSTANCE> getVariables() {
        return variables;
    }

    public void setVariables(List<VARIABLE_INSTANCE> variables) {
        this.variables = variables;
    }

    @Override
    public List<ROLE_USER> getRoles() {
        return roles;
    }

    public void setRoles(List<ROLE_USER> roles) {
        this.roles = roles;
    }

    @Override
    public List<TASK_INSTANCE> getTasks() {
        return tasks;
    }

    public void setTasks(List<TASK_INSTANCE> tasks) {
        this.tasks = tasks;
    }

    @Override
    public List<EXECUTION_VAR> getHistoricalVariables() {
        return historicalVariables;
    }

    public void setHistoricalVariables(List<EXECUTION_VAR> historicalVariables) {
        this.historicalVariables = historicalVariables;
    }
}
