package org.opensingular.form.wicket.mapper.attachment.upload.processor;

import org.apache.commons.fileupload.FileItem;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.opensingular.form.type.core.attachment.IAttachmentRef;
import org.opensingular.form.wicket.mapper.attachment.upload.info.FileUploadInfo;
import org.opensingular.form.wicket.mapper.attachment.upload.info.UploadInfo;
import org.opensingular.form.wicket.mapper.attachment.upload.info.UploadResponseInfo;
import org.opensingular.form.wicket.mapper.attachment.upload.manager.FileUploadManager;

import java.io.InputStream;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FileUploadProcessorTest {

    @Mock
    private UploadInfo uploadInfo;

    @Mock
    private FileUploadManager manager;

    @Mock
    private FileItem fileItem;

    @InjectMocks
    private FileUploadProcessor processor;

    @Test
    public void testProcesessWithSizeEquals0() throws Exception {
        when(fileItem.getSize()).thenReturn(0L);

        List<UploadResponseInfo> response = processor.process(fileItem);

        Assert.assertEquals(1L, response.size());
        Assert.assertEquals(UploadResponseInfo.ARQUIVO_NAO_PODE_SER_DE_TAMANHO_0_ZERO, response.get(0).getErrorMessage());
    }

    @Test
    public void testProcesessWithNotAllowedFileType() throws Exception {
        when(fileItem.getSize()).thenReturn(10L);
        when(uploadInfo.isFileTypeAllowed(anyString())).thenReturn(false);

        List<UploadResponseInfo> response = processor.process(fileItem);

        Assert.assertEquals(1L, response.size());
        Assert.assertEquals(UploadResponseInfo.TIPO_DE_ARQUIVO_NAO_PERMITIDO, response.get(0).getErrorMessage());
    }

    @Test
    public void testProcessCallingManagerCreteFile() throws Exception {

        InputStream    stream = mock(InputStream.class);
        FileUploadInfo info   = mock(FileUploadInfo.class);
        IAttachmentRef ref    = mock(IAttachmentRef.class);
        String         name   = "document.pdf";

        when(fileItem.getSize()).thenReturn(10L);
        when(fileItem.getName()).thenReturn(name);
        when(fileItem.getInputStream()).thenReturn(stream);
        when(uploadInfo.isFileTypeAllowed(eq("pdf"))).thenReturn(true);
        when(manager.createFile(eq(uploadInfo), eq(name), eq(stream))).thenReturn(info);
        when(info.getAttachmentRef()).thenReturn(ref);

        List<UploadResponseInfo> response = processor.process(fileItem);

        Assert.assertEquals(1L, response.size());
        Assert.assertNull(response.get(0).getErrorMessage());

        verify(manager).createFile(eq(uploadInfo), eq(name), eq(stream));

    }

}