/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.form.type.core.attachment;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SingularFormException;
import org.opensingular.lib.commons.base.SingularUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class SIAttachment extends SIComposite {

    private AttachmentDocumentService getAttachmentService() {
        return AttachmentDocumentService.lookup(this);
    }

    public void setContent(String name, File file, long length) {
        if (file == null) {
            throw new SingularFormException("O arquivo não pode ser nulo.");
        }
        setContent(name, getAttachmentService().addContent(getFileId(), file, length, name, getDocument()));
    }

    private void setContent(String name, IAttachmentRef ref) {
        setFileId(ref.getId());
        setFileHashSHA1(ref.getHashSHA1());
        setFileSize(ref.getSize());
        setFileName(name);
    }

    void deleteReference() {
        if (getFileId() != null) {
            getAttachmentService().deleteReference(getFileId(), getDocument());
        }
        setValue(STypeAttachment.FIELD_FILE_ID, null);
        setValue(STypeAttachment.FIELD_HASH_SHA1, null);
        setValue(STypeAttachment.FIELD_SIZE, null);
        setValue(STypeAttachment.FIELD_NAME, null);
        if (hasAttribute(STypeAttachment.ATR_ORIGINAL_ID)) {
            setAttributeValue(STypeAttachment.ATR_ORIGINAL_ID, null);
        }
        if (hasAttribute(STypeAttachment.ATR_IS_TEMPORARY)) {
            setAttributeValue(STypeAttachment.ATR_IS_TEMPORARY, null);
        }
    }

    @Override
    public void clearInstance() {
        deleteReference();
        super.clearInstance();
    }

    @Override
    protected void onRemove() {
        deleteReference();
        super.onRemove();
    }

    public IAttachmentRef getAttachmentRef() {
        IAttachmentRef ref = null;
        if (getDocument().isAttachmentPersistenceTemporaryHandlerSupported()) {
            ref = getDocument().getAttachmentPersistenceTemporaryHandler().getAttachment(getFileId());
        }
        if (ref == null && getDocument().isAttachmentPersistencePermanentHandlerSupported()) {
            ref = getDocument().getAttachmentPersistencePermanentHandler().getAttachment(getFileId());
        }
        return ref;
    }

    /**
     * Retorna o tamanho do arquivo binário associado ou -1 se não houver
     * arquivo.
     */
    public long getFileSize() {
        return Optional.ofNullable(getValueLong(STypeAttachment.FIELD_SIZE)).orElse(-1L);
    }

    public void setFileSize(long size) {
        setValue(STypeAttachment.FIELD_SIZE, size);
    }

    public String getFileName() {
        return getValueString(STypeAttachment.FIELD_NAME);
    }

    public void setFileName(String fileName) {
        setValue(STypeAttachment.FIELD_NAME, fileName);
    }

    public String getFileId() {
        String id = getValueString(STypeAttachment.FIELD_FILE_ID);
        if (id == null) {
            return getFileHashSHA1();
        }
        return id;
    }

    public void setFileId(String id) {
        setValue(STypeAttachment.FIELD_FILE_ID, id);
    }

    public String getOriginalFileId() {
        return getAttributeValue(STypeAttachment.ATR_ORIGINAL_ID);
    }

    public void setOriginalFileId(String id) {
        setAttributeValue(STypeAttachment.ATR_ORIGINAL_ID, id);
    }

    public String getFileHashSHA1() {
        return getValueString(STypeAttachment.FIELD_HASH_SHA1);
    }

    public void setFileHashSHA1(String hash) {
        setValue(STypeAttachment.FIELD_HASH_SHA1, hash);
    }

    public InputStream newInputStream() {
        try {
            IAttachmentRef ref = getAttachmentRef();
            return ref == null ? null : ref.getInputStream();
        } catch (IOException e) {
            throw SingularUtil.propagate(e);
        }
    }

    public String fileSizeToString() {
        if (getFileSize() <= 0) {
            return "";
        }
        final String[] sufix = new String[]{"B", "KB", "MB", "GB"};
        int posSufix = 0;
        double bytesSize = getFileSize();

        while (bytesSize > 900 && posSufix < sufix.length - 1) {
            bytesSize = bytesSize / 1024;
            posSufix++;
        }

        return Math.round(bytesSize) + " " + sufix[posSufix];
    }

    @Override
    public String toStringDisplayDefault() {
        if (getFileSize() <= 0 || getFileName() == null) {
            return super.toStringDisplayDefault();
        }
        return getFileName() + " (" + fileSizeToString() + ")";
    }

    public void update(IAttachmentRef ref) {
        this.setFileName(ref.getName());
        this.setFileId(ref.getId());
        this.setFileHashSHA1(ref.getHashSHA1());
        this.setFileSize(ref.getSize());
    }

}
