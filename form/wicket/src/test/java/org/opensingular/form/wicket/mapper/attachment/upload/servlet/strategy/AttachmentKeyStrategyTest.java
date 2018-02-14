/*
 * Copyright (C) 2018 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.form.wicket.mapper.attachment.upload.servlet.strategy;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.opensingular.form.wicket.mapper.attachment.upload.*;
import org.opensingular.form.wicket.mapper.attachment.upload.info.UploadInfo;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(org.mockito.junit.MockitoJUnitRunner.class)
public class AttachmentKeyStrategyTest {

    private String keyValue = "123456";
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
    private UploadInfo uploadInfo;

    @Mock
    private FileUploadManager uploadManager;

    @Mock
    private FileUploadProcessor uploadProcessor;

    @Mock
    private FileUploadManagerFactory fileUploadManagerFactory;

    @Mock
    private FileUploadConfig fileUploadConfig;

    @Mock
    private UploadResponseWriter uploadResponseWriter;

    @Mock
    private ServletFileUploadFactory servletFileUploadFactory;

    @Mock
    private ServletFileUpload servletFileUpload;

    @Spy
    private AttachmentKeyStrategy attachmentKeyStrategy;

    @Before
    public void setUp() {
        when(attachmentKeyStrategy.makeAttachmentKeyFactory()).thenReturn(attachmentKeyFactory);
        when(attachmentKeyStrategy.makeFileUploadConfig()).thenReturn(fileUploadConfig);
        when(attachmentKeyStrategy.makeFileUploadManagerFactory()).thenReturn(fileUploadManagerFactory);
        when(attachmentKeyStrategy.makeServletFileUploadFactory()).thenReturn(servletFileUploadFactory);
        when(attachmentKeyStrategy.makeFileUploadProcessor()).thenReturn(uploadProcessor);
        when(attachmentKeyStrategy.makeUploadResponseWriter()).thenReturn(uploadResponseWriter);
        attachmentKeyStrategy.init();
    }

    @Test
    public void getUploadUrl() {
        String requestPath = mockRequestPath();
        assertEquals(requestPath, AttachmentKeyStrategy.getUploadUrl(request, attachmentKey));
    }

    private String mockRequestPath() {
        ServletContext servletContext = mock(ServletContext.class);
        String contextpath = "http://localhost:8080";
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getContextPath()).thenReturn(contextpath);
        return contextpath + AttachmentKeyStrategy.UPLOAD_URL + "/" + keyValue;
    }

    @Test
    public void testDoPostWithMultipartNullKey() throws Exception {
        mockAttachmentKeyFactoryResult(null);
        attachmentKeyStrategy.process(request, response);
        verify(attachmentKeyFactory).makeFromRequestPathOrNull(eq(request));
        verify(response).sendError(eq(HttpServletResponse.SC_BAD_REQUEST), any());
    }

    @Test
    public void testDoPostWithMultipartValidKey() throws Exception {

        Map<String, List<FileItem>> params = new HashMap<>();

        List<FileItem> fileItems = new ArrayList<>();
        FileItem fileItem1 = mock(FileItem.class);
        FileItem fileItem2 = mock(FileItem.class);

        fileItems.add(fileItem1);
        fileItems.add(fileItem2);

        params.put(AttachmentKeyStrategy.PARAM_NAME, fileItems);

        mockFactories();
        mockKeySessionAndFileUploadManager(attachmentKey);

        when(uploadManager.findUploadInfoByAttachmentKey(eq(attachmentKey))).thenReturn(Optional.of(uploadInfo));
        when(servletFileUpload.parseParameterMap(eq(request))).thenReturn(params);
        when(attachmentKeyStrategy.toFileUploadItem(fileItem1)).thenReturn(mock(FileUploadItem.class));
        when(attachmentKeyStrategy.toFileUploadItem(fileItem2)).thenReturn(mock(FileUploadItem.class));

        attachmentKeyStrategy.process(request, response);

        verify(attachmentKeyFactory).makeFromRequestPathOrNull(eq(request));
        verify(uploadProcessor).process(eq(attachmentKeyStrategy.toFileUploadItem(fileItem1)), eq(uploadInfo), eq(uploadManager));
        verify(uploadProcessor).process(eq(attachmentKeyStrategy.toFileUploadItem(fileItem2)), eq(uploadInfo), eq(uploadManager));
    }

    @Test
    public void testDoPostWithMultipartValidKeyWithoutInfo() throws Exception {
        mockFactories();
        mockKeySessionAndFileUploadManager(attachmentKey);
        when(uploadManager.findUploadInfoByAttachmentKey(eq(attachmentKey))).thenReturn(Optional.empty());
        attachmentKeyStrategy.process(request, response);
        verify(attachmentKeyFactory).makeFromRequestPathOrNull(eq(request));
        verify(response).sendError(eq(HttpServletResponse.SC_NOT_FOUND), any());
    }

    @Test
    public void testAcceptRequest() throws IOException, ServletException {
        ServletFileUploadStrategy strategy = Mockito.spy(AttachmentKeyStrategy.class);
        strategy.init();
        when(request.getRequestURL()).thenReturn(new StringBuffer(""));
        assertFalse(strategy.accept(request));
        String requestPath = mockRequestPath();
        when(request.getRequestURL()).thenReturn(new StringBuffer(requestPath));
        assertTrue(strategy.accept(request));
    }

    private void mockFactories() {
        when(fileUploadManagerFactory.getFileUploadManagerFromSessionOrMakeAndAttach(eq(session))).thenReturn(uploadManager);
        when(servletFileUploadFactory.makeServletFileUpload(eq(uploadInfo))).thenReturn(servletFileUpload);
    }

    private void mockKeySessionAndFileUploadManager(AttachmentKey myKey) throws IOException {
        mockAttachmentKeyFactoryResult(myKey);
        mockSessionAndFileUploadManager();
    }

    private void mockAttachmentKeyFactoryResult(AttachmentKey myKey) throws IOException {
        when(attachmentKeyFactory.makeFromRequestPathOrNull(eq(request))).thenReturn(myKey);
    }

    private void mockSessionAndFileUploadManager() {
        when(request.getSession()).thenReturn(session);
    }
}
