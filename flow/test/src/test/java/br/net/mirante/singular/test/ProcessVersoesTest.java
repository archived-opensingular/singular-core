package br.net.mirante.singular.test;

import br.net.mirante.singular.definicao.InstanceProcessVersoes;
import br.net.mirante.singular.definicao.ProcessVersoes;
import br.net.mirante.singular.flow.core.Flow;
import br.net.mirante.singular.flow.core.ProcessDefinitionCache;
import br.net.mirante.singular.flow.core.ProcessInstance;
import br.net.mirante.singular.flow.core.TaskInstance;
import br.net.mirante.singular.flow.core.entity.IEntityProcessVersion;
import br.net.mirante.singular.flow.core.entity.IEntityProcessRole;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ProcessVersoesTest extends TestSupport {

    @Before
    public void setup() {
        assertNotNull(mbpmBean);
        Flow.setConf(mbpmBean);
    }

    @Test
    public void testarMudancaVersao() {

        ProcessVersoes processVersao1 = new ProcessVersoes();
        TaskInstance start1 = processVersao1.start();

        InstanceProcessVersoes.changeFlowToVersao2();

        ProcessDefinitionCache.invalidateAll();
        ProcessVersoes processVersao2 = new ProcessVersoes();
        TaskInstance start2 = processVersao2.start();

        ProcessInstance pi1 = start1.getProcessInstance();
        IEntityProcessVersion pd1 = pi1.getProcessDefinition().getEntity();
        ProcessInstance pi2 = start2.getProcessInstance();
        IEntityProcessVersion pd2 = pi2.getProcessDefinition().getEntity();
        assertNotEquals("As instancias de processo devem ser diferentes", pi1, pi2);
        assertNotEquals("As definições de processo devem ser diferentes", pd1, pd2);
    }

    @Test
    public void testarMudancaVersaoApenasPapeis() {

        ProcessVersoes processVersao1 = new ProcessVersoes();
        TaskInstance start1 = processVersao1.start();

        List<? extends IEntityProcessRole> rolesBefore = new ArrayList<>(start1.getProcessInstance().getProcessDefinition().getEntity().getProcessDefinition().getRoles());

        InstanceProcessVersoes.changeFlowToVersao1ComPapeis();

        ProcessDefinitionCache.invalidateAll();
        ProcessVersoes processVersao2 = new ProcessVersoes();
        TaskInstance start2 = processVersao2.start();

        ProcessInstance pi1 = start1.getProcessInstance();
        ProcessInstance pi2 = start2.getProcessInstance();
        List<? extends IEntityProcessRole> rolesAfter = start2.getProcessInstance().getProcessDefinition().getEntity().getProcessDefinition().getRoles();

        assertNotEquals("As instancias de processo devem ser diferentes", pi1, pi2);
        assertNotEquals("As roles devem ser diferentes", rolesAfter.get(0), rolesBefore.get(0));
    }

    @Test
    public void nadaMudou() {

        ProcessVersoes processVersao1 = new ProcessVersoes();
        TaskInstance start1 = processVersao1.start();

        ProcessDefinitionCache.invalidateAll();
        ProcessVersoes processVersao2 = new ProcessVersoes();
        TaskInstance start2 = processVersao2.start();

        ProcessInstance pi1 = start1.getProcessInstance();
        IEntityProcessVersion pd1 = pi1.getProcessDefinition().getEntity();
        ProcessInstance pi2 = start2.getProcessInstance();
        IEntityProcessVersion pd2 = pi2.getProcessDefinition().getEntity();

        assertNotEquals("As instancias de processo devem ser diferentes", pi1, pi2);
        assertEquals("As definições de processo devem ser iguais", pd1, pd2);
    }
}
