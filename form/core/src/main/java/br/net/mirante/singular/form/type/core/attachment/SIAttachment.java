/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.type.core.attachment;

import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SingularFormException;
import org.apache.tika.Tika;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

public class SIAttachment extends SIComposite {

    private AttachmentDocumentService getAttachmentService() {
        return AttachmentDocumentService.lookup(this);
    }

    public void setContent(InputStream in) {
        setContent(getAttachmentService().addContent(getFileId(), in));
    }

    public void setContent(byte[] content) {
        setContent(getAttachmentService().addContent(getFileId(), content));
    }

    private void setContent(IAttachmentRef ref) {
        if (!Objects.equals(getFileHashSHA1(), ref.getHashSHA1())) {
            setValue(STypeAttachment.FIELD_HASH_SHA1, ref.getHashSHA1());
        }
        if (Objects.equals(ref.getId(), ref.getHashSHA1())) {
            setValue(STypeAttachment.FIELD_FILE_ID, null);
        } else {
            setValue(STypeAttachment.FIELD_FILE_ID, ref.getId());
        }
        setValue(STypeAttachment.FIELD_SIZE, ref.getSize());
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
        final String hash = getFileHashSHA1();
        if (hash == null) {
            return null;
        }

        IAttachmentRef ref = getDocument().getAttachmentPersistenceTemporaryHandler().getAttachment(hash);

        if (ref == null) {
            ref = getDocument().getAttachmentPersistencePermanentHandler().getAttachment(hash);
        }

        if (ref == null) {
            //todo trocar para SingularException, rever se é relevante uma vez que crash a aplicacao pela falta de um anexo.
//            throw new RuntimeException(errorMsg("Não foi encontrado o arquivo de hash=" + hash + " e nome=" + getFileName()));
        }
        return ref;
    }

    public void setFileName(String name) {
        setValue(STypeAttachment.FIELD_NAME, name);
    }

    public void setFileId(String id) {
        setValue(STypeAttachment.FIELD_FILE_ID, id);
    }

    public void setOriginalFileId(String id) {
        setAttributeValue(STypeAttachment.ATR_ORIGINAL_ID, id);
    }

    public void setFileHashSHA1(String hash) {  setValue(STypeAttachment.FIELD_HASH_SHA1, hash);    }

    public void setFileSize(Integer size) { setValue(STypeAttachment.FIELD_SIZE, size); }

    /**
     * Retorna o tamanho do arquivo binário associado ou -1 se não houver
     * arquivo.
     */
    public Integer getFileSize() {
        return getValueInteger(STypeAttachment.FIELD_SIZE);
    }

    public String getFileName() {
        return getValueString(STypeAttachment.FIELD_NAME);
    }

    public String getFileId() {
        String id = getValueString(STypeAttachment.FIELD_FILE_ID);
        if (id == null) {
            return getFileHashSHA1();
        }
        return id;
    }

    public String getOriginalFileId() {
        return getAttributeValue(STypeAttachment.ATR_ORIGINAL_ID);
    }

    public String getFileHashSHA1() {
        return getValueString(STypeAttachment.FIELD_HASH_SHA1);
    }

    public byte[] getContentAsByteArray() {
        IAttachmentRef ref = getAttachmentRef();
        return ref == null ? null : ref.getContentAsByteArray();
    }

    public InputStream getContent() {
        IAttachmentRef ref = getAttachmentRef();
        return ref == null ? null : ref.getContent();
    }

    private String getContentType() {
        try {
            return new Tika().detect(getContent());
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
        if (getFileSize() == null || getFileName() == null) {
            return super.toStringDisplayDefault();
        }
        final String[] sufixo    = new String[]{"B", "KB", "MB", "GB"};
        int            posSufixo = 0;
        double         bytesSize = getFileSize();

        while (bytesSize > 900 && posSufixo < sufixo.length - 1) {
            bytesSize = bytesSize / 1024;
            posSufixo++;
        }

        return getFileName() + " (" + Math.round(bytesSize) + " " + sufixo[posSufixo] + ")";
    }
}
