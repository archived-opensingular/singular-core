package org.opensingular.form.persistence.entity;

import org.opensingular.singular.support.persistence.entity.BaseEntity;
import org.opensingular.singular.support.persistence.util.Constants;
import org.opensingular.singular.support.persistence.util.HybridIdentityOrSequenceGenerator;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@GenericGenerator(name = FormAnnotationVersionEntity.PK_GENERATOR_NAME, strategy = HybridIdentityOrSequenceGenerator.CLASS_NAME)
@Table(name = "TB_VERSAO_ANOTACAO_FORMULARIO", schema = Constants.SCHEMA)
public class FormAnnotationVersionEntity extends BaseEntity<Long> {

    public static final String PK_GENERATOR_NAME = "GENERATED_CO_VERSAO_ANOTACAO";

    @Id
    @Column(name = "CO_VERSAO_ANOTACAO")
    @GeneratedValue(generator = PK_GENERATOR_NAME)
    private Long cod;

    @ManyToOne
    @JoinColumns(value = {
            @JoinColumn(name = "CO_VERSAO_FORMULARIO", referencedColumnName = "CO_VERSAO_FORMULARIO"),
            @JoinColumn(name = "CO_CHAVE_ANOTACAO", referencedColumnName = "CO_CHAVE_ANOTACAO")
    }
    )
    private FormAnnotationEntity formAnnotationEntity;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_INCLUSAO")
    private Date inclusionDate;

    @Lob
    @Column(name = "XML_ANOTACAO")
    private String xml;

    @Column(name = "CO_AUTOR_INCLUSAO")
    private Integer inclusionActor;

    public FormAnnotationVersionEntity() {
        setInclusionDate(new Date());
    }

    @Override
    public Long getCod() {
        return cod;
    }

    public void setCod(Long cod) {
        this.cod = cod;
    }

    public FormAnnotationEntity getFormAnnotationEntity() {
        return formAnnotationEntity;
    }

    public void setFormAnnotationEntity(FormAnnotationEntity formAnnotationEntity) {
        this.formAnnotationEntity = formAnnotationEntity;
    }

    public Date getInclusionDate() {
        return inclusionDate;
    }

    public void setInclusionDate(Date inclusionDate) {
        this.inclusionDate = inclusionDate;
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    public Integer getInclusionActor() {
        return inclusionActor;
    }

    public void setInclusionActor(Integer inclusionActor) {
        this.inclusionActor = inclusionActor;
    }
}
