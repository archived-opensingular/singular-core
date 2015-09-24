package br.net.mirante.singular.persistence.entity;

import br.net.mirante.singular.flow.core.entity.IEntityProcessDefinition;
import br.net.mirante.singular.persistence.util.Constants;

import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the TB_DEFINICAO_PROCESSO database table.
 * 
 */
@Entity
@Table(name="TB_DEFINICAO_PROCESSO", schema = Constants.SCHEMA)
public class ProcessDefinition implements IEntityProcessDefinition {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="CO_DEFINICAO_PROCESSO")
	private Long cod;

	@Column(name="NO_CLASSE_JAVA", nullable = false)
	private String definitionClassName;

	@Column(name="NO_PROCESSO", nullable = false)
	private String name;

	@Column(name="SG_PROCESSO", nullable = false)
	private String abbreviation;

	//bi-directional many-to-one association to ProcessRight
	@OneToMany(mappedBy="processDefinition")
	private List<ProcessRight> processRights;

	//uni-directional many-to-one association to Category
	@ManyToOne
	@JoinColumn(name="CO_CATEGORA")
	private Category category;

	//bi-directional many-to-one association to TaskDefinition
	@OneToMany(mappedBy="processDefinition")
	private List<TaskDefinition> taskDefinitions;

	//bi-directional many-to-one association to Role
	@OneToMany(mappedBy="processDefinition")
	private List<Role> roles;

    //bi-directional many-to-one association to Role
    @OneToMany(mappedBy="processDefinition")
    private List<Process> versions;

	public ProcessDefinition() {
	}

	public Long getCod() {
		return this.cod;
	}

	public void setCod(Long cod) {
		this.cod = cod;
	}

    @Override
    public String getDefinitionClassName() {
        return definitionClassName;
    }

    public void setDefinitionClassName(String definitionClassName) {
        this.definitionClassName = definitionClassName;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getAbbreviation() {
        return abbreviation;
    }

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
    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public List<TaskDefinition> getTaskDefinitions() {
        return taskDefinitions;
    }

    public void setTaskDefinitions(List<TaskDefinition> taskDefinitions) {
        this.taskDefinitions = taskDefinitions;
    }

    @Override
    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    @Override
    public List<Process> getVersions() {
        return versions;
    }

    public void setVersions(List<Process> versions) {
        this.versions = versions;
    }
}