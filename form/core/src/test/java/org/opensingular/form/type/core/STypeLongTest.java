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