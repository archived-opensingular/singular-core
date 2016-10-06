package org.opensingular.singular.server.commons.persistence.entity.form;


import org.opensingular.form.persistence.entity.FormEntity;
import org.opensingular.flow.persistence.entity.TaskDefinitionEntity;
import org.opensingular.lib.support.persistence.entity.BaseEntity;
import org.opensingular.lib.support.persistence.enums.SimNao;
import org.opensingular.lib.support.persistence.util.Constants;
import org.opensingular.lib.support.persistence.util.GenericEnumUserType;
import org.opensingular.lib.support.persistence.util.HybridIdentityOrSequenceGenerator;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Optional;

@Entity
@Table(schema = Constants.SCHEMA, name = "TB_FORMULARIO_PETICAO")
@GenericGenerator(name = FormPetitionEntity.PK_GENERATOR_NAME, strategy = HybridIdentityOrSequenceGenerator.CLASS_NAME)
public class FormPetitionEntity extends BaseEntity<Long> implements Comparable<FormPetitionEntity> {

    public static final String PK_GENERATOR_NAME = "GENERATED_CO_FORMULARIO_PETICAO";

    @Id
    @Column(name = "CO_FORMULARIO_PETICAO")
    @GeneratedValue(generator = PK_GENERATOR_NAME)
    private Long cod;

    @ManyToOne
    @JoinColumn(name = "CO_PETICAO")
    private PetitionEntity petition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_FORMULARIO")
    private FormEntity form;

    @Column(name = "ST_FORM_PRINCIPAL", length = 1)
    @Type(type = GenericEnumUserType.CLASS_NAME, parameters = {
            @org.hibernate.annotations.Parameter(name = "enumClass", value = SimNao.ENUM_CLASS_NAME),
            @org.hibernate.annotations.Parameter(name = "identifierMethod", value = "getCodigo"),
            @org.hibernate.annotations.Parameter(name = "valueOfMethod", value = "valueOfEnum")})
    private SimNao mainForm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_DEFINICAO_TAREFA")
    private TaskDefinitionEntity taskDefinitionEntity;

    @Override
    public Long getCod() {
        return cod;
    }

    public void setCod(Long cod) {
        this.cod = cod;
    }

    public PetitionEntity getPetition() {
        return petition;
    }

    public void setPetition(PetitionEntity petition) {
        this.petition = petition;
    }

    public FormEntity getForm() {
        return form;
    }

    public void setForm(FormEntity form) {
        this.form = form;
    }

    public SimNao getMainForm() {
        return mainForm;
    }

    public void setMainForm(SimNao mainForm) {
        this.mainForm = mainForm;
    }

    public TaskDefinitionEntity getTaskDefinitionEntity() {
        return taskDefinitionEntity;
    }

    public void setTaskDefinitionEntity(TaskDefinitionEntity taskDefinitionEntity) {
        this.taskDefinitionEntity = taskDefinitionEntity;
    }

    @Override
    public int compareTo(FormPetitionEntity o) {
        return Optional.ofNullable(this.getCod()).orElse(0l).compareTo(Optional.ofNullable(o).map(BaseEntity::getCod).orElse(0l));
    }
}