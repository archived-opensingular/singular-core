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

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.opensingular.lib.commons.dto.HtmlToPdfDTO;

public class SingularUtilTest {

    @Test
    public void toSHA1Test() {
        Assert.assertNotNull(SingularUtil.toSHA1(new HtmlToPdfDTO()));
    }

    @Test
    public void convertToJavaIdentityTest() {
        String test = " um teste para verificar o que ele converte.";

        String convertedValue = SingularUtil.convertToJavaIdentity(test, true);
        Assert.assertEquals("umTesteParaVerificarOQueEleConverte", convertedValue);

        convertedValue = SingularUtil.convertToJavaIdentity(test, true, false);
        Assert.assertEquals("UmTesteParaVerificarOQueEleConverte", convertedValue);
    }

    @Test(expected = NullPointerException.class)
    public void propragateExceptionTest() {
        SingularUtil.propagate(new NullPointerException());
    }

    public void areEqualTest() {
        assertTrue(SingularUtil.areEqual(1, 1, it -> it));

        class A {
            int    i;
            String s;
            Date   d;
            public A(int i, String s, Date d) {
                this.i = i;
                this.s = s;
                this.d = d;
            }
        }

        A a = new A(1, "1", new Date(12345));
        A aAlt = new A(1, "A", new Date(11111));
        A aNull = new A(1, null, null);
        A b = new A(2, "B", new Date());

        assertTrue(SingularUtil.areEqual(a, a));
        assertFalse(SingularUtil.areEqual(a, aAlt));
        assertTrue(SingularUtil.areEqual(a, aAlt, it -> it.i));
        assertFalse(SingularUtil.areEqual(a, aAlt, it -> it.i, it -> it.s, it -> it.d));
        assertFalse(SingularUtil.areEqual(a, aNull, it -> it.i));
        assertFalse(SingularUtil.areEqual(a, aNull, it -> it.i, it -> it.s, it -> it.d));
        assertFalse(SingularUtil.areEqual(a, b));
        assertFalse(SingularUtil.areEqual(a, b, it -> it.i, it -> it.s, it -> it.d));
    }
}
