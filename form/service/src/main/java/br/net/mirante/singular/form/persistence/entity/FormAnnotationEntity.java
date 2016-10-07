package br.net.mirante.singular.form.persistence.entity;

import br.net.mirante.singular.support.persistence.entity.BaseEntity;
import br.net.mirante.singular.support.persistence.util.Constants;
import br.net.mirante.singular.support.persistence.util.HybridIdentityOrSequenceGenerator;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@GenericGenerator(name = FormAnnotationEntity.PK_GENERATOR_NAME, strategy = HybridIdentityOrSequenceGenerator.CLASS_NAME)
@Table(name = "TB_ANOTACAO_FORMULARIO", schema = Constants.SCHEMA)
public class FormAnnotationEntity extends BaseEntity<FormAnnotationPK> {

    public static final String PK_GENERATOR_NAME = "GENERATED_CO_ANOTACAO_FORM";

    @EmbeddedId
    private FormAnnotationPK cod;

    @ManyToOne
    @JoinColumn(name = "CO_VERSAO_ANOTACAO_ATUAL")
    private FormAnnotationVersionEntity annotationCurrentVersion;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "formAnnotationEntity")
    private List<FormAnnotationVersionEntity> annotationVersions;

    public static String getPkGeneratorName() {

        return PK_GENERATOR_NAME;
    }

    @Override
    public FormAnnotationPK getCod() {
        return cod;
    }

    public void setCod(FormAnnotationPK cod) {
        this.cod = cod;
    }

    public FormAnnotationVersionEntity getAnnotationCurrentVersion() {
        return annotationCurrentVersion;
    }

    public void setAnnotationCurrentVersion(FormAnnotationVersionEntity annotationCurrentVersion) {
        this.annotationCurrentVersion = annotationCurrentVersion;
    }

    public List<FormAnnotationVersionEntity> getAnnotationVersions() {
        return annotationVersions;
    }

    public void setAnnotationVersions(List<FormAnnotationVersionEntity> annotationVersions) {
        this.annotationVersions = annotationVersions;
    }

    public String getClassifier(){
        return getCod().getClassifier();
    }


}
