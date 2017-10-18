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

public class SingularSingletonStrategyTest {

    @Test
    public void testPutString() throws Exception {
        InstanceBoundedSingletonStrategy instanceBoundedSingletonStrategy = new InstanceBoundedSingletonStrategy();

        instanceBoundedSingletonStrategy.singletonize("test", () -> "nada");
        Assert.assertTrue(instanceBoundedSingletonStrategy.exists("test"));
        Assert.assertTrue(instanceBoundedSingletonStrategy.get("test").equals("nada"));

    }

    @Test
    public void testPutClass() throws Exception {
        InstanceBoundedSingletonStrategy instanceBoundedSingletonStrategy = new InstanceBoundedSingletonStrategy();

        instanceBoundedSingletonStrategy.singletonize(String.class, () -> "nada");
        Assert.assertTrue(instanceBoundedSingletonStrategy.exists(String.class));
        Assert.assertTrue(instanceBoundedSingletonStrategy.get(String.class).equals("nada"));

    }


    @Test(expected = SingularSingletonNotFoundException.class)
    public void testGetNotExisting() {
        InstanceBoundedSingletonStrategy instanceBoundedSingletonStrategy = new InstanceBoundedSingletonStrategy();
        instanceBoundedSingletonStrategy.get(String.class);

    }
}
