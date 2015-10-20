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

import br.net.mirante.singular.flow.core.entity.IEntityProcessVersion;
import br.net.mirante.singular.persistence.util.Constants;
import br.net.mirante.singular.persistence.util.HybridIdentityOrSequenceGenerator;
import org.hibernate.annotations.GenericGenerator;

/**
 * The persistent class for the TB_PROCESSO database table.
 */
@Entity
@Table(name = "TB_VERSAO_PROCESSO", schema = Constants.SCHEMA)
public class ProcessVersionEntity extends BaseEntity implements IEntityProcessVersion {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "CO_VERSAO_PROCESSO")
    @GeneratedValue(generator = "singular")
    @GenericGenerator(name = "singular", strategy = HybridIdentityOrSequenceGenerator.CLASS_NAME)
    private Integer cod;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_VERSAO", nullable = false)
    private Date versionDate;

    //uni-directional many-to-one association to ProcessDefinition
    @ManyToOne
    @JoinColumn(name = "CO_DEFINICAO_PROCESSO", nullable = false)
    private ProcessDefinitionEntity processDefinition;

    @OneToMany(mappedBy = "process")
    private List<TaskVersionEntity> tasks;

    public ProcessVersionEntity() {
    }

    @Override
    public Integer getCod() {
        return this.cod;
    }

    public void setCod(Integer cod) {
        this.cod = cod;
    }

    @Override
    public Date getVersionDate() {
        return versionDate;
    }

    public void setVersionDate(Date versionDate) {
        this.versionDate = versionDate;
    }

    @Override
    public ProcessDefinitionEntity getProcessDefinition() {
        return processDefinition;
    }

    public void setProcessDefinition(ProcessDefinitionEntity processDefinition) {
        this.processDefinition = processDefinition;
    }

    @Override
    public List<TaskVersionEntity> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskVersionEntity> tasks) {
        this.tasks = tasks;
    }

}