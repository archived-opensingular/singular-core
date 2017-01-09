package org.opensingular.form.wicket.mapper.attachment.upload.manager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.opensingular.form.type.core.attachment.IAttachmentPersistenceHandler;
import org.opensingular.form.type.core.attachment.IAttachmentRef;
import org.opensingular.form.wicket.mapper.attachment.upload.AttachmentKey;
import org.opensingular.form.wicket.mapper.attachment.upload.UploadPathHandler;
import org.opensingular.form.wicket.mapper.attachment.upload.factory.AttachmentKeyFactory;
import org.opensingular.form.wicket.mapper.attachment.upload.info.FileUploadInfo;
import org.opensingular.form.wicket.mapper.attachment.upload.info.FileUploadInfoRepository;
import org.opensingular.form.wicket.mapper.attachment.upload.info.UploadInfo;
import org.opensingular.form.wicket.mapper.attachment.upload.info.UploadInfoRepository;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FileUploadManagerTest {

    @Mock
    private AttachmentKeyFactory attachmentKeyFactory;

    @Mock
    private UploadInfoRepository uploadInfoRepository;

    @Mock
    private FileUploadInfoRepository fileUploadInfoRepository;

    @Mock
    private UploadPathHandler uploadPathHandler;

    @InjectMocks
    private FileUploadManager fileUploadManager;

    @Test
    public void testCreateUpload() {
        fileUploadManager.createUpload(null, null, null, null);
        verify(uploadInfoRepository).add(any());
    }

    @Test
    public void testConsumeFile() throws IOException {

        String         key            = "123456";
        AttachmentKey  attachmentKey  = new AttachmentKey(key);
        MockCallback   callback       = spy(new MockCallback());
        FileUploadInfo fileUploadInfo = mock(FileUploadInfo.class);
        IAttachmentRef attachmentRef  = mock(IAttachmentRef.class);
        Path           path           = createTestPath().resolve(key);

        when(uploadPathHandler.getLocalFilePath(eq(fileUploadInfo))).thenReturn(path);
        when(attachmentKeyFactory.get()).thenReturn(attachmentKey);
        when(fileUploadInfo.getAttachmentRef()).thenReturn(attachmentRef);
        when(fileUploadInfoRepository.findByID(key)).thenReturn(Optional.of(fileUploadInfo));

        AttachmentKey createdKey = fileUploadManager.createUpload(null, null, null, null);

        assertEquals(attachmentKey, createdKey);
        assertTrue(fileUploadManager.consumeFile(createdKey.asString(), callback).orElse(false));

        verify(callback).apply(eq(attachmentRef));
        verify(fileUploadInfoRepository).remove(eq(fileUploadInfo));

        path.toFile().deleteOnExit();
    }

    public static class MockCallback implements Function<IAttachmentRef, Boolean> {
        @Override
        public Boolean apply(IAttachmentRef attachmentRef) {
            return true;
        }
    }

    @Test
    public void testCreateFile() throws IOException {

        String                        key           = "123456";
        AttachmentKey                 attachmentKey = new AttachmentKey(key);
        UploadInfo                    ui            = mock(UploadInfo.class);
        IAttachmentPersistenceHandler handler       = mock(IAttachmentPersistenceHandler.class);
        InputStream                   in            = mock(InputStream.class);
        Path                          path          = createTestPath().resolve(key);

        when(ui.getPersistenceHandlerSupplier()).thenReturn(() -> handler);
        when(uploadPathHandler.getLocalFilePath(eq(key))).thenReturn(path);
        when(attachmentKeyFactory.get()).thenReturn(attachmentKey);

        String         fileName = "my_document.pdf";
        FileUploadInfo info     = fileUploadManager.createFile(ui, fileName, in);

        assertNotNull(info);

        verify(fileUploadInfoRepository).add(eq(info));
        verify(handler).addAttachment(path.toFile(), Files.size(path), fileName);

        path.toFile().deleteOnExit();
    }

    public Path createTestPath() throws IOException {
        Path tempDirectory = Files.createTempDirectory(this.getClass().getSimpleName() + "_");
        tempDirectory.toFile().deleteOnExit();
        return tempDirectory;
    }

}