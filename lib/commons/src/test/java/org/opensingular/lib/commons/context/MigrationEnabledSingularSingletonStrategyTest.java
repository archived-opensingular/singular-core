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

public class MigrationEnabledSingularSingletonStrategyTest {

    @Test
    public void testMigrate() throws Exception {
        SingularContextSetup.reset();

        //Default config
                ((SingularSingletonStrategy) SingularContext.get()).singletonize("nada", () -> "nada");
        Assert.assertTrue(SingularContext.get() instanceof SingularSingletonStrategy);

        //Another config
        InstanceBoundedSingletonStrategy another = new InstanceBoundedSingletonStrategy();
        another.put("nada2", "nada2");
        Assert.assertTrue(another instanceof SingularSingletonStrategy);
        another.putEntries(((SingularSingletonStrategy) SingularContext.get()));

        //reconfig
        SingularContextSetup.reset();
        SingularContextSetup.setup(another);

        Assert.assertTrue(((SingularSingletonStrategy) SingularContext.get()).exists("nada"));
        Assert.assertTrue(((SingularSingletonStrategy) SingularContext.get()).exists("nada2"));

    }
}
