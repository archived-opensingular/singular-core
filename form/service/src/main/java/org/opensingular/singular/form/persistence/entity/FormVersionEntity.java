package org.opensingular.singular.form.persistence.entity;

import org.opensingular.singular.support.persistence.entity.BaseEntity;
import org.opensingular.singular.support.persistence.util.Constants;
import org.opensingular.singular.support.persistence.util.HybridIdentityOrSequenceGenerator;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@GenericGenerator(name = FormVersionEntity.PK_GENERATOR_NAME, strategy = HybridIdentityOrSequenceGenerator.CLASS_NAME)
@Table(name = "TB_VERSAO_FORMULARIO", schema = Constants.SCHEMA)
public class FormVersionEntity extends BaseEntity<Long> {

    public static final String PK_GENERATOR_NAME = "GENERATED_CO_VERSAO_FORMULARIO";

    @Id
    @Column(name = "CO_VERSAO_FORMULARIO")
    @GeneratedValue(generator = PK_GENERATOR_NAME)
    private Long cod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_FORMULARIO")
    private FormEntity formEntity;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_INCLUSAO")
    private Date inclusionDate;

    @Lob
    @Column(name = "XML_CONTEUDO")
    private String xml;

    @Column(name = "CO_AUTOR_INCLUSAO")
    private Integer inclusionActor;

    @Column(name = "NU_VERSAO_CACHE")
    private Long cacheVersion;

    @OneToMany
    @JoinColumn(referencedColumnName = "CO_VERSAO_FORMULARIO", name = "CO_VERSAO_FORMULARIO", insertable = false, updatable = false)
    private List<FormAnnotationEntity> formAnnotations = new ArrayList<>(0);

    public FormVersionEntity() {
        setInclusionDate(new Date());
    }

    @Override
    public Long getCod() {
        return cod;
    }

    public void setCod(Long cod) {
        this.cod = cod;
    }

    public FormEntity getFormEntity() {
        return formEntity;
    }

    public void setFormEntity(FormEntity formEntity) {
        this.formEntity = formEntity;
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

    public Long getCacheVersion() {
        return cacheVersion;
    }

    public void setCacheVersion(Long cacheVersion) {
        this.cacheVersion = cacheVersion;
    }

    public List<FormAnnotationEntity> getFormAnnotations() {
        return formAnnotations;
    }

    public void setFormAnnotations(List<FormAnnotationEntity> formAnnotations) {
        this.formAnnotations = formAnnotations;
    }

}
