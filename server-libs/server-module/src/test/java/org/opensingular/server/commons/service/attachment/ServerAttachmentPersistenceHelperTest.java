package org.opensingular.server.commons.service.attachment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.opensingular.form.document.SDocument;
import org.opensingular.form.persistence.entity.AttachmentEntity;
import org.opensingular.form.persistence.entity.FormAttachmentEntity;
import org.opensingular.form.persistence.entity.FormVersionEntity;
import org.opensingular.form.service.IFormService;
import org.opensingular.form.type.core.attachment.IAttachmentPersistenceHandler;
import org.opensingular.form.type.core.attachment.SIAttachment;
import org.opensingular.form.type.core.attachment.helper.DefaultAttachmentPersistenceHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ServerAttachmentPersistenceHelperTest {

    @Mock
    private IFormService formService;

    @Mock
    private IFormAttachmentService formAttachmentService;

    @Mock
    private DefaultAttachmentPersistenceHelper defaultAttachmentPersistenceHelper;

    @Mock
    private SIAttachment obsoletAttachment;

    @Mock
    private SIAttachment newtAttachment;

    @Mock
    private SIAttachment normalAttachment;

    @Mock
    private FormAttachmentEntity obsoletFormAttachmentEntity;

    @Mock
    private FormAttachmentEntity normalFormAttachmentEntity;

    @Mock
    private AttachmentEntity obsoletAttachmentEntity;

    @Mock
    private AttachmentEntity normalAttachmentEntity;

    @Mock
    private SDocument document;

    @Mock
    private IAttachmentPersistenceHandler persistenceHandler;

    @Mock
    private IAttachmentPersistenceHandler temporaryHandler;

    @Mock
    private FormVersionEntity formVersionEntity;

    private ServerAttachmentPersistenceHelper serverAttachmentPersistenceHelper;

    @Before
    public void setUp(){

        when(obsoletFormAttachmentEntity.getAttachmentEntity()).thenReturn(obsoletAttachmentEntity);
        when(normalFormAttachmentEntity.getAttachmentEntity()).thenReturn(normalAttachmentEntity);

        when(obsoletAttachmentEntity.getCod()).thenReturn(1L);
        when(normalAttachmentEntity.getCod()).thenReturn(2L);

        when(obsoletAttachment.getFileId()).thenReturn("1");
        when(normalAttachment.getFileId()).thenReturn("2");

        when(formService.findCurrentFormVersion(document)).thenReturn(formVersionEntity);
        when(formAttachmentService.findAllByVersion(eq(formVersionEntity))).thenReturn(new ArrayList<>(Arrays.asList(obsoletFormAttachmentEntity, normalFormAttachmentEntity)));

        serverAttachmentPersistenceHelper = new ServerAttachmentPersistenceHelper(formService, formAttachmentService){
            @Override
            protected List<SIAttachment> findAttachments(SDocument document) {
                return new ArrayList<>(Arrays.asList(newtAttachment, normalAttachment));
            }

            @Override
            public void handleAttachment(SIAttachment attachment, IAttachmentPersistenceHandler temporaryHandler, IAttachmentPersistenceHandler persistenceHandler) {
                defaultAttachmentPersistenceHelper.handleAttachment(attachment, temporaryHandler, persistenceHandler);
            }
        };
    }

    @Test
    public void testIfDoPersistenceRemoveObsoletFormAttachmentEntities() {

        serverAttachmentPersistenceHelper.doPersistence(document, temporaryHandler, persistenceHandler);

        verify(defaultAttachmentPersistenceHelper).handleAttachment(eq(newtAttachment), eq(temporaryHandler), eq(persistenceHandler));
        verify(defaultAttachmentPersistenceHelper).handleAttachment(eq(normalAttachment), eq(temporaryHandler), eq(persistenceHandler));
        verifyNoMoreInteractions(defaultAttachmentPersistenceHelper);

        verify(formAttachmentService).deleteFormAttachmentEntity(eq(obsoletFormAttachmentEntity));
        verify(formAttachmentService).findAllByVersion(eq(formVersionEntity));
        verifyNoMoreInteractions(formAttachmentService);

    }



}