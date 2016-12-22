package org.opensingular.form.wicket.mapper.attachment.upload.manager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.opensingular.form.type.core.attachment.IAttachmentRef;
import org.opensingular.form.wicket.mapper.attachment.upload.AttachmentKey;
import org.opensingular.form.wicket.mapper.attachment.upload.UploadPathHandler;
import org.opensingular.form.wicket.mapper.attachment.upload.factory.AttachmentKeyFactory;
import org.opensingular.form.wicket.mapper.attachment.upload.info.FileUploadInfo;
import org.opensingular.form.wicket.mapper.attachment.upload.info.FileUploadInfoRepository;
import org.opensingular.form.wicket.mapper.attachment.upload.info.UploadInfoRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;
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
    AttachmentKeyFactory attachmentKeyFactory = new AttachmentKeyFactory();

    @Mock
    UploadInfoRepository uploadInfoRepository = new UploadInfoRepository();

    @Mock
    FileUploadInfoRepository fileUploadInfoRepository = new FileUploadInfoRepository();

    @Mock
    UploadPathHandler uploadPathHandler;

    @InjectMocks
    FileUploadManager fileUploadManager;

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
        Path           path           = Files.createTempFile("mock", "file");

        when(uploadPathHandler.getLocalFilePath(eq(fileUploadInfo))).thenReturn(path);
        when(attachmentKeyFactory.get()).thenReturn(attachmentKey);
        when(fileUploadInfo.getAttachmentRef()).thenReturn(attachmentRef);
        when(fileUploadInfoRepository.findByID(key)).thenReturn(Optional.of(fileUploadInfo));

        AttachmentKey createdKey = fileUploadManager.createUpload(null, null, null, null);

        assertEquals(attachmentKey, createdKey);
        assertTrue(fileUploadManager.consumeFile(createdKey.asString(), callback).orElse(false));

        verify(callback).apply(eq(attachmentRef));
        verify(fileUploadInfoRepository).remove(eq(fileUploadInfo));
    }

    public static class MockCallback implements Function<IAttachmentRef, Boolean> {
        @Override
        public Boolean apply(IAttachmentRef attachmentRef) {
            return true;
        }
    }

}