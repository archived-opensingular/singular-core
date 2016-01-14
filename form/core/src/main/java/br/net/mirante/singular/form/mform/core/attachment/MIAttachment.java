package br.net.mirante.singular.form.mform.core.attachment;

import java.io.InputStream;
import java.util.Objects;

import br.net.mirante.singular.form.mform.MIComposto;

public class MIAttachment extends MIComposto {

    private IAttachmentPersistenceHandler getAttachmentHandler() {
        return getDocument().getAttachmentPersistenceHandler();
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
            setValor(MTipoAttachment.FIELD_HASH_SHA1, ref.getHashSHA1());
        }
        if (Objects.equals(ref.getId(), ref.getHashSHA1())) {
            setValor(MTipoAttachment.FIELD_FILE_ID, null);
        } else {
            setValor(MTipoAttachment.FIELD_FILE_ID, ref.getId());
        }
        setValor(MTipoAttachment.FIELD_SIZE, ref.getSize());
    }

    public void deleteReference() {
        if (getFileId() != null) {
            getAttachmentService().deleteReference(getFileId());
        }
        setValor(MTipoAttachment.FIELD_FILE_ID, null);
        setValor(MTipoAttachment.FIELD_HASH_SHA1, null);
        setValor(MTipoAttachment.FIELD_SIZE, null);
        setValor(MTipoAttachment.FIELD_NAME, null);
        setValorAtributo(MTipoAttachment.ATR_ORIGINAL_ID, null);
        setValorAtributo(MTipoAttachment.ATR_IS_TEMPORARY, null);
    }

    @Override
    protected void onRemove() {
        deleteReference();
        super.onRemove();
    }

    public IAttachmentRef getAttachmentRef() {
        String id = getFileId();
        if (id == null) {
            return null;
        }
        IAttachmentRef ref = getAttachmentHandler().getAttachment(id);
        if (ref == null) {
            throw new RuntimeException(errorMsg("Não foi encontrado o arquivo de id=" + id + " e nome=" + getFileName()));
        }
        return ref;
    }

    public void setFileName(String name) {
        setValor(MTipoAttachment.FIELD_NAME, name);
    }

    public void setFileHashSHA1(String hash) {
        setValor(MTipoAttachment.FIELD_HASH_SHA1, hash);
    }

    public void setFileId(String id) {
        setValor(MTipoAttachment.FIELD_FILE_ID, id);
    }
    
    public void setOriginalFileId(String id) {
        setValorAtributo(MTipoAttachment.ATR_ORIGINAL_ID, id);
    }

    public void setFileSize(Integer size) {
        setValor(MTipoAttachment.FIELD_SIZE, size);
    }

    /**
     * Retorna o tamanho do arquivo binário associado ou -1 se não houver
     * arquivo.
     */
    public Integer getFileSize() {
        return getValorInteger(MTipoAttachment.FIELD_SIZE);
    }

    public String getFileName() {
        return getValorString(MTipoAttachment.FIELD_NAME);
    }

    public String getFileId() {
        String id = getValorString(MTipoAttachment.FIELD_FILE_ID);
        if (id == null) {
            return getFileHashSHA1();
        }
        return id;
    }
    
    public String getOriginalFileId() {
        return (String) getValorAtributo(MTipoAttachment.ATR_ORIGINAL_ID);
    }

    public String getFileHashSHA1() {
        return getValorString(MTipoAttachment.FIELD_HASH_SHA1);
    }

    public byte[] getContentAsByteArray() {
        IAttachmentRef ref = getAttachmentRef();
        return ref == null ? null : ref.getContentAsByteArray();
    }

    public InputStream getContent() {
        IAttachmentRef ref = getAttachmentRef();
        return ref == null ? null : ref.getContent();
    }

    public void setTemporary() {
        setValorAtributo(MTipoAttachment.ATR_IS_TEMPORARY, "true");
    }


    public boolean isTemporary() {
        return getValorAtributo(MTipoAttachment.ATR_IS_TEMPORARY) != null;
    }


}
