package org.opensingular.form.type.core;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

public class STypeMonetaryTest {

    @Test
    public void testDisplayFormat() {
        STypeMonetary sTypeMonetary = new STypeMonetary();
        Assert.assertEquals("R$ 10,00", sTypeMonetary.toStringDisplayDefault(BigDecimal.TEN));
        Assert.assertEquals("R$ 1.000.000,00", sTypeMonetary.toStringDisplayDefault(BigDecimal.valueOf(1_000_000)));
        Assert.assertEquals("R$ 1.000.000.000,00", sTypeMonetary.toStringDisplayDefault(BigDecimal.valueOf(1_000_000_000)));
        Assert.assertEquals("R$ 9,99", sTypeMonetary.toStringDisplayDefault(BigDecimal.valueOf(9.99)));
        Assert.assertEquals("R$ 0,99", sTypeMonetary.toStringDisplayDefault(BigDecimal.valueOf(0.99)));
    }
}
