package br.net.mirante.singular.persistence.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import br.net.mirante.singular.flow.core.entity.IEntityProcessDefinition;
import br.net.mirante.singular.flow.core.entity.IEntityProcessVersion;
import br.net.mirante.singular.flow.core.entity.IEntityTaskVersion;

/**
 * The base persistent class for the TB_VERSAO_PROCESSO database table.
 * <p>
 * Must declare a {@link GenericGenerator} with name
 * {@link AbstractProcessVersionEntity#PK_GENERATOR_NAME}.
 * </p>
 * <code>@GenericGenerator(name = AbstractProcessVersionEntity.PK_GENERATOR_NAME, strategy = "org.hibernate.id.IdentityGenerator")</code>
 * 
 * @param <PROCESS_DEF>
 * @param <TASK_VERSION>
 */
@MappedSuperclass
@Table(name = "TB_VERSAO_PROCESSO")
public abstract class AbstractProcessVersionEntity<PROCESS_DEF extends IEntityProcessDefinition, TASK_VERSION extends IEntityTaskVersion> extends BaseEntity implements IEntityProcessVersion {

    public static final String PK_GENERATOR_NAME = "GENERATED_CO_VERSAO_PROCESSO";

    @Id
    @Column(name = "CO_VERSAO_PROCESSO")
    @GeneratedValue(generator = PK_GENERATOR_NAME)
    private Integer cod;

    @ManyToOne
    @JoinColumn(name = "CO_DEFINICAO_PROCESSO", nullable = false)
    private PROCESS_DEF processDefinition;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_VERSAO", nullable = false)
    private Date versionDate;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "processVersion")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private List<TASK_VERSION> versionTasks = new ArrayList<>();

    public Integer getCod() {
        return cod;
    }

    public void setCod(Integer cod) {
        this.cod = cod;
    }

    public PROCESS_DEF getProcessDefinition() {
        return processDefinition;
    }

    public void setProcessDefinition(PROCESS_DEF processDefinition) {
        this.processDefinition = processDefinition;
    }

    public Date getVersionDate() {
        return versionDate;
    }

    public void setVersionDate(Date versionDate) {
        this.versionDate = versionDate;
    }

    public List<TASK_VERSION> getVersionTasks() {
        return versionTasks;
    }

    public void setVersionTasks(List<TASK_VERSION> versionTasks) {
        this.versionTasks = versionTasks;
    }

}
