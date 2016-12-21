package org.opensingular.server.commons.service.attachment;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.opensingular.form.document.SDocument;
import org.opensingular.form.persistence.dao.AttachmentDao;
import org.opensingular.form.persistence.entity.AttachmentContentEntitty;
import org.opensingular.form.persistence.entity.AttachmentEntity;
import org.opensingular.form.persistence.entity.FormVersionEntity;
import org.opensingular.form.service.IFormService;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ServerTemporaryAttachmentPersistenceServiceTest {

    @Mock
    private FormVersionEntity formVersionEntity;

    @Mock
    private IFormService formService;

    @Mock
    private SDocument document;

    @Mock
    private IFormAttachmentService formAttachmentService;

    @Mock
    private AttachmentDao<AttachmentEntity, AttachmentContentEntitty> attachmentDao;

    @InjectMocks
    private ServerTemporaryAttachmentPersistenceService serverTemporaryAttachmentPersistenceService;

    @Test
    public void deleteAttachment() throws Exception {

        Long             myAttachmentID   = 10L;
        AttachmentEntity attachmentEntity = new AttachmentEntity();

        attachmentEntity.setCod(1L);

        when(formService.findCurrentFormVersion(document)).thenReturn(formVersionEntity);
        when(attachmentDao.find(myAttachmentID)).thenReturn(attachmentEntity);

        serverTemporaryAttachmentPersistenceService.deleteAttachment(String.valueOf(myAttachmentID), document);
        verify(formAttachmentService).deleteFormAttachmentEntity(eq(attachmentEntity), eq(formVersionEntity));
    }


}