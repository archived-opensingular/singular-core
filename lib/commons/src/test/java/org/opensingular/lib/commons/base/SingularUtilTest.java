package org.opensingular.lib.commons.base;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.opensingular.lib.commons.dto.HtmlToPdfDTO;

public class SingularUtilTest {

    @Test
    public void toSHA1Test(){
        String sha1 = SingularUtil.toSHA1(new HtmlToPdfDTO());
        Assert.assertEquals("1459ea152917ad896c7adb282ce767e90fd8d7f5", sha1);
    }

    @Test
    public void convertToJavaIdentityTest(){
        String test = " um teste para verificar o que ele converte.";

        String convertedValue = SingularUtil.convertToJavaIdentity(test, true);
        Assert.assertEquals("umTesteParaVerificarOQueEleConverte", convertedValue);

        convertedValue = SingularUtil.convertToJavaIdentity(test, true, false);
        Assert.assertEquals("UmTesteParaVerificarOQueEleConverte", convertedValue);
    }
}
