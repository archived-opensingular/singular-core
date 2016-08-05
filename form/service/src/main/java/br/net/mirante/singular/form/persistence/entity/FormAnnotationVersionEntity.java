package br.net.mirante.singular.form.persistence.entity;

import br.net.mirante.singular.support.persistence.entity.BaseEntity;
import br.net.mirante.singular.support.persistence.util.Constants;
import br.net.mirante.singular.support.persistence.util.HybridIdentityOrSequenceGenerator;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@GenericGenerator(name = FormAnnotationVersionEntity.PK_GENERATOR_NAME, strategy = HybridIdentityOrSequenceGenerator.CLASS_NAME)
@Table(name = "TB_VERSAO_ANOTACAO_FORMULARIO", schema = Constants.SCHEMA)
public class FormAnnotationVersionEntity extends BaseEntity<Long> {

    public static final String PK_GENERATOR_NAME = "GENERATED_CO_VERSAO_ANOTACAO_FORM";

    @Id
    @Column(name = "CO_VERSAO_ANOTACAO_FORM")
    @GeneratedValue(generator = PK_GENERATOR_NAME)
    private Long cod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_VERSAO_FORMULARIO")
    private FormVersionEntity formVersionEntity;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_INCLUSAO")
    private Date inclusionDate;

    @Lob
    @Column(name = "XML_ANOTACAO")
    private String xml;

    @Column(name = "CO_AUTOR_INCLUSAO")
    private Integer inclusionActor;

    @Override
    public Long getCod() {
        return cod;
    }

    public void setCod(Long cod) {
        this.cod = cod;
    }

    public FormVersionEntity getFormVersionEntity() {
        return formVersionEntity;
    }

    public void setFormVersionEntity(FormVersionEntity formVersionEntity) {
        this.formVersionEntity = formVersionEntity;
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
