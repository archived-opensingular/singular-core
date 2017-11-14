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

package org.opensingular.flow.core;

import org.junit.Before;
import org.junit.Test;
import org.opensingular.flow.core.entity.IEntityFlowVersion;
import org.opensingular.flow.test.support.TestFlowSupport;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class SingularFlowConfigurationBeanTest extends TestFlowSupport {

    @Before
    public void setUp() {
        assertNotNull(singularFlowConfigurationBean);
        Flow.setConf(singularFlowConfigurationBean, true);
    }


    @Test
    public void inexistentFlowVersionShouldReturnNull() throws Exception {
        IEntityFlowVersion entityFlowVersion = singularFlowConfigurationBean.getPersistenceService().retrieveFlowVersionByCod(0);

        assertNull(entityFlowVersion);
    }
}