/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.type.core.attachment;

import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SingularFormException;
import org.apache.tika.Tika;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

public class SIAttachment extends SIComposite {

    private AttachmentDocumentService getAttachmentService() {
        return AttachmentDocumentService.lookup(this);
    }

    public void setContent(String name, File f, long length) {
        if (f == null){
            throw new SingularFormException("O arquivo não pode ser nulo.");
        }
        setContent(name, getAttachmentService().addContent(getFileId(), f, length));
    }

    private void setContent(String name, IAttachmentRef ref) {
        setFileId(ref.getId());
        setFileHashSHA1(ref.getHasSHA1());
        setFileSize(ref.getSize());
        setFileName(name);
    }


    void deleteReference() {
        if (getFileId() != null) {
            getAttachmentService().deleteReference(getFileId());
        }
        setValue(STypeAttachment.FIELD_FILE_ID, null);
        setValue(STypeAttachment.FIELD_HASH_SHA1, null);
        setValue(STypeAttachment.FIELD_SIZE, null);
        setValue(STypeAttachment.FIELD_NAME, null);
        setAttributeValue(STypeAttachment.ATR_ORIGINAL_ID, null);
        setAttributeValue(STypeAttachment.ATR_IS_TEMPORARY, null);
    }

    @Override
    protected void onRemove() {
        deleteReference();
        super.onRemove();
    }

    IAttachmentRef getAttachmentRef() {
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
        return Optional.ofNullable(getValueLong(STypeAttachment.FIELD_SIZE)).orElse(-1l);
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
        IAttachmentRef ref = getAttachmentRef();
        return ref == null ? null : ref.newInputStream();
    }

    public String getContentType() {
        try {
            return new Tika().detect(newInputStream());
        } catch (IOException e) {
            throw new SingularFormException("Não foi possivel detectar o content type.");
        }
    }

    boolean isContentTypeBrowserFriendly(String contentType) {
        final List<String> inlineContentTypes = STypeAttachment.INLINE_CONTENT_TYPES;
        for (String inlineContentType : inlineContentTypes) {
            if (contentType.matches(inlineContentType)) {
                return true;
            }
        }
        return false;
    }

    public boolean isContentTypeBrowserFriendly() {
        return isContentTypeBrowserFriendly(getContentType());
    }

    @Override
    public String toStringDisplayDefault() {
        if (getFileSize() <= 0 || getFileName() == null) {
            return super.toStringDisplayDefault();
        }
        final String[] sufixo = new String[]{"B", "KB", "MB", "GB"};
        int posSufixo = 0;
        double bytesSize = getFileSize();

        while (bytesSize > 900 && posSufixo < sufixo.length - 1) {
            bytesSize = bytesSize / 1024;
            posSufixo++;
        }

        return getFileName() + " (" + Math.round(bytesSize) + " " + sufixo[posSufixo] + ")";
    }
}
