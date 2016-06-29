/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.persistence.entity;

import br.net.mirante.singular.commons.base.SingularException;
import br.net.mirante.singular.form.io.CompressionUtil;
import br.net.mirante.singular.form.io.IOUtil;
import br.net.mirante.singular.form.type.core.attachment.IAttachmentRef;
import br.net.mirante.singular.support.persistence.entity.BaseEntity;
import br.net.mirante.singular.support.persistence.util.Constants;
import org.apache.commons.io.IOUtils;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;

@MappedSuperclass
@Table(schema = Constants.SCHEMA, name = "TB_ARQUIVO_PETICAO")
public class AbstractAttachmentEntity extends BaseEntity<String> implements IAttachmentRef {

    @Id
    @Column(name = "CO_ARQUIVO_PETICAO")
    private String id;

    @Column(name = "DS_SHA1", nullable = false)
    private String hashSha1;

    @Lob
    @Column(name = "BL_ARQUIVO_PETICAO", nullable = false)
    private Blob rawContent;

    @Column(name = "NU_TAMANHO", nullable = false)
    private long size;

    private transient File f;

    public AbstractAttachmentEntity() {
    }

    public AbstractAttachmentEntity(String id) {
        this.id = id;
    }

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

    public Blob getRawContent() {
        return rawContent;
    }

    public void setRawContent(Blob rawContent) {
        this.rawContent = rawContent;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public String getHasSHA1() {
        return hashSha1;
    }

    @Override
    public InputStream newInputStream() {
        try {
            if (f == null) {
                f = File.createTempFile(id, hashSha1);
                f.deleteOnExit();
                try (InputStream in = rawContent.getBinaryStream();
                     OutputStream fos = IOUtil.newBuffredOutputStream(f)) {
                    IOUtils.copy(in, fos);
                }
            }
            return CompressionUtil.inflateToInputStream(new FileInputStream(f));
        } catch (IOException | SQLException e) {
            throw new SingularException(e);
        }
    }

    public void deleteTempFile() {
        if (f != null && f.exists()) {
            f.delete();
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((hashSha1 == null) ? 0 : hashSha1.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = (int) (prime * result + size);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        AbstractAttachmentEntity other = (AbstractAttachmentEntity) obj;
        if (hashSha1 == null) {
            if (other.hashSha1 != null) return false;
        } else if (!hashSha1.equals(other.hashSha1)) return false;
        if (id == null) {
            if (other.id != null) return false;
        } else if (!id.equals(other.id)) return false;
        if (size != other.size) return false;
        return true;
    }


}
