/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package br.net.mirante.singular.form.persistence.dto;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;

import org.apache.commons.io.IOUtils;

import br.net.mirante.singular.commons.base.SingularException;
import br.net.mirante.singular.form.document.SDocument;
import br.net.mirante.singular.form.io.CompressionUtil;
import br.net.mirante.singular.form.io.IOUtil;
import br.net.mirante.singular.form.persistence.entity.AttachmentContentEntitty;
import br.net.mirante.singular.form.persistence.entity.AttachmentEntity;
import br.net.mirante.singular.form.persistence.service.AttachmentPersistenceService;
import br.net.mirante.singular.form.type.core.attachment.IAttachmentRef;
import br.net.mirante.singular.support.spring.util.ApplicationContextProvider;

public class AttachmentRef implements IAttachmentRef{

    private final String id;
    
    private final Long codContent;

    private final String hashSha1;

    private final long size;
    
    private final String name;

    private File file;
    
    public AttachmentRef(AttachmentEntity attachmentEntity) {
        this(attachmentEntity.getCod().toString(), attachmentEntity.getCodContent(), attachmentEntity.getHashSha1(), attachmentEntity.getSize(), attachmentEntity.getName());
    }
    
    public AttachmentRef(String id, Long codContent, String hashSha1, long size, String name) {
        super();
        this.id = id;
        this.codContent = codContent;
        this.hashSha1 = hashSha1;
        this.size = size;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getHashSHA1() {
        return hashSha1;
    }

    public long getSize() {
        return size;
    }

    public String getName() {
        return name;
    }
    
    public Long getCodContent() {
        return codContent;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public InputStream newInputStream() {
        try {
            if (file == null || !file.exists()) {
                
                AttachmentPersistenceService<AttachmentEntity, AttachmentContentEntitty> persistenceHandler = 
                    ApplicationContextProvider.get().getBean(SDocument.FILE_PERSISTENCE_SERVICE, AttachmentPersistenceService.class);
                
                Blob content = persistenceHandler.loadAttachmentContent(codContent);

                file = File.createTempFile(name, hashSha1);
                file.deleteOnExit();
                
                try (InputStream in = content.getBinaryStream();
                    OutputStream fos = IOUtil.newBuffredOutputStream(file)) {
                    IOUtils.copy(in, fos);
                }
            }
            return CompressionUtil.inflateToInputStream(new FileInputStream(file));
        } catch (IOException | SQLException e) {
            if(file != null){
                file.delete();
                file = null;
            }
            throw new SingularException(e);
        }
    }
    
    public int hashCode() {
        String cod = getId();
        return (cod == null) ? super.hashCode() : cod.hashCode();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AttachmentRef)) {
            return false;
        }
        AttachmentRef other = (AttachmentRef) obj;
        if (!((getId() == other.getId()) || (getId() != null && getId().equals(other.getId())))) {
            return false;
        }
        return true;
    }
}
