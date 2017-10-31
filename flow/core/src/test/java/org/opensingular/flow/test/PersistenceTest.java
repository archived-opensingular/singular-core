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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opensingular.flow.core.Flow;
import org.opensingular.flow.core.FlowInstance;
import org.opensingular.flow.persistence.entity.FlowInstanceEntity;
import org.opensingular.flow.persistence.entity.TaskInstanceEntity;
import org.opensingular.flow.test.definicao.SampleRequirement;
import org.opensingular.flow.test.support.TestFlowSupport;

import java.util.logging.Logger;

import static org.junit.Assert.assertNotNull;

public class PersistenceTest extends TestFlowSupport {

    @Before
    public void setUp() {
        assertNotNull(mbpmBean);
        Flow.setConf(mbpmBean, true);
    }

    @Test
    public void testJoinTableCurrentTask() {
        FlowInstance pi = new SampleRequirement().prepareStartCall().createAndStart();
        Integer cod = pi.getEntity().getCod();
        sessionFactory.getCurrentSession().flush();
        //Clear da sessão para evidenciar a consulta como única.
        sessionFactory.getCurrentSession().clear();
        Logger.getLogger(getClass().getSimpleName()).info("##LOAD BEGIN: Clear na sessão, recarregando flow instance: ");
        FlowInstanceEntity pientity = (FlowInstanceEntity) sessionFactory.getCurrentSession().load(FlowInstanceEntity.class, cod);
        Assert.assertNotNull(pientity.getCurrentTask());
        Assert.assertEquals(pientity.getCurrentTask().getClass(), TaskInstanceEntity.class);
        Logger.getLogger(getClass().getSimpleName()).info("##LOAD END. ");
    }
}
