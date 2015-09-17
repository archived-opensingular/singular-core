package br.net.mirante.singular.persistence.entity;

import br.net.mirante.singular.flow.core.entity.IEntityProcessRole;
import br.net.mirante.singular.persistence.util.Constants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;
import javax.persistence.*;


/**
 * The persistent class for the TB_PAPEL database table.
 */
@Entity
@Table(name="TB_PAPEL", schema = Constants.SCHEMA)
public class Role implements IEntityProcessRole {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CO_PAPEL")
    private Long cod;

    @Column(name = "NO_PAPEL")
    private String name;

    @Column(name = "SG_PAPEL")
    private String abbreviation;

    //bi-directional many-to-one association to ProcessDefinition
    @ManyToOne
    @JoinColumn(name = "CO_DEFINICAO_PROCESSO")
    private ProcessDefinition processDefinition;

    @OneToMany(mappedBy = "role")
    private List<RoleInstance> rolesInstances;

    public Role() {
    }

    public Long getCod() {
        return this.cod;
    }

    public void setCod(Long cod) {
        this.cod = cod;
    }


    public ProcessDefinition getProcessDefinition() {
        return this.processDefinition;
    }

    public void setProcessDefinition(ProcessDefinition processDefinition) {
        this.processDefinition = processDefinition;
    }

    @Override
    public String getAbbreviation() {
        return this.abbreviation;
    }

    @Override
    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public List<RoleInstance> getRolesInstances() {
        return rolesInstances;
    }

    public void setRolesInstances(List<RoleInstance> rolesInstances) {
        this.rolesInstances = rolesInstances;
    }
}