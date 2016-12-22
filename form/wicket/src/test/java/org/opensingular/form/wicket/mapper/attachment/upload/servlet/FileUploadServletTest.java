package org.opensingular.form.wicket.mapper.attachment.upload.servlet;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.opensingular.form.wicket.mapper.attachment.upload.AttachmentKey;
import org.opensingular.form.wicket.mapper.attachment.upload.config.FileUploadConfig;
import org.opensingular.form.wicket.mapper.attachment.upload.factory.AttachmentKeyFactory;
import org.opensingular.form.wicket.mapper.attachment.upload.factory.FileUploadObjectFactories;
import org.opensingular.form.wicket.mapper.attachment.upload.factory.FileUploadProcessorFactory;
import org.opensingular.form.wicket.mapper.attachment.upload.factory.ServletFileUploadFactory;
import org.opensingular.form.wicket.mapper.attachment.upload.info.UploadInfo;
import org.opensingular.form.wicket.mapper.attachment.upload.manager.FileUploadManager;
import org.opensingular.form.wicket.mapper.attachment.upload.manager.FileUploadManagerFactory;
import org.opensingular.form.wicket.mapper.attachment.upload.processor.FileUploadProcessor;
import org.opensingular.form.wicket.mapper.attachment.upload.writer.UploadResponseWriter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FileUploadServletTest {

    private String        keyValue      = "123456";
    private AttachmentKey attachmentKey = new AttachmentKey(keyValue);

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    @Mock
    private HttpServletResponse response;

    @Mock
    private AttachmentKeyFactory attachmentKeyFactory;

    @Mock
    private FileUploadProcessorFactory fileUploadProcessorFactory;

    @Mock
    private UploadInfo uploadInfo;

    @Mock
    private FileUploadManager uploadManager;

    @Mock
    private FileUploadProcessor uploadProcessor;

    @Mock
    private FileUploadManagerFactory fileUploadManagerFactory;

    @Mock
    private FileUploadConfig.Factory fileUploadConfigFactory;

    @Mock
    private FileUploadConfig fileUploadConfig;

    @Mock
    private UploadResponseWriter.Factory uploadResponseWriterFactory;

    @Mock
    private UploadResponseWriter uploadResponseWriter;

    @Mock
    private ServletFileUploadFactory servletFileUploadFactory;

    @Mock
    private ServletFileUpload servletFileUpload;

    @InjectMocks
    private FileUploadObjectFactories fileUploadObjectFactories;

    private FileUploadServlet uploadServlet;

    @Before
    public void setUp() {
        uploadServlet = new FileUploadServlet(fileUploadObjectFactories);
    }

    @Test
    public void getUploadUrl() throws Exception {
        ServletContext servletContext = mock(ServletContext.class);
        String         contextpath    = "http://localhost:8080";
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getContextPath()).thenReturn(contextpath);
        assertEquals(contextpath + FileUploadServlet.UPLOAD_URL + "/" + keyValue, FileUploadServlet.getUploadUrl(request, attachmentKey));
    }

    @Test
    public void testDoPostWithNonMultipart() throws Exception {
        uploadServlet.doPost(request, response);
        verify(response).sendError(eq(HttpServletResponse.SC_BAD_REQUEST), any());
    }

    @Test
    public void testDoPostWithMultipartNullKey() throws Exception {
        mockMultipartAndPost();
        mockAttachmentKeyFactoryResult(null);
        uploadServlet.doPost(request, response);
        verify(attachmentKeyFactory).get(eq(request));
        verify(response).sendError(eq(HttpServletResponse.SC_BAD_REQUEST), any());
    }


    @Test
    public void testDoPostWithMultipartValidKey() throws Exception {

        Map<String, List<FileItem>> params    = new HashMap<>();

        List<FileItem>              fileItems = new ArrayList<>();
        FileItem                    fileItem1 = mock(FileItem.class);
        FileItem                    fileItem2 = mock(FileItem.class);

        fileItems.add(fileItem1);
        fileItems.add(fileItem2);

        params.put(FileUploadServlet.PARAM_NAME, fileItems);

        mockFactories();
        mockMultipartAndPost();
        mockKeySessionAndFileUploadManager(attachmentKey);

        when(uploadManager.findUploadInfo(eq(attachmentKey))).thenReturn(Optional.of(uploadInfo));
        when(servletFileUpload.parseParameterMap(eq(request))).thenReturn(params);

        uploadServlet.doPost(request, response);

        verify(attachmentKeyFactory).get(eq(request));
        verify(fileUploadProcessorFactory).get(eq(uploadInfo), eq(uploadManager));
        verify(uploadProcessor).processFileItem(any(), eq(fileItem1));
        verify(uploadProcessor).processFileItem(any(), eq(fileItem2));
    }

    @Test
    public void testDoPostWithMultipartValidKeyWithoutInfo() throws Exception {
        mockFactories();
        mockMultipartAndPost();
        mockKeySessionAndFileUploadManager(attachmentKey);
        when(uploadManager.findUploadInfo(eq(attachmentKey))).thenReturn(Optional.empty());
        uploadServlet.doPost(request, response);
        verify(attachmentKeyFactory).get(eq(request));
        verify(response).sendError(eq(HttpServletResponse.SC_NOT_FOUND), any());
    }

    private void mockFactories() {
        when(fileUploadConfigFactory.get()).thenReturn(fileUploadConfig);
        when(fileUploadManagerFactory.get(eq(session))).thenReturn(uploadManager);
        when(fileUploadProcessorFactory.get(eq(uploadInfo), eq(uploadManager))).thenReturn(uploadProcessor);
        when(servletFileUploadFactory.get(eq(fileUploadConfig), eq(uploadInfo))).thenReturn(servletFileUpload);
        when(uploadResponseWriterFactory.get()).thenReturn(uploadResponseWriter);
    }

    private void mockMultipartAndPost() {
        when(request.getContentType()).thenReturn("multipart/");
        when(request.getMethod()).thenReturn("POST");
    }

    private void mockKeySessionAndFileUploadManager(AttachmentKey myKey) throws IOException {
        mockAttachmentKeyFactoryResult(myKey);
        mockSessionAndFileUploadManager();
    }

    private void mockAttachmentKeyFactoryResult(AttachmentKey myKey) throws IOException {
        when(attachmentKeyFactory.get(eq(request))).thenReturn(myKey);
    }

    private void mockSessionAndFileUploadManager() {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(FileUploadManager.SESSION_KEY)).thenReturn(uploadManager);
    }
}