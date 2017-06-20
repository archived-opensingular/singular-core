package org.opensingular.ws.wkhtmltopdf.client;

import java.io.File;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;
import org.opensingular.lib.commons.dto.HtmlToPdfDTO;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class RestfulHtmlToPdfStreamTest {

    
    public String baseURL = "http://10.0.0.142/wkhtmltopdf-ws"; 
    public String context = "/converthtmltopdf";
    

    public void testCreateUsingDefaultConfig(){
        Assert.assertNotNull(RestfulHtmlToPdfConverter.createUsingDefaultConfig());
    }


    public void testInstantiateByConstructor(){
        Assert.assertNotNull(new RestfulHtmlToPdfConverter("endpoint", ()-> new RestTemplate()));
    }


    public void testConvertWithNullValue(){
        RestfulHtmlToPdfConverter usingDefaultConfig =
                RestfulHtmlToPdfConverter.createUsingDefaultConfig();

        Optional<File> optionalValue = usingDefaultConfig.convert(null);
        Assert.assertFalse(optionalValue.isPresent());
    }
    
    
    private static RestTemplate defaultRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());
        return restTemplate;
    }

    @Test
    public void streamTest(){
        RestfulHtmlToPdfConverter converter = new RestfulHtmlToPdfConverter(baseURL + context ,RestfulHtmlToPdfStreamTest::defaultRestTemplate);
        HtmlToPdfDTO htmlToPdfDTO = new HtmlToPdfDTO();
        Optional<File> fileOpt = converter.convert(htmlToPdfDTO);
        
        
    }
}
