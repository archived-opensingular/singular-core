package br.net.mirante.singular.form.persistence.entity;

import br.net.mirante.singular.support.persistence.entity.BaseEntity;
import br.net.mirante.singular.support.persistence.util.Constants;
import br.net.mirante.singular.support.persistence.util.HybridIdentityOrSequenceGenerator;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@GenericGenerator(name = AttachmentEntity.PK_GENERATOR_NAME, strategy = HybridIdentityOrSequenceGenerator.CLASS_NAME)
@Table(name = "TB_ARQUIVO", schema = Constants.SCHEMA)
public class AttachmentEntity extends BaseEntity<Long> {

    public static final String PK_GENERATOR_NAME = "GENERATED_CO_ARQUIVO";

    @Id
    @Column(name = "CO_ARQUIVO")
    @GeneratedValue(generator = PK_GENERATOR_NAME)
    private Long cod;

    @Column(name = "NO_ARQUIVO")
    private String nome;

    @Column(name = "TX_SHA1")
    private String hashSha1;

    @Column(name = "NU_BYTES")
    private long size;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_CRIACAO")
    private Date creationDate;

    @Override
    public Long getCod() {
        return cod;
    }

    public void setCod(Long cod) {
        this.cod = cod;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
