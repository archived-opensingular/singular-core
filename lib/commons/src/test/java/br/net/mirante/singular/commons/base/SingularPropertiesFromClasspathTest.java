package br.net.mirante.singular.commons.base;

import org.junit.Assert;
import org.junit.Test;

/**
 * Properties file is stored in /src/test/java/resources/singular.properties
 */
public class SingularPropertiesFromClasspathTest {

    private final String MOCK_PROPERTY_KEY   = "mock.property";
    private final String MOCK_PROPERTY_VALUE = "Mock Property";

    @Test
    public void test() throws Exception {
        SingularProperties.INSTANCE.reload();
        Assert.assertEquals(MOCK_PROPERTY_VALUE, SingularProperties.INSTANCE.getProperty(MOCK_PROPERTY_KEY));
    }

}