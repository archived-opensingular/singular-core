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

package org.opensingular.ws.wkhtmltopdf.client;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Optional;

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

//     public String baseURL = "http://10.0.0.142/wkhtmltopdf-ws";
    public static String baseURL = "http://localhost:8080/wkhtmltopdf-ws";
    public static String context = "/converthtmltopdf";

    @BeforeClass
    public static void init() throws Exception {
        // System.setProperty("singular.ws.wkhtmltopdf.url","http://10.0.0.142/wkhtmltopdf-ws");
        System.setProperty("singular.ws.wkhtmltopdf.url", "http://localhost:8080/wkhtmltopdf-ws");
        System.setProperty("singular.wkhtml2pdf.home",
                "C:\\Desenv\\singular-platform-1.0.0\\native\\windows\\wkhtmltopdf");
    
          //TODO ver uma forma de levantar um server para rodar os rest's.
         //XXX atualmente nao funcionou os rest
        
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
        InputStream in = converter.convertStream(htmlToPdfDTO);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IOUtils.copy(in, baos);
        baos.close();
        in.close();

        Assert.assertTrue(baos.size() > 0);
        System.out.println(baos.size() + "Kb");
    }
    
    @Ignore
    @Test
    public void fileTest() throws IOException {
        RestfulHtmlToPdfConverter converter = RestfulHtmlToPdfConverter.createUsingDefaultConfig();

        HtmlToPdfDTO htmlToPdfDTO = new HtmlToPdfDTO();
        htmlToPdfDTO.setBody("<html><body>Teste para geracao de pdf via stream</body></html>");
        Optional<File> fileOpt = converter.convert(htmlToPdfDTO);

        Assert.assertTrue(fileOpt.isPresent());
        Assert.assertTrue(fileOpt.get().length() > 0);
        System.out.println(fileOpt.get().length() + "Kb");
    }

}
