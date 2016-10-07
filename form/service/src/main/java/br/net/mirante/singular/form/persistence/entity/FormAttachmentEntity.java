package br.net.mirante.singular.form.persistence.entity;


import br.net.mirante.singular.support.persistence.entity.BaseEntity;
import br.net.mirante.singular.support.persistence.util.Constants;
import br.net.mirante.singular.support.persistence.util.HybridIdentityOrSequenceGenerator;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@GenericGenerator(name = FormAttachmentEntity.PK_GENERATOR_NAME, strategy = HybridIdentityOrSequenceGenerator.CLASS_NAME)
@Table(name = "TB_ANEXO_FORMULARIO", schema = Constants.SCHEMA)
public class FormAttachmentEntity extends BaseEntity<Long> {

    public static final String PK_GENERATOR_NAME = "GENERATED_CO_ANEXO_FORMULARIO";

    @Id
    @Column(name = "CO_ANEXO_FORMULARIO")
    @GeneratedValue(generator = PK_GENERATOR_NAME)
    private Long cod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_VERSAO_FORMULARIO")
    private FormVersionEntity formVersionEntity;

    @Column(name = "TX_SHA1", nullable = false)
    private String hashSha1;

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

    public String getHashSha1() {
        return hashSha1;
    }

    public void setHashSha1(String hashSha1) {
        this.hashSha1 = hashSha1;
    }

}