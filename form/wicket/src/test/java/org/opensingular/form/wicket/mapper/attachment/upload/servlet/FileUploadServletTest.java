package org.opensingular.form.wicket.mapper.attachment.upload.servlet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.opensingular.form.type.core.attachment.IAttachmentRef;
import org.opensingular.form.wicket.mapper.attachment.upload.AttachmentKey;
import org.opensingular.form.wicket.mapper.attachment.upload.factory.FileUploadObjectFactory;
import org.opensingular.form.wicket.mapper.attachment.upload.info.UploadInfo;
import org.opensingular.form.wicket.mapper.attachment.upload.manager.FileUploadManager;
import org.opensingular.form.wicket.mapper.attachment.upload.processor.FileUploadProcessor;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FileUploadServletTest {

    @Mock
    HttpServletRequest req;

    @Mock
    HttpSession session;

    @Mock
    HttpServletResponse resp;

    @Mock
    FileUploadObjectFactory factory;

    @Mock
    UploadInfo info;

    @Mock
    FileUploadManager fum;

    @Mock
    FileUploadProcessor processor;

    FileUploadServlet servlet;

    @Before
    public void setUp() {
        servlet = new FileUploadServlet(factory);
    }

    @Test
    public void getUploadUrl() throws Exception {

        String         key            = "123456";
        AttachmentKey  myKey          = new AttachmentKey(key);
        ServletContext servletContext = mock(ServletContext.class);
        String         contextpath    = "http://localhost:8080";

        when(req.getServletContext()).thenReturn(servletContext);
        when(servletContext.getContextPath()).thenReturn(contextpath);

        assertEquals(contextpath + FileUploadServlet.UPLOAD_URL + "/" + key, FileUploadServlet.getUploadUrl(req, myKey));
    }

    @Test
    public void consumeFile() throws Exception {
        mockSessionAndFileUploadManager();

        Function<IAttachmentRef, String> callback = (x) -> "ok";
        String                           key      = "123465";

        FileUploadServlet.consumeFile(req, key, callback);
        verify(fum).consumeFile(eq(key), eq(callback));
    }

    @Test
    public void testDoPostWithNonMultipart() throws Exception {
        servlet.doPost(req, resp);
        verify(resp).sendError(eq(HttpServletResponse.SC_BAD_REQUEST), any());
    }

    @Test
    public void testDoPostWithMultipartNullKey() throws Exception {

        mockMultipartAndPost();
        when(factory.newAttachmentKey(eq(req))).thenReturn(null);

        servlet.doPost(req, resp);

        verify(factory).newAttachmentKey(eq(req));
        verify(resp).sendError(eq(HttpServletResponse.SC_BAD_REQUEST), any());
    }


    @Test
    public void testDoPostWithMultipartValidKey() throws Exception {

        AttachmentKey myKey = new AttachmentKey("123456");

        mockMultipartAndPost();
        mockKeySessionAndFileUploadManager(myKey);
        when(fum.findUploadInfo(eq(myKey))).thenReturn(Optional.of(info));

        when(factory.newFileUploadProcessor(eq(req), eq(resp), eq(info), eq(fum), eq(factory))).thenReturn(processor);

        servlet.doPost(req, resp);

        verify(factory).newAttachmentKey(eq(req));
        verify(factory).newFileUploadProcessor(eq(req), eq(resp), eq(info), eq(fum), eq(factory));
        verify(processor).handleFiles();
    }

    @Test
    public void testDoPostWithMultipartValidKeyWithoutInfo() throws Exception {

        AttachmentKey myKey = new AttachmentKey("123456");

        mockMultipartAndPost();
        mockKeySessionAndFileUploadManager(myKey);
        when(fum.findUploadInfo(eq(myKey))).thenReturn(Optional.empty());

        when(factory.newFileUploadProcessor(eq(req), eq(resp),
                eq(info), eq(fum), eq(factory))
        ).thenReturn(processor);

        servlet.doPost(req, resp);

        verify(factory).newAttachmentKey(eq(req));
        verify(resp).sendError(eq(HttpServletResponse.SC_NOT_FOUND), any());
    }

    private void mockMultipartAndPost() {
        when(req.getContentType()).thenReturn("multipart/");
        when(req.getMethod()).thenReturn("POST");
    }

    private void mockKeySessionAndFileUploadManager(AttachmentKey myKey) throws IOException {
        when(factory.newAttachmentKey(eq(req))).thenReturn(myKey);
        mockSessionAndFileUploadManager();
    }

    private void mockSessionAndFileUploadManager() {
        when(req.getSession()).thenReturn(session);
        when(session.getAttribute(FileUploadManager.SESSION_KEY)).thenReturn(fum);
    }
}