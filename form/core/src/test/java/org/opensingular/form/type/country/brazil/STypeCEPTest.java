package org.opensingular.form.type.country.brazil;

import org.junit.Test;

import static org.junit.Assert.*;


public class STypeCEPTest {

    private STypeCEP type = new STypeCEP();

    @Test
    public void unformatTest() {
        assertEquals("72210202", type.unformat("72.210-202"));
    }

    @Test
    public void testFormat() {
        assertEquals("72.210-202", type.format("72.210-202"));
        assertEquals("72.210-202", type.format("72210202"));
        assertEquals("02.210-202", type.format("02.210-202"));
        assertEquals("02.210-202", type.format("02210202"));
        assertEquals("04.256-320", type.format("04.256-320"));
        assertEquals("04.256-320", type.format("04256320"));
    }


}