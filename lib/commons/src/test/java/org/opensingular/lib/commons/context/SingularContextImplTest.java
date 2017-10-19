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

package org.opensingular.lib.commons.context;


import org.junit.Assert;
import org.junit.Test;
import org.opensingular.lib.commons.context.singleton.InstanceBoundedSingletonStrategy;

public class SingularContextImplTest {

    @Test
    public void testReset() throws Exception {
        SingularContextImpl.reset();
        Assert.assertFalse(SingularContextImpl.isConfigured());
    }

    @Test
    public void testSetup() throws Exception {
        SingularContextSetup.reset();
        SingularContextImpl.setup();
        Assert.assertTrue(SingularContextImpl.isConfigured());
    }


    @Test
    public void testSetupParameterized() throws Exception {
        SingularContextImpl.reset();
        InstanceBoundedSingletonStrategy instanceBoundedSingletonStrategy = new InstanceBoundedSingletonStrategy();
        Object                           o                                = new Object();
        instanceBoundedSingletonStrategy.put("test", o);
        SingularContextImpl.setup(instanceBoundedSingletonStrategy);
        Assert.assertEquals(o, ((SingularSingletonStrategy) SingularContextImpl.get()).get("test"));
    }

    @Test
    public void testSetupParameterized2() throws Exception {
        SingularContextImpl.reset();
        InstanceBoundedSingletonStrategy instanceBoundedSingletonStrategy = new InstanceBoundedSingletonStrategy();
        SingularContextImpl.setup(instanceBoundedSingletonStrategy);
        Object o = new Object();
        ((SingularSingletonStrategy) SingularContextImpl.get()).put("test", o);
        Assert.assertEquals(o, ((SingularSingletonStrategy) SingularContextImpl.get()).get("test"));
    }

    @Test(expected = SingularContextAlreadyConfiguredException.class)
    public void testSetupWithoutReset() throws Exception {
        SingularContextSetup.reset();
        SingularContextSetup.setup();
        SingularContextSetup.setup();
    }

    @Test
    public void testGet() throws Exception {
        SingularContextImpl.reset();
        SingularContext.get();
        Assert.assertTrue(SingularContextImpl.isConfigured());
    }
}
