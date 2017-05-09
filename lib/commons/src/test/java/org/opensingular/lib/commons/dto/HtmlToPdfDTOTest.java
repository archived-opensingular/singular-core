package org.opensingular.lib.commons.dto;

import org.junit.Assert;
import org.junit.Test;

public class HtmlToPdfDTOTest {

    @Test
    public void testDTO(){
        HtmlToPdfDTO dto = new HtmlToPdfDTO("", "", "");

        Assert.assertNotNull(dto);

        dto.setHeader("header");
        dto.setBody("body");
        dto.setFooter("footer");

        Assert.assertEquals("header", dto.getHeader());
        Assert.assertEquals("body", dto.getBody());
        Assert.assertEquals("footer", dto.getFooter());
    }
}
