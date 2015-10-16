package br.net.mirante.singular.persistence.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import br.net.mirante.singular.flow.core.entity.IEntityCategory;
import br.net.mirante.singular.persistence.util.Constants;


/**
 * The persistent class for the TB_CATEGORIA database table.
 */
@Entity
@Table(name = "TB_CATEGORIA", schema = Constants.SCHEMA)
public class CategoryEntity extends BaseEntity implements IEntityCategory {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CO_CATEGORIA")
    private Integer cod;

    @Column(name = "NO_CATEGORIA", nullable = false)
    private String name;

    public CategoryEntity() {
    }

    @Override
    public Integer getCod() {
        return this.cod;
    }

    public void setCod(Integer cod) {
        this.cod = cod;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}