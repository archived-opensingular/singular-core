package br.net.mirante.singular.pet.module.test;


import br.net.mirante.singular.pet.commons.spring.security.ServerContext;
import org.apache.commons.validator.routines.UrlValidator;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestServerContext {

    /**
     * Contexto registrado no arquivo properties server.properties para o petcionamento
     */
    private static final String CONTEXTO_PETICIONAMENTO = "/pettest";

    private HttpServletRequest getRequest() {
        HttpServletRequest mockedRequest = Mockito.mock(HttpServletRequest.class);
        Mockito.when(mockedRequest.getContextPath()).thenReturn("/singular");
        Mockito.when(mockedRequest.getPathInfo()).thenReturn("/singular" + CONTEXTO_PETICIONAMENTO + "/caixaentrada");
        return mockedRequest;
    }

    @Test
    public void testContextFromRequest() {
        Assert.assertEquals(ServerContext.PETICIONAMENTO, ServerContext.getContextFromRequest(getRequest()));
    }

    @Test
    public void testRegexFromContextPath() {
        Assert.assertEquals(CONTEXTO_PETICIONAMENTO + "/*", ServerContext.PETICIONAMENTO.getContextPath());

        Pattern p = Pattern.compile(ServerContext.PETICIONAMENTO.getPathRegex());
        Matcher m = p.matcher(ServerContext.PETICIONAMENTO.getContextPath());

        Assert.assertTrue(m.find());
        Assert.assertEquals(0, m.groupCount());
        Assert.assertEquals(ServerContext.PETICIONAMENTO.getContextPath(), m.group());
    }


    @Test
    public void testUrlPath() {
        UrlValidator validator = new UrlValidator();

        String path = ServerContext.ANALISE.getUrlPath() + "/pagina/testeurl";
        String url = "http://127.0.0.1:8080" + path;

        Assert.assertTrue(validator.isValid(url));

        Pattern p = Pattern.compile(ServerContext.ANALISE.getPathRegex());
        Matcher m = p.matcher(url);

        Assert.assertTrue(m.find());
        Assert.assertEquals(0, m.groupCount());
        Assert.assertEquals(path, m.group());


    }

}
