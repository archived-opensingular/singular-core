package org.opensingular.lib.wicket.util.resource;

import org.junit.Assert;
import org.junit.Test;

public class ColorTest {

    @Test
    public void colorTest(){
        Color colorDefault = Color.DEFAULT;

        Color blueHoki = Color.BLUE_HOKI;

        Assert.assertEquals("font-blue-hoki", blueHoki.getFontCssClass());
        Assert.assertEquals("theme-font-color", colorDefault.getFontCssClass());

        Assert.assertEquals("yellow-crusta", Color.YELLOW_CRUSTA.toString());
    }
}
