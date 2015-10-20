package br.net.mirante.singular.test;

import br.net.mirante.singular.definicao.DefinicaoProcessVersoes;
import br.net.mirante.singular.definicao.ProcessVersoes;
import br.net.mirante.singular.flow.core.Flow;
import br.net.mirante.singular.flow.core.ProcessDefinitionCache;
import br.net.mirante.singular.flow.core.ProcessInstance;
import br.net.mirante.singular.flow.core.TaskInstance;
import br.net.mirante.singular.flow.core.entity.IEntityProcessVersion;
import br.net.mirante.singular.flow.core.entity.IEntityRoleDefinition;
import br.net.mirante.singular.test.support.TestSupport;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public abstract class ProcessVersoesTest extends TestSupport {

    @Before
    public void setup() {
        assertNotNull(mbpmBean);
        Flow.setConf(mbpmBean, true);
    }

    @Test
    public void testarMudancaVersao() {

        ProcessVersoes processVersao1 = new DefinicaoProcessVersoes().newInstance();
        TaskInstance start1 = processVersao1.start();

        DefinicaoProcessVersoes.changeFlowToVersao2();

        ProcessDefinitionCache.invalidateAll();
        ProcessVersoes processVersao2 = new DefinicaoProcessVersoes().newInstance();
        TaskInstance start2 = processVersao2.start();

        ProcessInstance pi1 = start1.getProcessInstance();
        IEntityProcessVersion pd1 = pi1.getProcessDefinition().getEntityProcessVersion();
        ProcessInstance pi2 = start2.getProcessInstance();
        IEntityProcessVersion pd2 = pi2.getProcessDefinition().getEntityProcessVersion();
        assertNotEquals("As instancias de processo devem ser diferentes", pi1, pi2);
        assertNotEquals("As definições de processo devem ser diferentes", pd1, pd2);
    }

    @Test
    public void testarMudancaVersaoApenasPapeis() {

        ProcessVersoes processVersao1 = new DefinicaoProcessVersoes().newInstance();
        TaskInstance start1 = processVersao1.start();

        List<? extends IEntityRoleDefinition> rolesBefore = new ArrayList<>(
                start1.getProcessInstance().getProcessDefinition().getEntityProcessDefinition().getRoles());

        DefinicaoProcessVersoes.changeFlowToVersao1ComPapeis();

        ProcessDefinitionCache.invalidateAll();
        ProcessVersoes processVersao2 = new DefinicaoProcessVersoes().newInstance();
        TaskInstance start2 = processVersao2.start();

        ProcessInstance pi1 = start1.getProcessInstance();
        ProcessInstance pi2 = start2.getProcessInstance();
        List<? extends IEntityRoleDefinition> rolesAfter = start2.getProcessInstance().getProcessDefinition().getEntityProcessDefinition()
                .getRoles();

        assertNotEquals("As instancias de processo devem ser diferentes", pi1, pi2);
        assertNotEquals("As roles devem ser diferentes", rolesAfter.get(0), rolesBefore.get(0));
    }

    @Test
    public void nadaMudou() {


        ProcessVersoes processVersao1 = new DefinicaoProcessVersoes().newInstance();
        TaskInstance start1 = processVersao1.start();

        ProcessDefinitionCache.invalidateAll();
        ProcessVersoes processVersao2 = new DefinicaoProcessVersoes().newInstance();
        TaskInstance start2 = processVersao2.start();

        ProcessInstance pi1 = start1.getProcessInstance();
        IEntityProcessVersion pd1 = pi1.getProcessDefinition().getEntityProcessVersion();
        ProcessInstance pi2 = start2.getProcessInstance();
        IEntityProcessVersion pd2 = pi2.getProcessDefinition().getEntityProcessVersion();

        assertNotEquals("As instancias de processo devem ser diferentes", pi1, pi2);
        assertEquals("As definições de processo devem ser iguais", pd1, pd2);
    }
}
