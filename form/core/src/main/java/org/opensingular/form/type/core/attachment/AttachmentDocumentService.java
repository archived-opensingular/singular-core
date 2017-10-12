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

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import org.opensingular.form.SInstance;
import org.opensingular.form.SInstances;
import org.opensingular.form.SingularFormException;
import org.opensingular.form.document.SDocument;
import org.opensingular.lib.commons.context.RefService;

import java.io.File;
import java.util.Optional;

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
        SInstances.visit(document.getRoot(), (i, v) -> {
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

    private IAttachmentPersistenceHandler<?> getTemporaryAttachmentHandler() {
        return document.getAttachmentPersistenceTemporaryHandler();
    }

    public int countDistinctFiles() {
        return getTemporaryAttachmentHandler().getAttachments().size();
    }

    public static AttachmentDocumentService lookup(SInstance ref) {
        return lookup(ref.getDocument());
    }

    private static AttachmentDocumentService lookup(SDocument document) {
        Optional<AttachmentDocumentService> service = document.lookupLocalService(ATTACHMENT_DOCUMENT_SERVICE, AttachmentDocumentService.class);
        if (service.isPresent()) {
            return service.get();
        }
        AttachmentDocumentService service2 = new AttachmentDocumentService(document);
        document.bindLocalService(ATTACHMENT_DOCUMENT_SERVICE, AttachmentDocumentService.class, RefService.ofToBeDescartedIfSerialized(service2));
        return service2;
    }

    public IAttachmentRef addContent(String currentReferenceId, File content, long length, String name, String hashSha1, SDocument document) {
        return addContent(currentReferenceId, getTemporaryAttachmentHandler().addAttachment(content, length, name, hashSha1), document);
    }

    private IAttachmentRef addContent(String oldReferenceId, IAttachmentRef newRef, SDocument document) {
        if (newRef.getSize() <= 0) {
            throw new SingularFormException("O tamanho (em bytes) da nova referência a deve ser preenchido.");
        }
        if (oldReferenceId == null) {
            contador.add(newRef.getId());
        } else if (!newRef.getId().equals(oldReferenceId)) {
            deleteReference(oldReferenceId, document);
            contador.add(newRef.getId());
        }
        return newRef;
    }

    public void deleteReference(String fileId, SDocument document) {
        if (fileId != null) {
            contador.remove(fileId);
            if (contador.count(fileId) == 0) {
                getTemporaryAttachmentHandler().deleteAttachment(fileId, document);
            }
        }
    }
}
