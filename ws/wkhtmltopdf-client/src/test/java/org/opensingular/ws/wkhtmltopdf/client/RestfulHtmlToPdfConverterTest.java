package org.opensingular.ws.wkhtmltopdf.client;

import java.io.InputStream;

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

        InputStream in = usingDefaultConfig.convert(null);
        Assert.assertNull(in);
    }
}
