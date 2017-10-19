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
import org.opensingular.flow.test.definicao.DefinicaoFlowVersoes;
import org.opensingular.flow.test.definicao.FlowVersoes;
import org.opensingular.flow.test.support.TestFlowSupport;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ProcessVersoesTest extends TestFlowSupport {

    @Before
    public void setUp() {
        assertNotNull(mbpmBean);
        Flow.setConf(mbpmBean, true);
    }

    @Test
    public void testarMudancaVersao() {

        FlowVersoes processVersao1 = new DefinicaoFlowVersoes().prepareStartCall().createAndStart();
        TaskInstance start1 = processVersao1.getCurrentTaskOrException();

        DefinicaoFlowVersoes.changeFlowToVersao2();

        FlowDefinitionCache.invalidateAll();
        FlowVersoes processVersao2 = new DefinicaoFlowVersoes().prepareStartCall().createAndStart();
        TaskInstance start2 = processVersao2.getCurrentTaskOrException();

        FlowInstance pi1 = start1.getFlowInstance();
        IEntityFlowVersion pd1 = pi1.getFlowDefinition().getEntityFlowVersion();
        FlowInstance pi2 = start2.getFlowInstance();
        IEntityFlowVersion pd2 = pi2.getFlowDefinition().getEntityFlowVersion();
        assertNotEquals("As instancias de processo devem ser diferentes", pi1, pi2);
        assertNotEquals("As definições de processo devem ser diferentes", pd1, pd2);
    }

    @Test
    public void testarMudancaVersaoApenasPapeis() {

        FlowVersoes processVersao1 = new DefinicaoFlowVersoes().prepareStartCall().createAndStart();
        TaskInstance start1 = processVersao1.getCurrentTaskOrException();

        List<? extends IEntityRoleDefinition> rolesBefore = new ArrayList<>(
                start1.getFlowInstance().getFlowDefinition().getEntityFlowDefinition().getRoles());

        DefinicaoFlowVersoes.changeFlowToVersao1ComPapeis();

        FlowDefinitionCache.invalidateAll();
        FlowVersoes processVersao2 = new DefinicaoFlowVersoes().prepareStartCall().createAndStart();
        TaskInstance start2 = processVersao2.getCurrentTaskOrException();

        FlowInstance pi1 = start1.getFlowInstance();
        FlowInstance pi2 = start2.getFlowInstance();
        List<? extends IEntityRoleDefinition> rolesAfter = start2.getFlowInstance().getFlowDefinition().getEntityFlowDefinition()
                .getRoles();

        assertNotEquals("As instancias de processo devem ser diferentes", pi1, pi2);
        assertNotEquals("As roles devem ser diferentes", rolesAfter.get(0), rolesBefore.get(0));
    }

    @Test
    public void nadaMudou() {


        FlowVersoes processVersao1 = new DefinicaoFlowVersoes().prepareStartCall().createAndStart();
        TaskInstance start1 = processVersao1.getCurrentTaskOrException();

        FlowDefinitionCache.invalidateAll();
        FlowVersoes processVersao2 = new DefinicaoFlowVersoes().prepareStartCall().createAndStart();
        TaskInstance start2 = processVersao2.getCurrentTaskOrException();

        FlowInstance pi1 = start1.getFlowInstance();
        IEntityFlowVersion pd1 = pi1.getFlowDefinition().getEntityFlowVersion();
        FlowInstance pi2 = start2.getFlowInstance();
        IEntityFlowVersion pd2 = pi2.getFlowDefinition().getEntityFlowVersion();

        assertNotEquals("As instancias de processo devem ser diferentes", pi1, pi2);
        assertEquals("As definições de processo devem ser iguais", pd1, pd2);
    }
}
