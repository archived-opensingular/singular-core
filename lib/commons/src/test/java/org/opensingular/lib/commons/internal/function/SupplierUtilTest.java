package org.opensingular.lib.commons.internal.function;


import org.junit.Assert;
import org.junit.Test;
import org.opensingular.lib.commons.internal.function.SupplierUtil;

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
}
