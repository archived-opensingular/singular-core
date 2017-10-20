/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

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
