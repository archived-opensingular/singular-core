package org.opensingular.ws.wkhtmltopdf.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opensingular.lib.commons.dto.HtmlToPdfDTO;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class RestfulHtmlToPdfStreamTest {

    
    //public String baseURL = "http://10.0.0.142/wkhtmltopdf-ws"; 
    public String baseURL = "http://localhost:8080/wkhtmltopdf-ws";
    public String context = "/converthtmltopdf";
    
    @BeforeClass
    public static void init(){
//        System.setProperty("singular.ws.wkhtmltopdf.url","http://10.0.0.142/wkhtmltopdf-ws");
        System.setProperty("singular.ws.wkhtmltopdf.url","http://localhost:8080/wkhtmltopdf-ws");
        System.setProperty("singular.wkhtml2pdf.home","C:\\Desenv\\singular-platform-1.0.0\\native\\windows\\wkhtmltopdf");
        
    }
    
    private static RestTemplate defaultRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());
        return restTemplate;
    }

//    @Test
    public void streamTestd(){
        RestfulHtmlToPdfConverter converter = new RestfulHtmlToPdfConverter(baseURL + context ,RestfulHtmlToPdfStreamTest::defaultRestTemplate);
        HtmlToPdfDTO htmlToPdfDTO = new HtmlToPdfDTO();
        htmlToPdfDTO.setBody("<html><body>Ola</body></html>");
        Optional<File> convert = converter.convert(htmlToPdfDTO);
        
    }
//    @Ignore
    @Test
    public void streamTest(){
        RestfulHtmlToPdfConverter converter = new RestfulHtmlToPdfConverter(baseURL + context ,RestfulHtmlToPdfStreamTest::defaultRestTemplate);
        HtmlToPdfDTO htmlToPdfDTO = new HtmlToPdfDTO();
        htmlToPdfDTO.setBody("<html><body>Ola</body></html>");
        InputStream in = converter.convert2(htmlToPdfDTO);
        
        FileOutputStream fos= null;
        try {
            fos = new FileOutputStream(new File("C:\\Desenv\\temp\\files\\gerados\\teste.pdf"));
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        try {
            if(fos!=null){
            IOUtils.copy(in,fos);
            
            fos.close();
            in.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
