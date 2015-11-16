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
}
