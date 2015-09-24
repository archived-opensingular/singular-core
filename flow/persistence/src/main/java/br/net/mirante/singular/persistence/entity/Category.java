package br.net.mirante.singular.persistence.entity;

import br.net.mirante.singular.flow.core.entity.IEntityCategory;
import br.net.mirante.singular.persistence.util.Constants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;


/**
 * The persistent class for the TB_CATEGORIA database table.
 */
@Entity
@Table(name = "TB_CATEGORIA", schema = Constants.SCHEMA)
public class Category implements IEntityCategory {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CO_CATEGORIA")
    private Long cod;

    @Column(name = "NO_CATEGORIA", nullable = false)
    private String name;

    @OneToMany(mappedBy = "category")
    private List<ProcessDefinition> processDefinitions;

    public Category() {
    }

    public Long getCod() {
        return this.cod;
    }

    public void setCod(Long cod) {
        this.cod = cod;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public List<ProcessDefinition> getProcessDefinitions() {
        return processDefinitions;
    }

    public void setProcessDefinitions(List<ProcessDefinition> processDefinitions) {
        this.processDefinitions = processDefinitions;
    }
}