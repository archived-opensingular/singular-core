package br.net.mirante.singular.form.mform.core.attachment;

import java.io.InputStream;
import java.util.Objects;

import br.net.mirante.singular.form.mform.SIComposite;

public class SIAttachment extends SIComposite {

    private IAttachmentPersistenceHandler getAttachmentHandler() {
        return isTemporary() ? getDocument().getAttachmentPersistenceTemporaryHandler()
                : getDocument().getAttachmentPersistencePermanentHandler();
    }

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

    public void deleteReference() {
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

    public IAttachmentRef getAttachmentRef() {
        final String hash = getFileHashSHA1();
        if (hash == null) {
            return null;
        }
        final IAttachmentRef ref = getAttachmentHandler().getAttachment(hash);
        if (ref == null) {
            throw new RuntimeException(errorMsg("Não foi encontrado o arquivo de hash=" + hash + " e nome=" + getFileName()));
        }
        return ref;
    }

    public void setFileName(String name) {
        setValue(STypeAttachment.FIELD_NAME, name);
    }

    public void setFileHashSHA1(String hash) {
        setValue(STypeAttachment.FIELD_HASH_SHA1, hash);
    }

    public void setFileId(String id) {
        setValue(STypeAttachment.FIELD_FILE_ID, id);
    }

    public void setOriginalFileId(String id) {
        setAttributeValue(STypeAttachment.ATR_ORIGINAL_ID, id);
    }

    public void setFileSize(Integer size) {
        setValue(STypeAttachment.FIELD_SIZE, size);
    }

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

    public SIAttachment setTemporary() {
        setAttributeValue(STypeAttachment.ATR_IS_TEMPORARY, "true");
        return this;
    }

    public boolean isTemporary() {
        return getAttributeValue(STypeAttachment.ATR_IS_TEMPORARY) != null;
    }


}
