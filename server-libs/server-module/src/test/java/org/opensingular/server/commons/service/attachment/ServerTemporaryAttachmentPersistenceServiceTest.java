package org.opensingular.server.commons.service.attachment;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.opensingular.form.document.SDocument;
import org.opensingular.form.persistence.entity.FormVersionEntity;
import org.opensingular.form.service.IFormService;

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

    @InjectMocks
    private ServerTemporaryAttachmentPersistenceService serverTemporaryAttachmentPersistenceService;

    @Test
    public void deleteAttachment() throws Exception {
        Long myAttachmentID = 10L;
        when(formService.findCurrentFormVersion(document)).thenReturn(formVersionEntity);
        serverTemporaryAttachmentPersistenceService.deleteAttachment(String.valueOf(myAttachmentID), document);
        verify(formAttachmentService).deleteFormAttachmentEntity(myAttachmentID, formVersionEntity);
    }

    @Test
    public void deleteAttachmentWithNomNumericID() throws Exception {
        when(formService.findCurrentFormVersion(document)).thenReturn(formVersionEntity);
        serverTemporaryAttachmentPersistenceService.deleteAttachment("abc", document);
        verifyZeroInteractions(formAttachmentService);
        serverTemporaryAttachmentPersistenceService.deleteAttachment("123abc", document);
        verifyZeroInteractions(formAttachmentService);
        serverTemporaryAttachmentPersistenceService.deleteAttachment("abc123", document);
        verifyZeroInteractions(formAttachmentService);
        serverTemporaryAttachmentPersistenceService.deleteAttachment("1a2b3c", document);
        verifyZeroInteractions(formAttachmentService);
    }

}