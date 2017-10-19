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

package org.opensingular.form.type.core;

import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.*;

public class STypeLongTest {

    private final STypeLong sTypeLong = new STypeLong();

    @Test(expected = RuntimeException.class)
    public void testFromStringWithBigNumber() throws Exception {
        sTypeLong.fromString("99999999999999999999999999999999999");
    }

    @Test(expected = RuntimeException.class)
    public void testFromStringWithLittleNumber() throws Exception {
        sTypeLong.fromString("-99999999999999999999999999999999999");
    }

    @Test(expected = RuntimeException.class)
    public void testconvertNotNativeNotStringithBigNumber() throws Exception {
        sTypeLong.convertNotNativeNotString(new BigInteger("99999999999999999999999999999999999"));
    }

    @Test(expected = RuntimeException.class)
    public void testconvertNotNativeNotStringWithLittleNumber() throws Exception {
        sTypeLong.convertNotNativeNotString(new BigInteger("-99999999999999999999999999999999999"));
    }

    @Test
    public void testconvertNotNativeNotStringWithNormalNumber() throws Exception {
        assertEquals(Long.MAX_VALUE, (long) sTypeLong.convertNotNativeNotString(new BigInteger(String.valueOf(Long.MAX_VALUE))));
        assertEquals(Long.MIN_VALUE, (long) sTypeLong.convertNotNativeNotString(new BigInteger(String.valueOf(Long.MIN_VALUE))));
        assertEquals(Long.MAX_VALUE, (long) sTypeLong.convertNotNativeNotString(Long.MAX_VALUE));
        assertEquals(Long.MIN_VALUE, (long) sTypeLong.convertNotNativeNotString(Long.MIN_VALUE));
    }

}