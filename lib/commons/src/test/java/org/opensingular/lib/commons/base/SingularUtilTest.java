package org.opensingular.lib.commons.base;

import org.junit.Assert;
import org.junit.Test;
import org.opensingular.lib.commons.dto.HtmlToPdfDTO;

public class SingularUtilTest {

    @Test
    public void toSHA1Test(){
        Assert.assertNotNull(SingularUtil.toSHA1(new HtmlToPdfDTO()));
    }

    @Test
    public void convertToJavaIdentityTest(){
        String test = " um teste para verificar o que ele converte.";

        String convertedValue = SingularUtil.convertToJavaIdentity(test, true);
        Assert.assertEquals("umTesteParaVerificarOQueEleConverte", convertedValue);

        convertedValue = SingularUtil.convertToJavaIdentity(test, true, false);
        Assert.assertEquals("UmTesteParaVerificarOQueEleConverte", convertedValue);
    }

    @Test(expected = NullPointerException.class)
    public void propragateExceptionTest(){
        SingularUtil.propagate(new NullPointerException());
    }
}
