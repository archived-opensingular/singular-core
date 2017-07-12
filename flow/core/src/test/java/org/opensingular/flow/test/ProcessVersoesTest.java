package org.opensingular.flow.test;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.opensingular.flow.core.Flow;
import org.opensingular.flow.core.ProcessDefinitionCache;
import org.opensingular.flow.core.FlowInstance;
import org.opensingular.flow.core.TaskInstance;
import org.opensingular.flow.core.entity.IEntityProcessVersion;
import org.opensingular.flow.core.entity.IEntityRoleDefinition;
import org.opensingular.flow.test.definicao.DefinicaoFlowVersoes;
import org.opensingular.flow.test.definicao.FlowVersoes;
import org.opensingular.flow.test.support.TestFlowSupport;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

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

        ProcessDefinitionCache.invalidateAll();
        FlowVersoes processVersao2 = new DefinicaoFlowVersoes().prepareStartCall().createAndStart();
        TaskInstance start2 = processVersao2.getCurrentTaskOrException();

        FlowInstance pi1 = start1.getFlowInstance();
        IEntityProcessVersion pd1 = pi1.getProcessDefinition().getEntityProcessVersion();
        FlowInstance pi2 = start2.getFlowInstance();
        IEntityProcessVersion pd2 = pi2.getProcessDefinition().getEntityProcessVersion();
        assertNotEquals("As instancias de processo devem ser diferentes", pi1, pi2);
        assertNotEquals("As definições de processo devem ser diferentes", pd1, pd2);
    }

    @Test
    public void testarMudancaVersaoApenasPapeis() {

        FlowVersoes processVersao1 = new DefinicaoFlowVersoes().prepareStartCall().createAndStart();
        TaskInstance start1 = processVersao1.getCurrentTaskOrException();

        List<? extends IEntityRoleDefinition> rolesBefore = new ArrayList<>(
                start1.getFlowInstance().getProcessDefinition().getEntityProcessDefinition().getRoles());

        DefinicaoFlowVersoes.changeFlowToVersao1ComPapeis();

        ProcessDefinitionCache.invalidateAll();
        FlowVersoes processVersao2 = new DefinicaoFlowVersoes().prepareStartCall().createAndStart();
        TaskInstance start2 = processVersao2.getCurrentTaskOrException();

        FlowInstance pi1 = start1.getFlowInstance();
        FlowInstance pi2 = start2.getFlowInstance();
        List<? extends IEntityRoleDefinition> rolesAfter = start2.getFlowInstance().getProcessDefinition().getEntityProcessDefinition()
                .getRoles();

        assertNotEquals("As instancias de processo devem ser diferentes", pi1, pi2);
        assertNotEquals("As roles devem ser diferentes", rolesAfter.get(0), rolesBefore.get(0));
    }

    @Test
    public void nadaMudou() {


        FlowVersoes processVersao1 = new DefinicaoFlowVersoes().prepareStartCall().createAndStart();
        TaskInstance start1 = processVersao1.getCurrentTaskOrException();

        ProcessDefinitionCache.invalidateAll();
        FlowVersoes processVersao2 = new DefinicaoFlowVersoes().prepareStartCall().createAndStart();
        TaskInstance start2 = processVersao2.getCurrentTaskOrException();

        FlowInstance pi1 = start1.getFlowInstance();
        IEntityProcessVersion pd1 = pi1.getProcessDefinition().getEntityProcessVersion();
        FlowInstance pi2 = start2.getFlowInstance();
        IEntityProcessVersion pd2 = pi2.getProcessDefinition().getEntityProcessVersion();

        assertNotEquals("As instancias de processo devem ser diferentes", pi1, pi2);
        assertEquals("As definições de processo devem ser iguais", pd1, pd2);
    }
}
