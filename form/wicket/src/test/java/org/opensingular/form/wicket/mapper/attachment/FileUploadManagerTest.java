package org.opensingular.form.wicket.mapper.attachment;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.opensingular.form.type.core.attachment.IAttachmentPersistenceHandler;
import org.opensingular.form.type.core.attachment.IAttachmentRef;
import org.opensingular.form.wicket.mapper.attachment.upload.AttachmentKey;
import org.opensingular.form.wicket.mapper.attachment.upload.manager.FileUploadManager;

import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import static org.junit.Assert.*;

public class FileUploadManagerTest {

    HttpSession httpSession;

    @Before
    public void setUp() {
        httpSession = Mockito.mock(HttpSession.class);
    }

    @Test
    public void testGet() {
        FileUploadManager fum = new FileUploadManager();
        Mockito.when(httpSession.getAttribute(FileUploadManager.SESSION_KEY)).thenReturn(fum);
        assertEquals(fum, FileUploadManager.get(httpSession));
    }

    @Test
    public void testCreateUpload() {
        FileUploadManager fum = new FileUploadManager();
        AttachmentKey     key = fum.createUpload(1L, null, Collections.emptyList());
        assertNotNull(fum.findUploadInfo(key));
    }

    @Test
    public void testeCreateFile() throws IOException {

        //mocks
        IAttachmentRef                attachmentRef = Mockito.mock(IAttachmentRef.class);
        IAttachmentPersistenceHandler handler       = Mockito.mock(IAttachmentPersistenceHandler.class);
        InputStream                   inputStream   = Mockito.mock(InputStream.class);

        //manager
        FileUploadManager fum = new FileUploadManager(() -> handler);

        //mock return values
        Mockito.when(handler.addAttachment(Mockito.any(), Mockito.anyLong(), Mockito.anyString())).thenReturn(attachmentRef);

        //key
        AttachmentKey key = fum.createUpload(1L, null, Collections.emptyList());

        assertEquals(fum.createFile(key, "mock_1", inputStream).getAttachmentRef(), attachmentRef);

    }


}