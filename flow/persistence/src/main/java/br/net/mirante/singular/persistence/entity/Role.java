package br.net.mirante.singular.persistence.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.net.mirante.singular.flow.core.entity.IEntityProcessDefinition;
import br.net.mirante.singular.flow.core.entity.IEntityProcessRole;
import br.net.mirante.singular.persistence.util.Constants;


/**
 * The persistent class for the TB_PAPEL database table.
 */
@Entity
@Table(name="TB_DEFINICAO_PAPEL", schema = Constants.SCHEMA)
public class Role extends BaseEntity implements IEntityProcessRole {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CO_DEFINICAO_PAPEL")
    private Integer cod;

    @Column(name = "NO_PAPEL", nullable = false)
    private String name;

    @Column(name = "SG_PAPEL", nullable = false)
    private String abbreviation;

    //bi-directional many-to-one association to ProcessDefinition
    @ManyToOne
    @JoinColumn(name = "CO_DEFINICAO_PROCESSO")
    private ProcessDefinition processDefinition;

    public Role() {
    }

    @Override
    public Integer getCod() {
        return this.cod;
    }

    public void setCod(Integer cod) {
        this.cod = cod;
    }


    @Override
    public ProcessDefinition getProcessDefinition() {
        return this.processDefinition;
    }

    @Override
    public void setProcessDefinition(IEntityProcessDefinition processDefinition) {
        setProcessDefinition((ProcessDefinition) processDefinition);
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

}