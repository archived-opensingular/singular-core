package org.opensingular.ws.wkhtmltopdf.client;

import java.io.File;
import java.io.InputStream;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;

public class RestfulHtmlToPdfConverterTest {

    @Test
    public void testCreateUsingDefaultConfig(){
        Assert.assertNotNull(RestfulHtmlToPdfConverter.createUsingDefaultConfig());
    }

    @Test
    public void testInstantiateByConstructor(){
        Assert.assertNotNull(new RestfulHtmlToPdfConverter("endpoint"));
    }

    @Test
    public void testConvertWithNullValue(){
        RestfulHtmlToPdfConverter usingDefaultConfig =
                RestfulHtmlToPdfConverter.createUsingDefaultConfig();

        Optional<File> convert = usingDefaultConfig.convert(null);
        Assert.assertFalse(convert.isPresent());
    }
    
    @Test
    public void testConvertStreamWithNullValue(){
        RestfulHtmlToPdfConverter usingDefaultConfig =
                RestfulHtmlToPdfConverter.createUsingDefaultConfig();

        InputStream in = usingDefaultConfig.convertStream(null);
        Assert.assertNull(in);
    }
}
