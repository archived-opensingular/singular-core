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


import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runners.MethodSorters;
import org.opensingular.flow.core.ExecuteWaitingTasksJob;
import org.opensingular.flow.core.Flow;
import org.opensingular.flow.core.FlowDefinitionCache;
import org.opensingular.flow.core.FlowInstance;
import org.opensingular.flow.core.SingularFlowException;
import org.opensingular.flow.core.entity.IEntityRoleInstance;
import org.opensingular.flow.persistence.entity.Actor;
import org.opensingular.flow.persistence.entity.TaskInstanceEntity;
import org.opensingular.flow.persistence.entity.TaskInstanceHistoryEntity;
import org.opensingular.flow.test.definicao.Peticao;
import org.opensingular.flow.test.support.TestFlowSupport;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.opensingular.flow.test.definicao.Peticao.PeticaoTask.AGUARDANDO_PUBLICACAO;
import static org.opensingular.flow.test.definicao.Peticao.PeticaoTask.DEFERIDO;
import static org.opensingular.flow.test.definicao.Peticao.PeticaoTask.INDEFERIDO;
import static org.opensingular.flow.test.definicao.Peticao.PeticaoTask.PUBLICADO;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PeticaoTest extends TestFlowSupport {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        Flow.setConf(mbpmBean, true);
    }

    @After
    public void tearDown() {
        FlowDefinitionCache.invalidateAll();
    }

//    @Test
//    @Ignore
//    public void showSwingDiagramTest() {
//        Logger.getLogger(PeticaoTest.class.getName()).log(Level.INFO, "Gerando diagrama...");
//        Peticao.main(null);
//        Logger.getLogger(PeticaoTest.class.getName()).log(Level.INFO, "Pronto!");
//    }

    @Test
    public void testeCriarInstanciaPeticao() {
        FlowInstance id = startInstance();
        FlowInstance id2 = Flow.getFlowInstance(id.getFullId());

        assertEqualsInstance(id, id2);
    }

    @Test
    public void executeTransitionWithoutTransitionName() {
        thrown.expect(SingularFlowException.class);

        FlowInstance instance = startInstance();
        instance.prepareTransition().go();
    }

    @Test
    public void executeHappyPath() {
        FlowInstance ip = startInstance();
        ip.prepareTransition(Peticao.APROVAR_TECNICO).go();
        ip.prepareTransition(Peticao.APROVAR_GERENTE).go();
        ip.prepareTransition(Peticao.PUBLICAR).go();

        assertLatestTaskName(PUBLICADO.getName(), ip);
    }

    @Test
    public void grantApplication() {
        FlowInstance ip = startInstance();
        ip.prepareTransition(Peticao.APROVAR_TECNICO).go();
        ip.prepareTransition(Peticao.DEFERIR).go();

        assertLatestTaskName(DEFERIDO.getName(), ip);
    }

    @Test
    public void rejectApplication() {
        FlowInstance ip = startInstance();
        ip.prepareTransition(Peticao.INDEFERIR).go();

        assertLatestTaskName(INDEFERIDO.getName(), ip);
    }

    @Test
    public void fuzzyFlow() {
        FlowInstance ip = startInstance();
        ip.prepareTransition(Peticao.APROVAR_TECNICO).go();
        ip.prepareTransition(Peticao.SOLICITAR_AJUSTE_ANALISE).go();
        ip.prepareTransition(Peticao.COLOCAR_EM_EXIGENCIA).go();
        ip.prepareTransition(Peticao.CUMPRIR_EXIGENCIA).go();
        ip.prepareTransition(Peticao.APROVAR_TECNICO).go();
        ip.prepareTransition(Peticao.APROVAR_GERENTE).go();
        ip.prepareTransition(Peticao.PUBLICAR).go();

        assertLatestTaskName(PUBLICADO.getName(), ip);
    }

    @Test
    public void naoDeveriaTerDataDeFim() {
        FlowInstance ip = startInstance();
        ip.prepareTransition(Peticao.APROVAR_TECNICO).go();

        assertNull("Instancia não deveria ter uma data de fim", ip.getEndDate());
        assertNull("Tarefa não deveria ter uma data de fim", ip.getLastTaskOrException().getEndDate());
    }

