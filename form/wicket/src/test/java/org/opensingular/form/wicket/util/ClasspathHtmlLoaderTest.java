package org.opensingular.form.wicket.util;

import org.junit.Assert;
import org.junit.Test;

public class ClasspathHtmlLoaderTest {

    @Test
    public void loadHtmlTest(){
        ClasspathHtmlLoader string = new ClasspathHtmlLoader("string", String.class);
        Assert.assertNull(string.loadHtml());
    }
}
