package br.net.mirante.singular.form.mform.core.attachment;

import java.io.InputStream;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import br.net.mirante.singular.form.mform.MInstances;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.RefService;
import br.net.mirante.singular.form.mform.SingularFormException;
import br.net.mirante.singular.form.mform.document.SDocument;

/**
 * Faz a gestão da referência a anexos mantidos por um documento. Se entender
 * que um anexo não está mais em uso (sem nenhuma referência), então apaga da
 * persistência associada ao atual documento.
 *
 * @author Daniel C. Bordin
 *
 */
class AttachmentDocumentService {

    private static final String ATTACHMENT_DOCUMENT_SERVICE = "DefaultAttachmentDocumentService";

    private final SDocument document;

    private final Multiset<String> contador = HashMultiset.create();

    private AttachmentDocumentService(SDocument document) {
        this.document = document;
        MInstances.visitAll(document.getRoot(), i -> {
            if (i instanceof SIAttachment) {
                addReference((SIAttachment) i);
            }
        });
    }

    private void addReference(SIAttachment attachment) {
        String id = attachment.getFileId();
        if (id != null) {
            contador.add(id);
        }
    }

    private IAttachmentPersistenceHandler getTemporaryAttachmentHandler() {
        return document.getAttachmentPersistenceTemporaryHandler();
    }

    public int countDistinctFiles() {
        return getTemporaryAttachmentHandler().getAttachments().size();
    }

    public static AttachmentDocumentService lookup(SInstance ref) {
        return lookup(ref.getDocument());
    }

    private static AttachmentDocumentService lookup(SDocument document) {
        AttachmentDocumentService service = document.lookupLocalService(ATTACHMENT_DOCUMENT_SERVICE, AttachmentDocumentService.class);
        if (service == null) {
            service = new AttachmentDocumentService(document);
            document.bindLocalService(ATTACHMENT_DOCUMENT_SERVICE,AttachmentDocumentService.class, RefService.ofToBeDescartedIfSerialized(service));
        }
        return service;
    }

    public IAttachmentRef addContent(String oldReferenceId, byte[] content) {
        return addContent(oldReferenceId, getTemporaryAttachmentHandler().addAttachment(content));
    }

    public IAttachmentRef addContent(String oldReferenceId, InputStream in) {
        return addContent(oldReferenceId, getTemporaryAttachmentHandler().addAttachment(in));
    }

    private IAttachmentRef addContent(String oldReferenceId, IAttachmentRef newRef) {
        if (newRef.getSize() == null) {
            throw new SingularFormException("O size da nova referência a anexo deveria ter sido preenchido");
        }
        if (oldReferenceId == null) {
            contador.add(newRef.getId());
        } else if (!newRef.getId().equals(oldReferenceId)) {
            deleteReference(oldReferenceId);
            contador.add(newRef.getId());
        }
        return newRef;
    }

    public void deleteReference(String fileId) {
        if (fileId != null) {
            contador.remove(fileId);
            if (contador.count(fileId) == 0) {
                getTemporaryAttachmentHandler().deleteAttachment(fileId);
            }
        }
    }
}