//
//    @Test
//    public void testeComUsuarioCriador() {
//        Peticao p = new Peticao();
//        FlowInstance ip = p.newPreStartInstance();
//        p
//        ip.executeTransition(Peticao.APROVAR_TECNICO);
//
//        assertNull("Instancia não deveria ter uma data de fim", ip.getEndDate());
//        assertNull("Tarefa não deveria ter uma data de fim", ip.getTaskNewer().getEndDate());
//    }

    @Test
    public void deveriaTerDataDeFim() {
        FlowInstance ip = startInstance();
        ip.prepareTransition(Peticao.INDEFERIR).go();

        assertNotNull("Instancia deveria ter uma data de fim", ip.getEndDate());
        assertNotNull("Tarefa deveria ter uma data de fim", ip.getLastTaskOrException().getEndDate());
    }

    @Test
    public void expirarAprovaGerente() {
        FlowInstance ip = startInstance();
        System.out.println("Id - " + ip.getId());
        ip.prepareTransition(Peticao.APROVAR_TECNICO).go();

        TaskInstanceEntity currentTask = ip.getCurrentTaskOrException().getEntityTaskInstance();
        addDaysToTaskTargetDate(currentTask, -3);
        testDAO.update(currentTask);

        new ExecuteWaitingTasksJob(null).run();

        assertLatestTaskName(AGUARDANDO_PUBLICACAO.getName(), ip);
    }

    @Test
    public void verificarUserTemPermissaoAcesso() {
        Actor user1 = testDAO.getSomeUser(1);
        FlowInstance ip = startInstance();
        ip.addOrReplaceUserRole(Peticao.PAPEL_GERENTE, user1);
        ip.prepareTransition(Peticao.APROVAR_TECNICO).go();
        assertTrue("Usuário não tem permissao", ip.canExecuteTask(user1));
    }

    @Test
    public void verificarUserNaoPermissaoAcesso() {
        Actor user1 = testDAO.getSomeUser(1);
        FlowInstance ip = startInstance();
        ip.addOrReplaceUserRole(Peticao.PAPEL_GERENTE, user1);
        assertFalse("Usuário não deveria ter permissao", ip.canExecuteTask(user1));
    }

    @Test
    public void trocarUsuarioPapel() {
        FlowInstance ip = startInstance();
        Actor user1 = testDAO.getSomeUser(1);
        Actor user2 = testDAO.getSomeUser(2);
        Actor user3 = testDAO.getSomeUser(3);
        Actor user4 = testDAO.getSomeUser(4);
        ip.addOrReplaceUserRole(Peticao.PAPEL_ANALISTA, user1);
        ip.addOrReplaceUserRole(Peticao.PAPEL_ANALISTA, user2);
        ip.addOrReplaceUserRole(Peticao.PAPEL_ANALISTA, user3);
        ip.addOrReplaceUserRole(Peticao.PAPEL_ANALISTA, user4);

        IEntityRoleInstance role = null;
        testDAO.refresh(ip.getEntity());
        for (IEntityRoleInstance entityRole : ip.getEntity().getRoles()) {
            if (entityRole.getRole().getAbbreviation().equalsIgnoreCase(Peticao.PAPEL_ANALISTA)) {
                role = entityRole;
            }
        }

        assertNotNull(role);
        assertEquals("Usuário diferente do esperado.", user4, role.getUser());
    }

    @Test
    public void atribuirPapelInexistente() {
        Actor user1 = testDAO.getSomeUser(1);
        thrown.expect(SingularFlowException.class);
        thrown.expectMessage("Não foi possível encontrar a role: Inexistente");

        FlowInstance ip = startInstance();
        ip.addOrReplaceUserRole("Inexistente", user1);
    }

    @Test
    public void atribuirPapelExistenteEmOutraTask() {
        Actor user1 = testDAO.getSomeUser(1);
        FlowInstance ip = startInstance();
        ip.addOrReplaceUserRole(Peticao.PAPEL_GERENTE, user1);
    }

    @Test
    public void verifyTaskAlocationHistory() {
        Actor user1 = testDAO.getSomeUser(1);
        Actor user2 = testDAO.getSomeUser(2);
        Integer counterHistory = testDAO.countHistory();
        assertNotNull(counterHistory);

        FlowInstance ip = startInstance();
        ip.addOrReplaceUserRole(Peticao.PAPEL_ANALISTA, user1);
        assertEquals(++counterHistory, testDAO.countHistory());

        ip.getCurrentTaskOrException().relocateTask(null, user2, false, "Testando...");
        assertEquals(++counterHistory, testDAO.countHistory());

        ip.prepareTransition(Peticao.APROVAR_TECNICO).go();
        ip.addOrReplaceUserRole(Peticao.PAPEL_GERENTE, user1);
        assertEquals(++counterHistory, testDAO.countHistory());

        ip.getCurrentTaskOrException().relocateTask(Flow.getUserIfAvailable(), user1, false, "Testando...");
        assertEquals(++counterHistory, testDAO.countHistory());

        List<TaskInstanceHistoryEntity> lastHistories = testDAO.retrieveLastHistories(4);
        assertEquals(Flow.getUserIfAvailable(), lastHistories.get(0).getAllocatorUser());
        assertEquals(user1, lastHistories.get(0).getAllocatedUser());
        assertEquals("Alocação", lastHistories.get(0).getType().getDescription());
        assertEquals("Papel definido", lastHistories.get(1).getType().getDescription());
        assertEquals(user2, lastHistories.get(2).getAllocatedUser());
        assertEquals("Alocação Automática", lastHistories.get(2).getType().getDescription());
        assertNotNull(String.format("Descrição: %s não finalizada", lastHistories.get(2).getType().getDescription()), lastHistories.get(2).getAllocationEndDate());
        assertEquals("Papel definido", lastHistories.get(3).getType().getDescription());
    }


    @Test
    public void allocationHistory() {
        Actor user1 = testDAO.getSomeUser(1);
        Actor user2 = testDAO.getSomeUser(2);

        FlowInstance ip = startInstance();
        ip.getCurrentTaskOrException().relocateTask(null, user1, false, "Primeira...");
        ip.getCurrentTaskOrException().relocateTask(null, user2, false, "Segunda...");
        ip.getCurrentTaskOrException().relocateTask(null, user1, false, "Volta para o inicial...");

        List<TaskInstanceHistoryEntity> lastHistories = testDAO.retrieveLastHistories(3);
        assertNull(String.format("Descrição: %s Causa: %s finalizada incorretamente",
                lastHistories.get(0).getType().getDescription(),
                lastHistories.get(0).getDescription()),
                lastHistories.get(0).getAllocationEndDate());
        assertNotNull(String.format("Descrição: %s Causa: %s não finalizada",
                lastHistories.get(1).getType().getDescription(),
                lastHistories.get(1).getDescription()),
                lastHistories.get(1).getAllocationEndDate());
        assertNotNull(String.format("Descrição: %s Causa: %s não finalizada",
                lastHistories.get(2).getType().getDescription(),
                lastHistories.get(2).getDescription()),
                lastHistories.get(2).getAllocationEndDate());

    }

    @Test
    public void verifyAutomaticTransitionHistory() {
        Integer counterHistory = testDAO.countHistory();
        assertNotNull(counterHistory);

        FlowInstance ip = startInstance();
        ip.prepareTransition(Peticao.APROVAR_TECNICO).go();

        TaskInstanceEntity currentTask = ip.getCurrentTaskOrException().getEntityTaskInstance();
        addDaysToTaskTargetDate(currentTask, -3);
        testDAO.update(currentTask);
        new ExecuteWaitingTasksJob(null).run();
        assertEquals(++counterHistory, testDAO.countHistory());

        List<TaskInstanceHistoryEntity> lastHistories = testDAO.retrieveLastHistories(1);
        assertEquals("Transição Automática", lastHistories.get(0).getType().getDescription());
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    //// MÉTODOS UTILITÁRIOS ////
    /////////////////////////////////////////////////////////////////////////////////////////////////
    private FlowInstance startInstance() {
        Peticao p = new Peticao();
        return p.prepareStartCall().createAndStart();
    }

    private void assertEqualsInstance(FlowInstance instance1, FlowInstance instance2) {
        Serializable cod1 = instance1.getEntity().getCod();
        Serializable cod2 = instance2.getEntity().getCod();

        assertEquals("As instâncias de processo são diferentes", cod1, cod2);
    }

    private void assertLatestTaskName(String expectedCurrentTaskName, FlowInstance instance) {
        assertEquals("Situação diferente do esperado",
                expectedCurrentTaskName,
                instance.getLastTaskOrException().getName());
    }

    private void addDaysToTaskTargetDate(TaskInstanceEntity taskInstance, int days) {
        Calendar newEndDate = Calendar.getInstance();
        newEndDate.setTime(taskInstance.getTargetEndDate());
        newEndDate.add(Calendar.DATE, days);

        taskInstance.setTargetEndDate(newEndDate.getTime());
    }
}
