package br.net.mirante.singular.form.wicket.mapper.attachment;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.wicket.mock.MockRequestParameters;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.http.WebResponse;

public class WebBehaviourBaseTest {
    protected ServletWebRequest request;
    protected MockRequestParameters parameters;
    protected HttpServletRequest containerRequest;
    protected WebResponse response;

    protected ServletWebRequest mockRequest() throws FileUploadException {
        request = mock(ServletWebRequest.class);
        parameters = new MockRequestParameters();
        when(request.getRequestParameters()).thenReturn(parameters);
        containerRequest = mock(HttpServletRequest.class);
        when(containerRequest.getContentType()).thenReturn("multipart/form-data");
        when(request.getContainerRequest()).thenReturn(containerRequest);

        return request;
    }

    protected WebResponse mockResponse() {
        response = mock(WebResponse.class);
        when(response.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        return response;
    }
}
