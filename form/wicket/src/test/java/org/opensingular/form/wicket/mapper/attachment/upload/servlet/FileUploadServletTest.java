package org.opensingular.form.wicket.mapper.attachment.upload.servlet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.opensingular.form.wicket.mapper.attachment.upload.AttachmentKey;
import org.opensingular.form.wicket.mapper.attachment.upload.factory.AttachmentKeyFactory;
import org.opensingular.form.wicket.mapper.attachment.upload.factory.FileUploadProcessorFactory;
import org.opensingular.form.wicket.mapper.attachment.upload.info.UploadInfo;
import org.opensingular.form.wicket.mapper.attachment.upload.manager.FileUploadManager;
import org.opensingular.form.wicket.mapper.attachment.upload.manager.FileUploadManagerFactory;
import org.opensingular.form.wicket.mapper.attachment.upload.processor.FileUploadProcessor;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FileUploadServletTest {

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

    private FileUploadServlet uploadServlet;

    @Before
    public void setUp() {
        uploadServlet = new FileUploadServlet(fileUploadProcessorFactory, attachmentKeyFactory, fileUploadManagerFactory);
    }

    @Test
    public void getUploadUrl() throws Exception {

        String         key            = "123456";
        AttachmentKey  myKey          = new AttachmentKey(key);
        ServletContext servletContext = mock(ServletContext.class);
        String         contextpath    = "http://localhost:8080";

        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getContextPath()).thenReturn(contextpath);

        assertEquals(contextpath + FileUploadServlet.UPLOAD_URL + "/" + key, FileUploadServlet.getUploadUrl(request, myKey));
    }

    @Test
    public void testDoPostWithNonMultipart() throws Exception {
        uploadServlet.doPost(request, response);
        verify(response).sendError(eq(HttpServletResponse.SC_BAD_REQUEST), any());
    }

    @Test
    public void testDoPostWithMultipartNullKey() throws Exception {

        mockMultipartAndPost();
        when(attachmentKeyFactory.get(eq(request))).thenReturn(null);

        uploadServlet.doPost(request, response);

        verify(attachmentKeyFactory).get(eq(request));
        verify(response).sendError(eq(HttpServletResponse.SC_BAD_REQUEST), any());
    }


    @Test
    public void testDoPostWithMultipartValidKey() throws Exception {

        AttachmentKey myKey = new AttachmentKey("123456");

        mockMultipartAndPost();
        mockKeySessionAndFileUploadManager(myKey);
        when(fileUploadProcessorFactory.get(eq(request), eq(response), eq(uploadInfo), eq(uploadManager))).thenReturn(uploadProcessor);
        when(fileUploadManagerFactory.get(eq(session))).thenReturn(uploadManager);
        when(uploadManager.findUploadInfo(eq(myKey))).thenReturn(Optional.of(uploadInfo));

        uploadServlet.doPost(request, response);

        verify(attachmentKeyFactory).get(eq(request));
        verify(fileUploadProcessorFactory).get(eq(request), eq(response), eq(uploadInfo), eq(uploadManager));
        verify(uploadProcessor).handleFiles();
    }

    @Test
    public void testDoPostWithMultipartValidKeyWithoutInfo() throws Exception {

        AttachmentKey myKey = new AttachmentKey("123456");

        mockMultipartAndPost();
        mockKeySessionAndFileUploadManager(myKey);
        when(fileUploadManagerFactory.get(eq(session))).thenReturn(uploadManager);
        when(uploadManager.findUploadInfo(eq(myKey))).thenReturn(Optional.empty());
        when(fileUploadProcessorFactory.get(eq(request), eq(response), eq(uploadInfo), eq(uploadManager))).thenReturn(uploadProcessor);

        uploadServlet.doPost(request, response);

        verify(attachmentKeyFactory).get(eq(request));
        verify(response).sendError(eq(HttpServletResponse.SC_NOT_FOUND), any());
    }

    private void mockMultipartAndPost() {
        when(request.getContentType()).thenReturn("multipart/");
        when(request.getMethod()).thenReturn("POST");
    }

    private void mockKeySessionAndFileUploadManager(AttachmentKey myKey) throws IOException {
        when(attachmentKeyFactory.get(eq(request))).thenReturn(myKey);
        mockSessionAndFileUploadManager();
    }

    private void mockSessionAndFileUploadManager() {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(FileUploadManager.SESSION_KEY)).thenReturn(uploadManager);
    }
}