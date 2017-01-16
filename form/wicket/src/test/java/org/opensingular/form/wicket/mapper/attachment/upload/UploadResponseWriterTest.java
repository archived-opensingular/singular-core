/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

package org.opensingular.form.wicket.mapper.attachment.upload;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.opensingular.form.wicket.mapper.attachment.upload.info.UploadResponseInfo;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UploadResponseWriterTest {

    @Mock
    private HttpServletResponse response;

    private UploadResponseWriter uploadResponseWriter = new UploadResponseWriter();

    @Test
    public void testWriteJsonObjectResponseTo() throws IOException {

        StringWriter       stringWriter = new StringWriter();
        PrintWriter        printWriter  = new PrintWriter(stringWriter);
        String             jsonResponse = "{'name':'document.pdf'}";
        UploadResponseInfo responseInfo = mock(UploadResponseInfo.class);

        when(responseInfo.toString()).thenReturn(jsonResponse);
        when(response.getWriter()).thenReturn(printWriter);

        uploadResponseWriter.writeJsonObjectResponseTo(response, responseInfo);

        verify(response).setContentType(eq(UploadResponseWriter.APPLICATION_JSON));
        assertEquals(stringWriter.getBuffer().toString(), jsonResponse);

    }

    @Test
    public void testWriteJsonArrayResponseTo() throws IOException {

        StringWriter stringWriter = new StringWriter();
        PrintWriter  printWriter  = new PrintWriter(stringWriter);

        UploadResponseInfo responseInfoA = mock(UploadResponseInfo.class);
        UploadResponseInfo responseInfoB = mock(UploadResponseInfo.class);

        String jsonResponseA = "{'name':'document.pdf'}";
        String jsonResponseB = "{'name':'document2.pdf'}";
        String jsonResponse  = "[\"" + jsonResponseA + "\",\"" + jsonResponseB + "\"]";

        when(responseInfoA.toString()).thenReturn(jsonResponseA);
        when(responseInfoB.toString()).thenReturn(jsonResponseB);
        when(response.getWriter()).thenReturn(printWriter);

        uploadResponseWriter.writeJsonArrayResponseTo(response, Arrays.asList(responseInfoA, responseInfoB));

        verify(response).setContentType(eq(UploadResponseWriter.APPLICATION_JSON));
        assertEquals(stringWriter.getBuffer().toString(), jsonResponse);

    }

}