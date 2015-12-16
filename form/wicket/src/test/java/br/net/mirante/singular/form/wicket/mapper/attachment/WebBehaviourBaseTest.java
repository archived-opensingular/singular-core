package br.net.mirante.singular.form.wicket.mapper.attachment;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.wicket.mock.MockRequestParameters;
import org.apache.wicket.mock.MockWebResponse;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.http.WebResponse;

import javax.servlet.http.HttpServletRequest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WebBehaviourBaseTest {
    protected ServletWebRequest request;
    protected MockRequestParameters parameters;
    protected HttpServletRequest containerRequest;
    protected MockWebResponse response;

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
        response = new MockWebResponse();
        return response;
    }
}
