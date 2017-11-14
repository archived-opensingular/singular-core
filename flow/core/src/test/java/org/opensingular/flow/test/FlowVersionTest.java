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

package org.opensingular.flow.test;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.opensingular.flow.core.Flow;
import org.opensingular.flow.core.FlowDefinitionCache;
import org.opensingular.flow.core.FlowInstance;
import org.opensingular.flow.core.TaskInstance;
import org.opensingular.flow.core.entity.IEntityFlowVersion;
import org.opensingular.flow.core.entity.IEntityRoleDefinition;
import org.opensingular.flow.test.definicao.MultiVersionSampleFlowDefinition;
import org.opensingular.flow.test.definicao.MultiVersionSampleFlowInstance;
import org.opensingular.flow.test.support.TestFlowSupport;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FlowVersionTest extends TestFlowSupport {

    @Before
    public void setUp() {
        assertNotNull(singularFlowConfigurationBean);
        Flow.setConf(singularFlowConfigurationBean, true);
    }

    @Test
    public void testVersionChange() {

        MultiVersionSampleFlowInstance flowVersion1 = new MultiVersionSampleFlowDefinition().prepareStartCall().createAndStart();
        TaskInstance start1 = flowVersion1.getCurrentTaskOrException();

        MultiVersionSampleFlowDefinition.changeFlowToVersion2();

        FlowDefinitionCache.invalidateAll();
        MultiVersionSampleFlowInstance flowVersion2 = new MultiVersionSampleFlowDefinition().prepareStartCall().createAndStart();
        TaskInstance start2 = flowVersion2.getCurrentTaskOrException();

        FlowInstance pi1 = start1.getFlowInstance();
        IEntityFlowVersion pd1 = pi1.getFlowDefinition().getEntityFlowVersion();
        FlowInstance pi2 = start2.getFlowInstance();
        IEntityFlowVersion pd2 = pi2.getFlowDefinition().getEntityFlowVersion();
        assertNotEquals("As instancias de fluxo devem ser diferentes", pi1, pi2);
        assertNotEquals("As definições de fluxo devem ser diferentes", pd1, pd2);
    }

    @Test
    public void testarMudancaVersaoApenasPapeis() {

        MultiVersionSampleFlowInstance flowVersao1 = new MultiVersionSampleFlowDefinition().prepareStartCall().createAndStart();
        TaskInstance start1 = flowVersao1.getCurrentTaskOrException();

        List<? extends IEntityRoleDefinition> rolesBefore = new ArrayList<>(
                start1.getFlowInstance().getFlowDefinition().getEntityFlowDefinition().getRoles());

        MultiVersionSampleFlowDefinition.changeFlowToVersao1ComPapeis();

        FlowDefinitionCache.invalidateAll();
        MultiVersionSampleFlowInstance flowVersao2 = new MultiVersionSampleFlowDefinition().prepareStartCall().createAndStart();
        TaskInstance start2 = flowVersao2.getCurrentTaskOrException();

        FlowInstance pi1 = start1.getFlowInstance();
        FlowInstance pi2 = start2.getFlowInstance();
        List<? extends IEntityRoleDefinition> rolesAfter = start2.getFlowInstance().getFlowDefinition().getEntityFlowDefinition()
                .getRoles();

        assertNotEquals("As instancias de fluxo devem ser diferentes", pi1, pi2);
        assertNotEquals("As roles devem ser diferentes", rolesAfter.get(0), rolesBefore.get(0));
    }

    @Test
    public void nadaMudou() {


        MultiVersionSampleFlowInstance flowVersion1 = new MultiVersionSampleFlowDefinition().prepareStartCall().createAndStart();
        TaskInstance start1 = flowVersion1.getCurrentTaskOrException();

        FlowDefinitionCache.invalidateAll();
        MultiVersionSampleFlowInstance flowVersion2 = new MultiVersionSampleFlowDefinition().prepareStartCall().createAndStart();
        TaskInstance start2 = flowVersion2.getCurrentTaskOrException();

        FlowInstance pi1 = start1.getFlowInstance();
        IEntityFlowVersion pd1 = pi1.getFlowDefinition().getEntityFlowVersion();
        FlowInstance pi2 = start2.getFlowInstance();
        IEntityFlowVersion pd2 = pi2.getFlowDefinition().getEntityFlowVersion();

        assertNotEquals("As instancias de fluxo devem ser diferentes", pi1, pi2);
        assertEquals("As definições de fluxo devem ser iguais", pd1, pd2);
    }
}
