package org.opensingular.lib.wicket.util.resource;

import org.junit.Assert;
import org.junit.Test;

public class ColorTest {

    @Test
    public void colorTest(){
        Color colorDefault = DefaultColors.DEFAULT;

        Color blueHoki = DefaultColors.BLUE_HOKI;

        Assert.assertEquals("font-blue-hoki", blueHoki.getFontCssClass());
        Assert.assertEquals("theme-font-color", colorDefault.getFontCssClass());

        Assert.assertEquals("yellow-crusta", DefaultColors.YELLOW_CRUSTA.toString());
    }
}
