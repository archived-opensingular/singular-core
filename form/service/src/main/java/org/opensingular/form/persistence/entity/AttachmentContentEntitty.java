package org.opensingular.form.persistence.entity;

import java.sql.Blob;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;

import org.opensingular.lib.support.persistence.entity.BaseEntity;
import org.opensingular.lib.support.persistence.util.Constants;
import org.opensingular.lib.support.persistence.util.HybridIdentityOrSequenceGenerator;

@Entity
@GenericGenerator(name = AttachmentContentEntitty.PK_GENERATOR_NAME, strategy = HybridIdentityOrSequenceGenerator.CLASS_NAME)
@Table(name = "TB_CONTEUDO_ARQUIVO", schema = Constants.SCHEMA)
public class AttachmentContentEntitty extends BaseEntity<Long> {

    public static final String PK_GENERATOR_NAME = "GENERATED_CO_CONTEUDO_ARQUIVO";

    @Id
    @GeneratedValue(generator = PK_GENERATOR_NAME)
    @Column(name = "CO_CONTEUDO_ARQUIVO")
    private Long cod;

    @Column(name = "TX_SHA1", nullable = false)
    private String hashSha1;

    @Column(name = "NU_BYTES")
    private long size;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_INCLUSAO", nullable = false)
    private Date inclusionDate;

    @Lob
    @Column(name = "BL_CONTEUDO", nullable = false)
    private Blob content;

    public Long getCod() {
        return cod;
    }

    public void setCod(Long cod) {
        this.cod = cod;
    }

    public String getHashSha1() {
        return hashSha1;
    }

    public void setHashSha1(String hashSha1) {
        this.hashSha1 = hashSha1;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public Date getInclusionDate() {
        return inclusionDate;
    }

    public void setInclusionDate(Date inclusionDate) {
        this.inclusionDate = inclusionDate;
    }

    public Blob getContent() {
        return content;
    }

    public void setContent(Blob content) {
        this.content = content;
    }

}
