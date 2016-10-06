package org.opensingular.singular.form.provider;

import org.junit.Assert;
import org.junit.Test;
import org.opensingular.form.provider.FreemarkerUtil;


public class FreemarkerUtilTest {

    @Test
    public void safeWrapSingle() throws Exception {
        String template = "${teste}";
        Assert.assertEquals("${(teste)!''}", FreemarkerUtil.safeWrap(template));
    }

    @Test
    public void safeWrapComplex() throws Exception {
        String template = "${teste.foo} - ${teste}";
        Assert.assertEquals("${(teste.foo)!''} - ${(teste)!''}", FreemarkerUtil.safeWrap(template));
    }

}