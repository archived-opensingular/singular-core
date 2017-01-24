package org.opensingular.server.commons.service.attachment;

import org.opensingular.form.SInstances;
import org.opensingular.form.document.SDocument;
import org.opensingular.form.persistence.entity.FormAttachmentEntity;
import org.opensingular.form.service.IFormService;
import org.opensingular.form.type.core.attachment.IAttachmentPersistenceHandler;
import org.opensingular.form.type.core.attachment.SIAttachment;
import org.opensingular.form.type.core.attachment.helper.DefaultAttachmentPersistenceHelper;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
public class ServerAttachmentPersistenceHelper extends DefaultAttachmentPersistenceHelper {

    @Inject
    private IFormService formService;

    @Inject
    private IFormAttachmentService formAttachmentService;

    @Inject
    public ServerAttachmentPersistenceHelper(IFormService formService,
                                             IFormAttachmentService formAttachmentService) {
        this.formService = formService;
        this.formAttachmentService = formAttachmentService;
    }

    @Override
    public void doPersistence(SDocument document,
                              IAttachmentPersistenceHandler temporaryHandler,
                              IAttachmentPersistenceHandler persistenceHandler) {

        final List<FormAttachmentEntity> currentFormAttachmentEntities = getCurrentFormAttachmentEntities(document);

        findAttachments(document).forEach(attachment -> {
            removeFormAttachment(currentFormAttachmentEntities, attachment);
            handleAttachment(attachment, temporaryHandler, persistenceHandler);
        });

        currentFormAttachmentEntities.forEach(formAttachmentService::deleteFormAttachmentEntity);
    }

    private void removeFormAttachment(List<FormAttachmentEntity> currentFormAttachmentEntities, SIAttachment attachment) {
        currentFormAttachmentEntities.stream()
                .filter(f -> f.getAttachmentEntity().getCod().toString().equals(attachment.getFileId()))
                .findFirst()
                .ifPresent(currentFormAttachmentEntities::remove);
    }

    private List<FormAttachmentEntity> getCurrentFormAttachmentEntities(SDocument document) {
        return formAttachmentService.findAllByVersion(formService.findCurrentFormVersion(document));
    }

    protected List<SIAttachment> findAttachments(SDocument document) {
        return SInstances.streamDescendants(document.getRoot(), true)
                .filter(instance -> instance instanceof SIAttachment)
                .map(instance -> (SIAttachment) instance)
                .collect(Collectors.toList());
    }

}