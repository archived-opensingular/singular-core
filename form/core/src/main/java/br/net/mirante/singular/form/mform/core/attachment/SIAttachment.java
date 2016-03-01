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
            setValor(STypeAttachment.FIELD_HASH_SHA1, ref.getHashSHA1());
        }
        if (Objects.equals(ref.getId(), ref.getHashSHA1())) {
            setValor(STypeAttachment.FIELD_FILE_ID, null);
        } else {
            setValor(STypeAttachment.FIELD_FILE_ID, ref.getId());
        }
        setValor(STypeAttachment.FIELD_SIZE, ref.getSize());
    }

    public void deleteReference() {
        if (getFileId() != null) {
            getAttachmentService().deleteReference(getFileId());
        }
        setValor(STypeAttachment.FIELD_FILE_ID, null);
        setValor(STypeAttachment.FIELD_HASH_SHA1, null);
        setValor(STypeAttachment.FIELD_SIZE, null);
        setValor(STypeAttachment.FIELD_NAME, null);
        setValorAtributo(STypeAttachment.ATR_ORIGINAL_ID, null);
        setValorAtributo(STypeAttachment.ATR_IS_TEMPORARY, null);
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
        setValor(STypeAttachment.FIELD_NAME, name);
    }

    public void setFileHashSHA1(String hash) {
        setValor(STypeAttachment.FIELD_HASH_SHA1, hash);
    }

    public void setFileId(String id) {
        setValor(STypeAttachment.FIELD_FILE_ID, id);
    }

    public void setOriginalFileId(String id) {
        setValorAtributo(STypeAttachment.ATR_ORIGINAL_ID, id);
    }

    public void setFileSize(Integer size) {
        setValor(STypeAttachment.FIELD_SIZE, size);
    }

    /**
     * Retorna o tamanho do arquivo binário associado ou -1 se não houver
     * arquivo.
     */
    public Integer getFileSize() {
        return getValorInteger(STypeAttachment.FIELD_SIZE);
    }

    public String getFileName() {
        return getValorString(STypeAttachment.FIELD_NAME);
    }

    public String getFileId() {
        String id = getValorString(STypeAttachment.FIELD_FILE_ID);
        if (id == null) {
            return getFileHashSHA1();
        }
        return id;
    }

    public String getOriginalFileId() {
        return getValorAtributo(STypeAttachment.ATR_ORIGINAL_ID);
    }

    public String getFileHashSHA1() {
        return getValorString(STypeAttachment.FIELD_HASH_SHA1);
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
        setValorAtributo(STypeAttachment.ATR_IS_TEMPORARY, "true");
        return this;
    }

    public boolean isTemporary() {
        return getValorAtributo(STypeAttachment.ATR_IS_TEMPORARY) != null;
    }


}
