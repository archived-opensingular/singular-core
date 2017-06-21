package org.opensingular.ws.wkhtmltopdf.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Server;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.opensingular.lib.commons.dto.HtmlToPdfDTO;


public class RestfulHtmlToPdfStreamTest {

    private static Server server;
    private static URI serverUri;

    // public String baseURL = "http://10.0.0.142/wkhtmltopdf-ws";
    public static String baseURL = "http://localhost:8080/wkhtmltopdf-ws";
    public static String context = "/converthtmltopdf";

    @BeforeClass
    public static void init() throws Exception {
        // System.setProperty("singular.ws.wkhtmltopdf.url","http://10.0.0.142/wkhtmltopdf-ws");
        System.setProperty("singular.ws.wkhtmltopdf.url", "http://localhost:8080/wkhtmltopdf-ws");
        System.setProperty("singular.wkhtml2pdf.home",
                "C:\\Desenv\\singular-platform-1.0.0\\native\\windows\\wkhtmltopdf");
    
          //TODO ver uma forma de levantar um server para rodar os rest's.  
//        server = new Server(8080);
//        WebAppContext webapp = new WebAppContext();
//        webapp.setContextPath("/wkhtmltopdf-ws");
//
//        File warFile = new File("src/test/resources/wkhtmltopdf-ws.war");
//        webapp.setWar(warFile.getAbsolutePath());
//
//        server.setHandler(webapp);
//        server.start();
//        server.setStopAtShutdown(true);
    }
    
    @AfterClass
    public static void destroy() throws Exception{
//        server.stop();
    }

    @Ignore
    @Test
    public void streamTest() throws IOException {
        RestfulHtmlToPdfConverter converter = RestfulHtmlToPdfConverter.createUsingDefaultConfig();

        HtmlToPdfDTO htmlToPdfDTO = new HtmlToPdfDTO();
        htmlToPdfDTO.setBody("<html><body>Teste para geracao de pdf via stream</body></html>");
        InputStream in = converter.convert(htmlToPdfDTO);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IOUtils.copy(in, baos);
        baos.close();
        in.close();

        Assert.assertTrue(baos.size() > 0);
        System.out.println(baos.size() + "Kb");
    }
}
