package br.net.mirante.singular.server.commons.persistence.entity.form;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;

import br.net.mirante.singular.form.mform.core.attachment.IAttachmentRef;
import br.net.mirante.singular.persistence.entity.BaseEntity;
import br.net.mirante.singular.persistence.util.Constants;

@MappedSuperclass
@Table(schema = Constants.SCHEMA, name = "TB_ARQUIVO_PETICAO")
public class AbstractAttachmentEntity extends BaseEntity<String> implements IAttachmentRef {

    @Id
    @Column(name = "CO_ARQUIVO_PETICAO")
    private String id;

    @Column(name = "DS_SHA1")
    private String hashSha1;

    @Lob
    @Column(name = "BL_ARQUIVO_PETICAO")
    private byte[] rawContent;

    @Column(name = "NU_TAMANHO")
    private int size;
    
    public AbstractAttachmentEntity() {}
    
    public AbstractAttachmentEntity(String id) { this.id = id;   }

    @Override
    public String getCod() {
        return getId();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHashSha1() {
        return hashSha1;
    }

    public void setHashSha1(String hashSha1) {
        this.hashSha1 = hashSha1;
    }

    public byte[] getRawContent() {
        return rawContent;
    }

    public void setRawContent(byte[] rawContent) {
        this.rawContent = rawContent;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public String getHashSHA1() {
        return hashSha1;
    }

    @Override
    public InputStream getContent() {
        return new ByteArrayInputStream(rawContent);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((hashSha1 == null) ? 0 : hashSha1.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + size;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)    return true;
        if (obj == null)    return false;
        if (getClass() != obj.getClass())   return false;
        AbstractAttachmentEntity other = (AbstractAttachmentEntity) obj;
        if (hashSha1 == null) {
            if (other.hashSha1 != null) return false;
        } else if (!hashSha1.equals(other.hashSha1))    return false;
        if (id == null) {
            if (other.id != null)   return false;
        } else if (!id.equals(other.id))    return false;
        if (size != other.size) return false;
        return true;
    }
    

}
