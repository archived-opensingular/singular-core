package org.opensingular.form.type.country.brazil;

import org.junit.Test;
import org.opensingular.form.SDictionary;
import org.opensingular.form.type.core.SIString;

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

    @Test
    public void setValueTest(){
        STypeCEP typeCEP = SDictionary.create().getType(STypeCEP.class);
        SIString cep1 = typeCEP.newInstance();
        cep1.setValue("70863520");
        SIString cep2 = typeCEP.newInstance();
        cep2.setValue(cep1);
        assertEquals(typeCEP.format("70863520"), cep2.getValue());
    }


}