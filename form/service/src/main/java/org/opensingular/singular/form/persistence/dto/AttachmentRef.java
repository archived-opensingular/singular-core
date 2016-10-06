/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.opensingular.singular.form.persistence.dto;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;

import org.apache.commons.io.IOUtils;

import org.opensingular.singular.commons.base.SingularUtil;
import org.opensingular.form.document.SDocument;
import org.opensingular.form.io.CompressionUtil;
import org.opensingular.form.io.IOUtil;
import org.opensingular.singular.form.persistence.entity.AttachmentContentEntitty;
import org.opensingular.singular.form.persistence.entity.AttachmentEntity;
import org.opensingular.singular.form.persistence.service.AttachmentPersistenceService;
import org.opensingular.form.type.core.attachment.IAttachmentRef;
import org.opensingular.singular.support.spring.util.ApplicationContextProvider;

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
    public InputStream getInputStream() throws IOException {
        try {
            if (file == null || !file.exists()) {
                
                AttachmentPersistenceService<AttachmentEntity, AttachmentContentEntitty> persistenceHandler = 
                    ApplicationContextProvider.get().getBean(SDocument.FILE_PERSISTENCE_SERVICE, AttachmentPersistenceService.class);
                
                Blob content = persistenceHandler.loadAttachmentContent(codContent);

                file = File.createTempFile(name, hashSha1 + "."+id);
                file.deleteOnExit();
                
                try (InputStream in = content.getBinaryStream();
                    OutputStream fos = IOUtil.newBuffredOutputStream(file)) {
                    IOUtils.copy(in, fos);
                }
            }
            return CompressionUtil.inflateToInputStream(new FileInputStream(file));
        } catch (Exception e) {
            if(file != null){
                file.delete();
                file = null;
            }
            throw SingularUtil.propagate(e);
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
