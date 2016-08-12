package br.net.mirante.singular.form.persistence.entity;

import br.net.mirante.singular.support.persistence.entity.BaseEntity;
import br.net.mirante.singular.support.persistence.util.Constants;

import javax.persistence.*;
import java.sql.Blob;
import java.util.Date;

@Entity
@Table(name = "TB_CONTEUDO_ARQUIVO", schema = Constants.SCHEMA)
public class AttachmentContentEntitty extends BaseEntity<String> {

    @Id
    @Column(name = "TX_SHA1")
    private String hashSha1;

    @Column(name = "NU_BYTES")
    private long size;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_INCLUSAO")
    private Date inclusionDate;

    @Lob
    @Column(name = "BL_CONTEUDO")
    private Blob content;

    @Override
    public String getCod() {
        return getHashSha1();
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