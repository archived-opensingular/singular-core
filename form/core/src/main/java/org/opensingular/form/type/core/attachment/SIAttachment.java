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
import org.opensingular.internal.lib.commons.util.SingularIOUtils;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.InputStream;
import java.util.Optional;

public class SIAttachment extends SIComposite {

    private AttachmentDocumentService getAttachmentService() {
        return AttachmentDocumentService.lookup(this);
    }

    public void setContent(String name, File file, long length, String hashSha1) {
        if (file == null) {
            throw new SingularFormException("O arquivo não pode ser nulo.", this);
        }
        setContent(name, getAttachmentService().addContent(getFileId(), file, length, name, hashSha1, getDocument()));
    }

    private void setContent(String name, IAttachmentRef ref) {
        setFileId(ref.getId());
        setFileHashSHA1(ref.getHashSHA1());
        setFileSize(ref.getSize());
        setFileName(name);
    }

    void deleteReference() {
        String fileId = getFileId();
        if (fileId != null) {
            getAttachmentService().deleteReference(fileId, getDocument());
        }
        setValue(STypeAttachment.FIELD_FILE_ID, null);
        setValue(STypeAttachment.FIELD_HASH_SHA1, null);
        setValue(STypeAttachment.FIELD_FILE_SIZE, null);
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
        if (getDocument().isAttachmentPersistenceTemporaryHandlerSupported()) {
            IAttachmentRef ref = getDocument().getAttachmentPersistenceTemporaryHandler().getAttachment(getFileId());
            if (ref != null) {
                return ref;
            }
        }
        return getDocument().getAttachmentPersistencePermanentHandler()
                .map(h -> h.getAttachment(getFileId())).orElse(null);
    }

    /**
     * Retorna o tamanho do arquivo binário associado ou -1 se não houver
     * arquivo.
     */
    public long getFileSize() {
        return Optional.ofNullable(getValueLong(STypeAttachment.FIELD_FILE_SIZE)).orElse(-1L);
    }

    public void setFileSize(long size) {
        setValue(STypeAttachment.FIELD_FILE_SIZE, size);
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

    /**
     * Retorna o conteúdo do anexo como uma InputStream se existir.
     * @see IAttachmentRef#getContentAsInputStream()
     */
    @Nonnull
    public Optional<InputStream> getContentAsInputStream() {
        IAttachmentRef ref = getAttachmentRef();
        return ref == null ? Optional.empty(): Optional.of(ref.getContentAsInputStream());
    }

    /**
     * Retorna o conteúdo do anexo como um array de bytes.
     * <b>ATENÇÂO: DEVE SER PREFERENCIALMENTE USADO {@link #getContentAsInputStream()}</b> se
     * há expectativa de manipular arquivos de grande tamanho.
     * @see IAttachmentRef#getContentAsByteArray()
     */
    @Nonnull
    public Optional<byte[]> getContentAsByteArray() {
        IAttachmentRef ref = getAttachmentRef();
        return ref == null ? Optional.empty(): Optional.of(ref.getContentAsByteArray());
    }

    public String fileSizeToString() {
        long size = getFileSize();
        return size <= 0 ? "" : SingularIOUtils.humanReadableByteCountRound(getFileSize());
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
