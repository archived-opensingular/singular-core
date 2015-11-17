package br.net.mirante.singular.dao.form;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import br.net.mirante.singular.form.mform.core.attachment.IAttachmentRef;

@Entity
@Table(name = "EXAMPLE_FILE")
public class ExampleFile implements IAttachmentRef{

    @Id String id;
    private String hashSha1;
    @Lob private byte[] rawContent;
    private int size;

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
        ExampleFile other = (ExampleFile) obj;
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
