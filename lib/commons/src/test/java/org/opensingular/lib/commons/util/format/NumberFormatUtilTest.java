package org.opensingular.lib.commons.util.format;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

public class NumberFormatUtilTest {

    @Test
    public void  formatValor() {
        BigDecimal valor = new BigDecimal("100000");
        Assert.assertEquals("R$ 100.000,00", NumberFormatUtil.formatValor(valor));
    }

    @Test
    public void  formatValorSemMoeda() {
        BigDecimal valor = new BigDecimal("100000");
        Assert.assertEquals("100.000,00", NumberFormatUtil.formatValorSemMoeda(valor));
    }

    @Test
    public void formatDecimalSeparator() {
        String valor = "100.000,00";
        Assert.assertEquals(new BigDecimal("100000.00"), NumberFormatUtil.formatDecimalSeparator(valor));
    }
}
