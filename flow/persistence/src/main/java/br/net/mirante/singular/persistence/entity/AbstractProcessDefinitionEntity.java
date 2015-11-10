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
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import br.net.mirante.singular.flow.core.entity.IEntityCategory;
import br.net.mirante.singular.flow.core.entity.IEntityProcessDefinition;
import br.net.mirante.singular.flow.core.entity.IEntityProcessGroup;
import br.net.mirante.singular.flow.core.entity.IEntityProcessVersion;
import br.net.mirante.singular.flow.core.entity.IEntityRoleDefinition;
import br.net.mirante.singular.flow.core.entity.IEntityTaskDefinition;

/**
 * The base persistent class for the TB_DEFINICAO_PROCESSO database table.
 * <p>
 * Must declare a {@link GenericGenerator} with name {@link AbstractProcessDefinitionEntity#PK_GENERATOR_NAME}.
 * </p>
 * <code>@GenericGenerator(name = AbstractProcessDefinitionEntity.PK_GENERATOR_NAME, strategy = "org.hibernate.id.IdentityGenerator")</code>
 *
 * @param <IEntityProcessGroup>
 * @param <IEntityCategory>
 * @param <IEntityTaskDefinition>
 * @param <IEntityRoleDefinition>
 * @param <IEntityProcessVersion>
 */
@MappedSuperclass
@Table(name = "TB_DEFINICAO_PROCESSO")
public abstract class AbstractProcessDefinitionEntity<GROUP extends IEntityProcessGroup, CATEGORY extends IEntityCategory, TASK_DEF extends IEntityTaskDefinition, ROLE_DEF extends IEntityRoleDefinition, PROCESS_VERSION extends IEntityProcessVersion> extends BaseEntity<Integer> implements IEntityProcessDefinition {

    public static final String PK_GENERATOR_NAME = "GENERATED_CO_DEFINICAO_PROCESSO";

    @Id
    @Column(name = "CO_DEFINICAO_PROCESSO")
    @GeneratedValue(generator = PK_GENERATOR_NAME)
    private Integer cod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_CATEGORIA")
    private CATEGORY category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_GRUPO_PROCESSO", nullable = false)
    private GROUP processGroup;

    @Column(name = "SG_PROCESSO", length = 200, nullable = false, unique = true)
    private String key;

    @Column(name = "NO_PROCESSO", length = 200, nullable = false)
    private String name;

    @Column(name = "NO_CLASSE_JAVA", length = 250, nullable = false, unique = true)
    private String definitionClassName;

    @OneToMany(mappedBy = "processDefinition", fetch = FetchType.LAZY)
    private List<TASK_DEF> taskDefinitions;

    @OrderBy("NO_PAPEL")
    @OneToMany(mappedBy = "processDefinition", fetch = FetchType.LAZY)
    private List<ROLE_DEF> roles = new ArrayList<>();

    @OneToMany(mappedBy = "processDefinition", fetch = FetchType.LAZY)
    private List<PROCESS_VERSION> versions = new ArrayList<>();

    public Integer getCod() {
        return cod;
    }

    public void setCod(Integer cod) {
        this.cod = cod;
    }

    public CATEGORY getCategory() {
        return category;
    }

    public void setCategory(IEntityCategory category) {
        this.category = (CATEGORY) category;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDefinitionClassName() {
        return definitionClassName;
    }

    public void setDefinitionClassName(String definitionClassName) {
        this.definitionClassName = definitionClassName;
    }

    public List<TASK_DEF> getTaskDefinitions() {
        return taskDefinitions;
    }

    public void setTaskDefinitions(List<TASK_DEF> taskDefinitions) {
        this.taskDefinitions = taskDefinitions;
    }

    public List<ROLE_DEF> getRoles() {
        return roles;
    }

    public void setRoles(List<ROLE_DEF> roles) {
        this.roles = roles;
    }

    public List<PROCESS_VERSION> getVersions() {
        return versions;
    }

    public void setVersions(List<PROCESS_VERSION> versions) {
        this.versions = versions;
    }

    public GROUP getProcessGroup() {
        return processGroup;
    }

    public void setProcessGroup(IEntityProcessGroup processGroup) {
        this.processGroup = (GROUP) processGroup;
    }

    
}
