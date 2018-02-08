package org.opensingular.form.wicket.mapper.attachment.upload.servlet.strategy;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.substringAfterLast;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(org.mockito.junit.MockitoJUnitRunner.class)
public class ServletFileUploadStrategyHandlerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Spy
    private ServletFileUploadStrategyHandler servletFileUploadStrategyHandler;

    @Test
    public void testProcessFileUpload() throws ServletException, FileUploadException, IOException {
        mockPath(request, "");
        ServletFileUploadStrategy servletFileUploadStrategy = mock(ServletFileUploadStrategy.class);
        when(servletFileUploadStrategyHandler.chooseStrategy(request)).thenReturn(servletFileUploadStrategy);
        servletFileUploadStrategyHandler.processFileUpload(request, response);
        verify(servletFileUploadStrategy).init();
        verify(servletFileUploadStrategy).process(request, response);
    }

    @Test
    public void testDefaultStrategy() throws ServletException, FileUploadException, IOException {
        mockPath(request, "");
        ServletFileUploadStrategy servletFileUploadStrategy = servletFileUploadStrategyHandler.chooseStrategy(request);
        assertTrue(servletFileUploadStrategy == servletFileUploadStrategyHandler.DEFAULT_STRATEGY);
    }

    @Test
    public void testNonDefaultStrategy() {
        mockPath(request, "http://localhost:8080" + ServletFileUploadStrategy.UPLOAD_URL + "/123456");
        assertTrue(StringUtils.isNotBlank(substringAfterLast(defaultString(request.getRequestURL().toString()), AttachmentKeyStrategy.UPLOAD_URL + "/")));
        ServletFileUploadStrategy servletFileUploadStrategy = servletFileUploadStrategyHandler.chooseStrategy(request);
        assertTrue(servletFileUploadStrategy != servletFileUploadStrategyHandler.DEFAULT_STRATEGY);
    }

    @Test
    public void testNotEmptyAvailableStrategies() {
        assertTrue(!servletFileUploadStrategyHandler.getInstance().listAvailableStrategies().isEmpty());
    }

    @Test
    public void testNotNullDefaultValue() {
        assertNotNull(servletFileUploadStrategyHandler.DEFAULT_STRATEGY);
    }

    private void mockPath(HttpServletRequest httpServletRequest, String path) {
        when(httpServletRequest.getRequestURL()).thenReturn(new StringBuffer(path));
    }
}
