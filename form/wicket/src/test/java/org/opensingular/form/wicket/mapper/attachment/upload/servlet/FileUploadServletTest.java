/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.form.wicket.mapper.attachment.upload.servlet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.opensingular.form.wicket.mapper.attachment.upload.servlet.strategy.ServletFileUploadStrategyHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(org.mockito.junit.MockitoJUnitRunner.class)
public class FileUploadServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Spy
    private FileUploadServlet uploadServlet;

    @Test
    public void testDoPostWithNonMultipart() throws Exception {
        uploadServlet.doPost(request, response);
        verify(response).sendError(eq(HttpServletResponse.SC_BAD_REQUEST), any());
    }

    @Test
    public void testDelegateProcess() throws Exception {
        mockMultipartAndPost();
        when(request.getRequestURL()).thenReturn(new StringBuffer(""));
        uploadServlet.doPost(request, response);
        //TODO rever teste
        verify(response).sendError(eq(HttpServletResponse.SC_BAD_REQUEST), any());
    }

    private void mockMultipartAndPost() {
        when(request.getContentType()).thenReturn("multipart/");
        when(request.getMethod()).thenReturn("POST");
    }

}