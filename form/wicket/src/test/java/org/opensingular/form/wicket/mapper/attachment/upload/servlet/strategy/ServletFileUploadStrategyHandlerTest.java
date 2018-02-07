package org.opensingular.form.wicket.mapper.attachment.upload.servlet.strategy;

import org.apache.commons.fileupload.FileUploadException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static org.mockito.Mockito.verify;

@RunWith(org.mockito.junit.MockitoJUnitRunner.class)
public class ServletFileUploadStrategyHandlerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Spy
    private ServletFileUploadStrategyHandler servletFileUploadStrategyHandler;

    @Test
    public void test () throws ServletException, FileUploadException, IOException {
        //TODO douglas.silva - reescrever teste
//        verify(servletFileUploadStrategyHandler).processFileUpload(request, response);
    }
}
