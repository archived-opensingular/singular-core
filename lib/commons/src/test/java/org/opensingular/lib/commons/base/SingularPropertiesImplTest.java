package org.opensingular.lib.commons.base;

import org.junit.Assert;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

public class SingularPropertiesImplTest {

    @Test
    public void testTester() {
        Assert.assertEquals(SingularPropertiesTest.MOCK_PROPERTY_CLASSPATH_VALUE, SingularProperties.get().getProperty(SingularPropertiesTest.MOCK_PROPERTY_KEY));
        SingularPropertiesImpl.Tester.runInSandbox(impl -> {
            impl.setProperty(SingularPropertiesTest.MOCK_PROPERTY_KEY, "abc");
            Assert.assertEquals("abc", SingularProperties.get().getProperty(SingularPropertiesTest.MOCK_PROPERTY_KEY));
        });
        Assert.assertEquals(SingularPropertiesTest.MOCK_PROPERTY_CLASSPATH_VALUE, SingularProperties.get().getProperty(SingularPropertiesTest.MOCK_PROPERTY_KEY));
    }

    @Test
    public void testSaveRestore() {
        final String originalServerHome = SingularProperties.get().getSingularServerHome();
        Assert.assertEquals(SingularPropertiesTest.MOCK_PROPERTY_CLASSPATH_VALUE, SingularProperties.get().getProperty(SingularPropertiesTest.MOCK_PROPERTY_KEY));

        Object state1 = SingularPropertiesImpl.Tester.saveState();
        Assert.assertEquals(originalServerHome, SingularProperties.get().getSingularServerHome());
        Assert.assertEquals(SingularPropertiesTest.MOCK_PROPERTY_CLASSPATH_VALUE, SingularProperties.get().getProperty(SingularPropertiesTest.MOCK_PROPERTY_KEY));
        
        SingularPropertiesImpl.get().setSingularServerHome("/");
        Assert.assertEquals("/", SingularProperties.get().getSingularServerHome());
        Assert.assertEquals(SingularPropertiesTest.MOCK_PROPERTY_CLASSPATH_VALUE, SingularProperties.get().getProperty(SingularPropertiesTest.MOCK_PROPERTY_KEY));

        Object state2 = SingularPropertiesImpl.Tester.saveState();
        Assert.assertEquals("/", SingularProperties.get().getSingularServerHome());
        Assert.assertEquals(SingularPropertiesTest.MOCK_PROPERTY_CLASSPATH_VALUE, SingularProperties.get().getProperty(SingularPropertiesTest.MOCK_PROPERTY_KEY));
        
        SingularPropertiesImpl.get().setProperty(SingularPropertiesTest.MOCK_PROPERTY_KEY, "xxx");
        Assert.assertEquals("/", SingularProperties.get().getSingularServerHome());
        Assert.assertEquals("xxx", SingularProperties.get().getProperty(SingularPropertiesTest.MOCK_PROPERTY_KEY));

        SingularPropertiesImpl.Tester.restoreState(state2);
        Assert.assertEquals("/", SingularProperties.get().getSingularServerHome());
        Assert.assertEquals(SingularPropertiesTest.MOCK_PROPERTY_CLASSPATH_VALUE, SingularProperties.get().getProperty(SingularPropertiesTest.MOCK_PROPERTY_KEY));

        SingularPropertiesImpl.Tester.restoreState(state1);
        Assert.assertEquals(originalServerHome, SingularProperties.get().getSingularServerHome());
        Assert.assertEquals(SingularPropertiesTest.MOCK_PROPERTY_CLASSPATH_VALUE, SingularProperties.get().getProperty(SingularPropertiesTest.MOCK_PROPERTY_KEY));
    }

    @Test
    public void usingTesterTest(){
        SingularPropertiesImpl.Tester tester = new SingularPropertiesImpl.Tester(new Properties());

        tester.setProperty("key", "value");
        Assert.assertEquals("value", tester.getProperty("key"));
    }

    @Test(expected = SingularException.class)
    public void reloadAndOverrideTestException() throws MalformedURLException {
        SingularPropertiesImpl singularProperties = SingularPropertiesImpl.get();
        singularProperties.reloadAndOverrideWith(new URL("http://www.notexistentsite.com/"));
    }

    @Test
    public void containsKey(){
        SingularPropertiesImpl singularProperties = SingularPropertiesImpl.get();
        Assert.assertTrue(singularProperties.containsKey(SingularPropertiesTest.MOCK_PROPERTY_KEY));

        Assert.assertTrue(singularProperties.isTrue(SingularPropertiesTest.MOCK_TRUE_KEY));
        Assert.assertFalse(singularProperties.isTrue(SingularPropertiesTest.MOCK_FALSE_KEY));

        Assert.assertFalse(singularProperties.isFalse(SingularPropertiesTest.MOCK_TRUE_KEY));
        Assert.assertTrue(singularProperties.isFalse(SingularPropertiesTest.MOCK_FALSE_KEY));
    }
}
