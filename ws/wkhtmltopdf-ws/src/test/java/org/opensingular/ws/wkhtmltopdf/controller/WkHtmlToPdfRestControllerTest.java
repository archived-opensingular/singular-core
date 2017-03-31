package org.opensingular.ws.wkhtmltopdf.controller;

import org.junit.Assert;
import org.junit.Test;

public class WkHtmlToPdfRestControllerTest {

//    @Test
//    public void testConvertHtmlToPdf(){
//        Assert.assertNotNull(new WkHtmlToPdfRestController().convertHtmlToPdf(getHtmlToPdfDTO()));
//    }

//    private HtmlToPdfDTO getHtmlToPdfDTO(){
//        HtmlToPdfDTO dto = new HtmlToPdfDTO();
//        dto.setHeader("<header>" +
//                "<title>Mock html</title>" +
//                "</header>");
//        dto.setBody("<body><div>Qualquer coisa com um valor<div></body>");
//        dto.setFooter("<footer></footer>");
//
//        return dto;
//    }

    @Test
    public void testConvertHtmlToPdfWithNullValue(){
        Assert.assertNull(new WkHtmlToPdfRestController().convertHtmlToPdf(null));
    }

}
