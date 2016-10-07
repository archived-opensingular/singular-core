package org.opensingular.lib.support;


import org.junit.Assert;
import org.junit.Test;
import org.opensingular.lib.support.persistence.util.GenericEnumUserType;

public class GenericEnumUserTypeTest {

    /**
     * Verifica se a referencia estática ao nome da classe está de acordo com o pacote
     * onde a classe de fato está.
     */
    @Test
    public void testStaticClassReference(){
        Assert.assertEquals(GenericEnumUserType.CLASS_NAME, GenericEnumUserType.class.getName());
    }
}
