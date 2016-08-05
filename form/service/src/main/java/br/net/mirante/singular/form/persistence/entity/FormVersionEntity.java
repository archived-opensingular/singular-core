package br.net.mirante.singular.form.persistence.entity;

import br.net.mirante.singular.support.persistence.entity.BaseEntity;
import br.net.mirante.singular.support.persistence.util.Constants;
import br.net.mirante.singular.support.persistence.util.HybridIdentityOrSequenceGenerator;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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

    @OneToMany(mappedBy = "formVersionEntity")
    private List<FormAnnotationVersionEntity> formAnnotationVersionEntities;

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

    public List<FormAnnotationVersionEntity> getFormAnnotationVersionEntities() {
        return formAnnotationVersionEntities;
    }

    public void setFormAnnotationVersionEntities(List<FormAnnotationVersionEntity> formAnnotationVersionEntities) {
        this.formAnnotationVersionEntities = formAnnotationVersionEntities;
    }

    public Optional<FormAnnotationVersionEntity> getLatestFormAnnotationVersionEntity() {
        if (getFormAnnotationVersionEntities() != null) {
            return getFormAnnotationVersionEntities()
                    .stream()
                    .sorted((a, b) -> b.getInclusionDate().compareTo(a.getInclusionDate()))
                    .findFirst();
        }
        return Optional.empty();
    }

}
