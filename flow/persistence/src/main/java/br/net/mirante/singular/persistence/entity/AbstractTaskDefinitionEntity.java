package br.net.mirante.singular.persistence.entity;

import java.util.ArrayList;
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

import org.hibernate.annotations.GenericGenerator;

import br.net.mirante.singular.flow.core.entity.IEntityProcessDefinition;
import br.net.mirante.singular.flow.core.entity.IEntityTaskDefinition;
import br.net.mirante.singular.flow.core.entity.IEntityTaskVersion;

/**
 * The base persistent class for the TB_DEFINICAO_TAREFA database table.
 * <p>
 * Must declare a {@link GenericGenerator} with name
 * {@link AbstractTaskDefinitionEntity#PK_GENERATOR_NAME}.
 * </p>
 * <code>@GenericGenerator(name = AbstractTaskDefinitionEntity.PK_GENERATOR_NAME, strategy = "org.hibernate.id.IdentityGenerator")</code>
 *
 * @param <PROCESS_DEF>
 * @param <TASK_VERSION>
 */
@MappedSuperclass
@Table(name = "TB_DEFINICAO_TAREFA")
public abstract class AbstractTaskDefinitionEntity<PROCESS_DEF extends IEntityProcessDefinition, TASK_VERSION extends IEntityTaskVersion> extends BaseEntity implements IEntityTaskDefinition {

    public static final String PK_GENERATOR_NAME = "GENERATED_CO_DEFINICAO_TAREFA";

    @Id
    @Column(name = "CO_DEFINICAO_TAREFA")
    @GeneratedValue(generator = PK_GENERATOR_NAME)
    private Integer cod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_DEFINICAO_PROCESSO", nullable = false)
    private PROCESS_DEF processDefinition;

    @Column(name = "SG_TAREFA", length = 100, nullable = false)
    private String abbreviation;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "taskDefinition")
    private List<TASK_VERSION> versions = new ArrayList<>();

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

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public List<TASK_VERSION> getVersions() {
        return versions;
    }

    public void setVersions(List<TASK_VERSION> versions) {
        this.versions = versions;
    }

}
