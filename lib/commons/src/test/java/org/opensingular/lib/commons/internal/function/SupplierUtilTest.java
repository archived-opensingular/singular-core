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
