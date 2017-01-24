package org.opensingular.server.commons.service.attachment;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.opensingular.form.SInstance;
import org.opensingular.form.document.SDocument;
import org.opensingular.form.persistence.dao.AttachmentDao;
import org.opensingular.form.persistence.dto.AttachmentRef;
import org.opensingular.form.persistence.entity.AttachmentContentEntity;
import org.opensingular.form.persistence.entity.AttachmentEntity;
import org.opensingular.form.persistence.entity.FormVersionEntity;
import org.opensingular.form.service.IFormService;
import org.opensingular.form.type.core.attachment.AttachmentCopyContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class ServerAttachmentPersistenceServiceTest {

    @Mock
    private AttachmentRef attachmentRef;

    @Mock
    private SDocument document;

    @Mock
    private FormAttachmentService formAttachmentService;

    @Mock
    private SInstance root;

    @Mock
    private IFormService formService;

    @Mock
    private FormVersionEntity formVersionEntity;

    @Mock
    private AttachmentDao<AttachmentEntity, AttachmentContentEntity> attachmentDao;

    @InjectMocks
    private ServerAttachmentPersistenceService serverAttachmentPersistenceService;

    @Test
    public void copy() throws Exception {

        final Long             myAttachmenyID   = 1L;
        final AttachmentEntity attachmentEntity = new AttachmentEntity();

        when(document.getRoot()).thenReturn(root);
        when(attachmentRef.getId()).thenReturn(String.valueOf(myAttachmenyID));
        when(formService.findCurrentFormVersion(eq(document))).thenReturn(formVersionEntity);
        when(attachmentDao.find(myAttachmenyID)).thenReturn(attachmentEntity);

        AttachmentCopyContext context = serverAttachmentPersistenceService.copy(attachmentRef, document);

        assertFalse(context.isDeleteOldFiles());
        assertFalse(context.isUpdateFileId());
        assertEquals(attachmentRef, context.getNewAttachmentRef());

        verify(formAttachmentService).saveNewFormAttachmentEntity(eq(attachmentEntity), eq(formVersionEntity));
    }

    @Test
    public void deleteAttachment() throws Exception {

        final Long             myAttachmenyID   = 10L;
        final AttachmentEntity attachmentEntity = new AttachmentEntity();

        attachmentEntity.setCod(1L);

        when(formService.findCurrentFormVersion(eq(document))).thenReturn(formVersionEntity);
        when(attachmentDao.find(myAttachmenyID)).thenReturn(attachmentEntity);

        serverAttachmentPersistenceService.deleteAttachment(String.valueOf(myAttachmenyID), document);

        verify(formAttachmentService).deleteFormAttachmentEntity(eq(attachmentEntity), eq(formVersionEntity));
    }

}