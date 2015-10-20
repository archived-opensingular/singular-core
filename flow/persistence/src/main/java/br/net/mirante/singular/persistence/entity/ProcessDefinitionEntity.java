package br.net.mirante.singular.persistence.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import br.net.mirante.singular.flow.core.entity.IEntityCategory;
import br.net.mirante.singular.flow.core.entity.IEntityProcessDefinition;
import br.net.mirante.singular.persistence.util.Constants;
import br.net.mirante.singular.persistence.util.HybridIdentityOrSequenceGenerator;
import org.hibernate.annotations.GenericGenerator;

/**
 * The persistent class for the TB_DEFINICAO_PROCESSO database table.
 */
@Entity
@Table(name = "TB_DEFINICAO_PROCESSO", schema = Constants.SCHEMA)
public class ProcessDefinitionEntity extends BaseEntity implements IEntityProcessDefinition {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "singular")
    @GenericGenerator(name = "singular", strategy = HybridIdentityOrSequenceGenerator.CLASS_NAME)
    @Column(name = "CO_DEFINICAO_PROCESSO")
    private Integer cod;

    @Column(name = "NO_CLASSE_JAVA", nullable = false)
    private String definitionClassName;

    @Column(name = "NO_PROCESSO", nullable = false)
    private String name;

    @Column(name = "SG_PROCESSO", nullable = false)
    private String abbreviation;

    //bi-directional many-to-one association to ProcessRight
    @OneToMany(mappedBy = "processDefinition")
    private List<ProcessRight> processRights;

    //uni-directional many-to-one association to Category
    @ManyToOne
    @JoinColumn(name = "CO_CATEGORA")
    private CategoryEntity category;

    //bi-directional many-to-one association to TaskDefinition
    @OneToMany(mappedBy = "processDefinition")
    private List<TaskDefinitionEntity> taskDefinitions;

    //bi-directional many-to-one association to Role
    @OneToMany(mappedBy = "processDefinition")
    private List<RoleDefinitionEntity> roles;

    //bi-directional many-to-one association to Role
    @OneToMany(mappedBy = "processDefinition")
    private List<ProcessVersionEntity> versions;

    public ProcessDefinitionEntity() {
    }

    @Override
    public Integer getCod() {
        return this.cod;
    }

    public void setCod(Integer cod) {
        this.cod = cod;
    }

    @Override
    public String getDefinitionClassName() {
        return definitionClassName;
    }

    @Override
    public void setDefinitionClassName(String definitionClassName) {
        this.definitionClassName = definitionClassName;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getAbbreviation() {
        return abbreviation;
    }

    @Override
    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public List<ProcessRight> getProcessRights() {
        return processRights;
    }

    public void setProcessRights(List<ProcessRight> processRights) {
        this.processRights = processRights;
    }

    @Override
    public CategoryEntity getCategory() {
        return category;
    }

    @Override
    public void setCategory(IEntityCategory category) {
        setCategory((CategoryEntity) category);
    }

    public void setCategory(CategoryEntity category) {
        this.category = category;
    }

    @Override
    public List<TaskDefinitionEntity> getTaskDefinitions() {
        return taskDefinitions;
    }

    public void setTaskDefinitions(List<TaskDefinitionEntity> taskDefinitions) {
        this.taskDefinitions = taskDefinitions;
    }

    @Override
    public List<RoleDefinitionEntity> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleDefinitionEntity> roles) {
        this.roles = roles;
    }

    @Override
    public List<ProcessVersionEntity> getVersions() {
        return versions;
    }

    public void setVersions(List<ProcessVersionEntity> versions) {
        this.versions = versions;
    }
}