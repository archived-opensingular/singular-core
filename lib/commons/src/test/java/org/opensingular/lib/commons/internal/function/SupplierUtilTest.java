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

package org.opensingular.lib.commons.internal.function;


import org.junit.Assert;
import org.junit.Test;
import org.opensingular.lib.commons.base.SingularException;
import org.opensingular.lib.commons.internal.function.SupplierUtil;
import org.opensingular.lib.commons.lambda.ISupplier;

import java.io.Serializable;
import java.util.function.Supplier;

public class SupplierUtilTest {

    @Test
    public void nullSupplierTest() {
        Supplier<Integer> supp = SupplierUtil.cached(() -> {
            return (Integer) null;
        });
        Assert.assertEquals(supp.get(), null);
    }

    @Test
    public void IntegerSupplierTest() {
        Supplier<Integer> supp = SupplierUtil.cached(() -> {
            return (Integer) 12;
        });
        Assert.assertEquals(supp.get(), new Integer(12));
    }

    @Test
    public void serializableTest(){
        String valueString = "Valor passado pro supplier";
        ISupplier<String> serializable = SupplierUtil.serializable(valueString);

        Assert.assertEquals(valueString, serializable.get());

        serializable = SupplierUtil.serializable(null);
        Assert.assertNull(serializable.get());
    }

    @Test(expected = SingularException.class)
    public void serializableExceptionTest(){
        SupplierUtil.serializable(new NotSerializable());
    }

    private class NotSerializable {
        public String value;
    }

}
