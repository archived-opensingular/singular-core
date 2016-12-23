package org.opensingular.form.wicket.mapper.attachment.upload.writer;

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
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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