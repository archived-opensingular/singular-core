package org.opensingular.ws.wkhtmltopdf.client.mock;

import org.junit.Assert;
import org.junit.Test;
import org.opensingular.lib.commons.dto.HtmlToPdfDTO;
import org.opensingular.ws.wkhtmltopdf.client.MockHtmlToPdfConverter;


public class MockHtmlToPdfConverterTest {
    @Test
    public void convert() throws Exception {
        MockHtmlToPdfConverter mockHtmlToPdfConverter = new MockHtmlToPdfConverter();
        HtmlToPdfDTO htmlToPdfDTO = new HtmlToPdfDTO("","", "");
        Assert.assertTrue(mockHtmlToPdfConverter.convert(htmlToPdfDTO).isPresent());
    }

}