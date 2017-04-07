package org.opensingular.lib.support.persistence.entityanddao;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.opensingular.lib.support.persistence.entity.BaseEntity;
import org.opensingular.lib.support.persistence.enums.SimNao;
import org.opensingular.lib.support.persistence.util.GenericEnumUserType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
public class TestEntity extends BaseEntity<Integer> {

    public TestEntity(Integer cod, String name, String otherField){
        this.cod = cod;
        this.name = name;
        this.otherField = otherField;
    }

    public TestEntity(){
    }

    @Id
    private Integer cod;

    @Column
    private String name;

    @Column
    private String otherField;

    @Temporal(TemporalType.DATE)
    private Date date;

    @Type(type = GenericEnumUserType.CLASS_NAME, parameters = @Parameter(name = "enumClass", value = SimNao.ENUM_CLASS_NAME))
    @Column(name = "SIM_NAO")
    private SimNao simNaoEnum;

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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public SimNao getSimNaoEnum() {
        return simNaoEnum;
    }

    public void setSimNaoEnum(SimNao simNaoEnum) {
        this.simNaoEnum = simNaoEnum;
    }
}
