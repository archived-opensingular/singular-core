package org.opensingular.ws.wkhtmltopdf.client;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.Optional;

public class RestfulHtmlToPdfConverterTest {

    @Test
    public void testCreateUsingDefaultConfig(){
        Assert.assertNotNull(RestfulHtmlToPdfConverter.createUsingDefaultConfig());
    }

    @Test
    public void testInstantiateByConstructor(){
        Assert.assertNotNull(new RestfulHtmlToPdfConverter("endpoint", ()-> new RestTemplate()));
    }

    @Test
    public void testConvertWithNullValue(){
        RestfulHtmlToPdfConverter usingDefaultConfig =
                RestfulHtmlToPdfConverter.createUsingDefaultConfig();

        Optional<File> optionalValue = usingDefaultConfig.convert(null);
        Assert.assertFalse(optionalValue.isPresent());
    }
}
