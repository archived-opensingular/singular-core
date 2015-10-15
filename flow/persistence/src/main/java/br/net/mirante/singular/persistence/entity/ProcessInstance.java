package br.net.mirante.singular.persistence.entity;

import br.net.mirante.singular.flow.core.SingularFlowException;
import br.net.mirante.singular.flow.core.entity.IEntityProcessInstance;
import br.net.mirante.singular.flow.core.entity.IEntityTaskInstance;
import br.net.mirante.singular.persistence.util.Constants;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The persistent class for the TB_INSTANCIA_PROCESSO database table.
 */
@Entity
@Table(name = "TB_INSTANCIA_PROCESSO", schema = Constants.SCHEMA)
public class ProcessInstance implements IEntityProcessInstance {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CO_INSTANCIA_PROCESSO")
    private Integer cod;

    @Column(name = "DS_INSTANCIA_PROCESSO")
    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_FIM")
    private Date endDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_INICIO", nullable = false)
    private Date beginDate;

    //uni-directional many-to-one association to Actor
    @ManyToOne
    @JoinColumn(name = "CO_ATOR_CRIADOR")
    private Actor userCreator;

    //bi-directional many-to-one association to TaskInstance
    @ManyToOne
    @JoinColumn(name = "CO_INSTANCIA_TAREFA_PAI")
    private TaskInstance parentTask;

    //uni-directional many-to-one association to Process
    @ManyToOne
    @JoinColumn(name = "CO_VERSAO_PROCESSO", nullable = false)
    private Process process;

    //bi-directional many-to-one association to TaskInstance
    @OneToMany(mappedBy = "processInstance")
    @OrderBy("beginDate")
    private List<TaskInstance> tasks;

    //bi-directional many-to-one association to Variable
    @OneToMany(mappedBy = "processInstance")
    private List<Variable> variables;

    //bi-directional many-to-one association to ExecutionVariable
    @OneToMany(mappedBy = "processInstance")
    private List<ExecutionVariable> historicalVariables;

    @OneToMany(mappedBy = "processInstance")
    private List<RoleInstance> roles;

    @OneToMany(mappedBy = "processInstance", fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @Where(clause = "DT_FIM is null")
    private List<TaskInstance> currentTasks;

    @Override
    public Integer getCod() {
        return cod;
    }

    public void setCod(Integer cod) {
        this.cod = cod;
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

    @Override
    public Actor getUserCreator() {
        return userCreator;
    }

    public void setUserCreator(Actor userCreator) {
        this.userCreator = userCreator;
    }

    @Override
    public TaskInstance getParentTask() {
        return parentTask;
    }

    @Override
    public void setParentTask(IEntityTaskInstance parent) {
        setParentTask((TaskInstance) parent);
    }

    public void setParentTask(TaskInstance parentTask) {
        this.parentTask = parentTask;
    }

    @Override
    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }

    @Override
    public List<TaskInstance> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskInstance> tasks) {
        this.tasks = tasks;
    }

    @Override
    public List<Variable> getVariables() {
        return variables;
    }

    public void setVariables(List<Variable> variables) {
        this.variables = variables;
    }

    @Override
    public List<ExecutionVariable> getHistoricalVariables() {
        return historicalVariables;
    }

    @Override
    public List<RoleInstance> getRoles() {
        return roles;
    }

    public void setHistoricalVariables(List<ExecutionVariable> historicalVariables) {
        this.historicalVariables = historicalVariables;
    }

    public void setRoles(List<RoleInstance> roles) {
        this.roles = roles;
    }

    @Override
    public void addTask(IEntityTaskInstance taskInstance) {
        if (getTasks() == null) {
            setTasks(new ArrayList<>());
        }

        getTasks().add((TaskInstance) taskInstance);
    }

    public List<TaskInstance> getCurrentTasks() {
        return currentTasks;
    }

    public void setCurrentTasks(List<TaskInstance> currentTasks) {
        this.currentTasks = currentTasks;
    }

    public TaskInstance getCurrentTask() {
        if (currentTasks != null && currentTasks.size() == 1) {
            return currentTasks.stream().findFirst().get();
        } else if (currentTasks != null && currentTasks.size() != 1) {
            throw new SingularFlowException("Esse fluxo possui mais de um estado atual," +
                    " não é possível determinar um único estado atual");
        }
        return null;
    }
}