/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.lib.commons.base;

import org.junit.Assert;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

public class SingularPropertiesImplTest {

    @Test
    public void testTester() {
        Assert.assertEquals(SingularPropertiesTest.MOCK_PROPERTY_CLASSPATH_VALUE, SingularProperties.get(SingularPropertiesTest.MOCK_PROPERTY_KEY));
        SingularPropertiesImpl.Tester.runInSandbox(impl -> {
            impl.setProperty(SingularPropertiesTest.MOCK_PROPERTY_KEY, "abc");
            Assert.assertEquals("abc", SingularProperties.get(SingularPropertiesTest.MOCK_PROPERTY_KEY));
        });
        Assert.assertEquals(SingularPropertiesTest.MOCK_PROPERTY_CLASSPATH_VALUE, SingularProperties.get(SingularPropertiesTest.MOCK_PROPERTY_KEY));
    }

    @Test
    public void testSaveRestore() {
        final String originalServerHome = SingularProperties.get().getSingularServerHome();
        Assert.assertEquals(SingularPropertiesTest.MOCK_PROPERTY_CLASSPATH_VALUE, SingularProperties.get(SingularPropertiesTest.MOCK_PROPERTY_KEY));

        Object state1 = SingularPropertiesImpl.Tester.saveState();
        Assert.assertEquals(originalServerHome, SingularProperties.get().getSingularServerHome());
        Assert.assertEquals(SingularPropertiesTest.MOCK_PROPERTY_CLASSPATH_VALUE, SingularProperties.get(SingularPropertiesTest.MOCK_PROPERTY_KEY));
        
        SingularPropertiesImpl.get().setSingularServerHome("/");
        Assert.assertEquals("/", SingularProperties.get().getSingularServerHome());
        Assert.assertEquals(SingularPropertiesTest.MOCK_PROPERTY_CLASSPATH_VALUE, SingularProperties.get(SingularPropertiesTest.MOCK_PROPERTY_KEY));

        Object state2 = SingularPropertiesImpl.Tester.saveState();
        Assert.assertEquals("/", SingularProperties.get().getSingularServerHome());
        Assert.assertEquals(SingularPropertiesTest.MOCK_PROPERTY_CLASSPATH_VALUE, SingularProperties.get(SingularPropertiesTest.MOCK_PROPERTY_KEY));
        
        SingularPropertiesImpl.get().setProperty(SingularPropertiesTest.MOCK_PROPERTY_KEY, "xxx");
        Assert.assertEquals("/", SingularProperties.get().getSingularServerHome());
        Assert.assertEquals("xxx", SingularProperties.get(SingularPropertiesTest.MOCK_PROPERTY_KEY));

        SingularPropertiesImpl.Tester.restoreState(state2);
        Assert.assertEquals("/", SingularProperties.get().getSingularServerHome());
        Assert.assertEquals(SingularPropertiesTest.MOCK_PROPERTY_CLASSPATH_VALUE, SingularProperties.get(SingularPropertiesTest.MOCK_PROPERTY_KEY));

        SingularPropertiesImpl.Tester.restoreState(state1);
        Assert.assertEquals(originalServerHome, SingularProperties.get().getSingularServerHome());
        Assert.assertEquals(SingularPropertiesTest.MOCK_PROPERTY_CLASSPATH_VALUE, SingularProperties.get(SingularPropertiesTest.MOCK_PROPERTY_KEY));
    }

   /* @Test(expected = SingularException.class)
    public void reloadAndOverrideTestException() throws MalformedURLException {
        SingularPropertiesImpl singularProperties = SingularPropertiesImpl.get();
        singularProperties.reloadAndOverrideWith(new URL("http://www.notexistentsite.com/"));
    }*/

    @Test
    public void containsKey(){
        SingularPropertiesImpl singularProperties = SingularPropertiesImpl.get();
        Assert.assertNotNull(singularProperties.getProperty(SingularPropertiesTest.MOCK_PROPERTY_KEY));

        Assert.assertTrue(singularProperties.isTrue(SingularPropertiesTest.MOCK_TRUE_KEY));
        Assert.assertFalse(singularProperties.isTrue(SingularPropertiesTest.MOCK_FALSE_KEY));

        Assert.assertFalse(singularProperties.isFalse(SingularPropertiesTest.MOCK_TRUE_KEY));
        Assert.assertTrue(singularProperties.isFalse(SingularPropertiesTest.MOCK_FALSE_KEY));
    }
}
