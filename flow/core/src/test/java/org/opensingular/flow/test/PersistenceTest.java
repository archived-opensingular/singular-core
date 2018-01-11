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

import java.sql.PreparedStatement;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.opensingular.flow.test.definicao.SampleRequirement.*;
import static org.opensingular.flow.test.definicao.SampleRequirement.SampleTask.*;

public class PersistenceTest extends TestFlowSupport {

    @Before
    public void setUp() {
        assertNotNull(mbpmBean);
        Flow.setConf(mbpmBean, true);
    }

    @Test
    public void testJoinTableCurrentTask() {
        FlowInstance pi  = new SampleRequirement().prepareStartCall().createAndStart();
        Integer      cod = pi.getEntity().getCod();
        sessionFactory.getCurrentSession().flush();
        //Clear da sessão para evidenciar a consulta como única.
        sessionFactory.getCurrentSession().clear();
        Logger.getLogger(getClass().getSimpleName()).info("##LOAD BEGIN: Clear na sessão, recarregando flow instance: ");
        FlowInstanceEntity pientity = (FlowInstanceEntity) sessionFactory.getCurrentSession().load(FlowInstanceEntity.class, cod);
        Assert.assertTrue(pientity.getCurrentTask().isPresent());
        Assert.assertEquals(pientity.getCurrentTask().get().getClass(), TaskInstanceEntity.class);
        Logger.getLogger(getClass().getSimpleName()).info("##LOAD END. ");
    }

    /**
     * Simulates an unordered sequence use for task instances. In that case we could end with newer tasks with numeric ids smaller than older ones.
     * This scenario covers oracle RAC with non ordered sequences.
     */
    @Test
    public void testUnorderedSequenceExecution() {
        FlowInstance pi = new SampleRequirement().prepareStartCall().createAndStart();
        pi.prepareTransition(APROVAR_TECNICO).go();
        sessionFactory.getCurrentSession().flush();
        List<Integer> pks = pi.getEntity().getTasks().stream().map(i -> i.getCod()).collect(Collectors.toList());
        System.out.println(pks);
        //reversing pk order to simulate unordered sequences like in a Oracle RAC database:
        sessionFactory.getCurrentSession().doWork(c -> {
            for (int i = 0; i < pks.size(); i++) {
                PreparedStatement ps = c.prepareStatement(" update DBSINGULAR.TB_INSTANCIA_TAREFA SET CO_INSTANCIA_TAREFA = " + (pks.get(pks.size() - i - 1) + 100) + " where CO_INSTANCIA_TAREFA = " + pks.get(i));
                ps.executeUpdate();
                ps.close();
            }
            c.commit();
        });


        sessionFactory.getCurrentSession().flush();
        sessionFactory.getCurrentSession().clear();
        FlowInstanceEntity flowInstanceEntity = (FlowInstanceEntity) pi.getEntity();
        List<Integer>      pksNovas           = flowInstanceEntity.getTasks().stream().map(i -> i.getCod()).collect(Collectors.toList());
        System.out.println(pksNovas);
        String currentTask = flowInstanceEntity.getCurrentTask().get().getTaskVersion().getAbbreviation();
        System.out.println(currentTask);
        Assert.assertEquals(AGUARDANDO_GERENTE.getKey(), currentTask);
    }
}
