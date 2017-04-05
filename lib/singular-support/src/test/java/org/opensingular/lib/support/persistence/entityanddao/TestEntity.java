package org.opensingular.lib.support.persistence.entityanddao;

import org.opensingular.lib.support.persistence.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class TestEntity extends BaseEntity<Integer> {

    public TestEntity(Integer cod, String name, String otherField){
        this.cod = cod;
        this.name = name;
        this.otherField = otherField;
    }

    @Id
    private Integer cod;

    @Column
    private String name;

    @Column
    private String otherField;

    @Override
    public Integer getCod() {
        return cod;
    }

    public void setCod(Integer cod) {
        this.cod = cod;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOtherField() {
        return otherField;
    }

    public void setOtherField(String otherField) {
        this.otherField = otherField;
    }
}
