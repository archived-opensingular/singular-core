package br.net.mirante.singular.form.mform.core.attachment;

import java.io.InputStream;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import br.net.mirante.singular.form.mform.MInstances;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.ServiceRef;
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

    private final SDocument document;

    private final Multiset<String> contador = HashMultiset.create();

    private AttachmentDocumentService(SDocument document) {
        this.document = document;
        MInstances.visitAll(document.getRoot(), i -> {
            if (i instanceof MIAttachment) {
                addReference((MIAttachment) i);
            }
        });
    }

    private void addReference(MIAttachment attachment) {
        String id = attachment.getFileId();
        if (id != null) {
            contador.add(id);
        }
    }

    private IAttachmentPersistenceHandler getAttachmentHandler() {
        return document.getAttachmentPersistenceHandler();
    }

    public int countDistinctFiles() {
        return getAttachmentHandler().getAttachments().size();
    }

    public static AttachmentDocumentService lookup(MInstancia ref) {
        return lookup(ref.getDocument());
    }

    private static AttachmentDocumentService lookup(SDocument document) {
        AttachmentDocumentService aService = document.lookupLocalService(AttachmentDocumentService.class);
        if (aService == null) {
            aService = new AttachmentDocumentService(document);
            document.bindLocalService(AttachmentDocumentService.class, ServiceRef.ofToBeDescartedIfSerialized(aService));
        }
        return aService;
    }

    public IAttachmentRef addContent(String oldReferenceId, byte[] content) {
        return addContent(oldReferenceId, getAttachmentHandler().addAttachment(content));
    }

    public IAttachmentRef addContent(String oldReferenceId, InputStream in) {
        return addContent(oldReferenceId, getAttachmentHandler().addAttachment(in));
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
                getAttachmentHandler().deleteAttachment(fileId);
            }
        }
    }
}
