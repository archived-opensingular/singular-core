package org.opensingular.singular.test;


import org.opensingular.singular.flow.test.definicao.Peticao;
import org.opensingular.flow.core.ExecuteWaitingTasksJob;
import org.opensingular.flow.core.Flow;
import org.opensingular.flow.core.ProcessDefinitionCache;
import org.opensingular.flow.core.ProcessInstance;
import org.opensingular.flow.core.SingularFlowException;
import org.opensingular.flow.core.entity.IEntityRoleInstance;
import org.opensingular.singular.persistence.entity.Actor;
import org.opensingular.singular.persistence.entity.TaskInstanceEntity;
import org.opensingular.singular.persistence.entity.TaskInstanceHistoryEntity;
import org.opensingular.singular.test.support.TestSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runners.MethodSorters;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.opensingular.singular.flow.test.definicao.Peticao.PeticaoTask.AGUARDANDO_PUBLICACAO;
import static org.opensingular.singular.flow.test.definicao.Peticao.PeticaoTask.DEFERIDO;
import static org.opensingular.singular.flow.test.definicao.Peticao.PeticaoTask.INDEFERIDO;
import static org.opensingular.singular.flow.test.definicao.Peticao.PeticaoTask.PUBLICADO;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public abstract class PeticaoTest extends TestSupport {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setup() {
        Flow.setConf(mbpmBean, true);
    }

    @After
    public void tearDown() {
        ProcessDefinitionCache.invalidateAll();
    }

    @Test
    @Ignore
    public void showSwingDiagramTest() {
        Logger.getLogger(PeticaoTest.class.getName()).log(Level.INFO, "Gerando diagrama...");
        Peticao.main(null);
        Logger.getLogger(PeticaoTest.class.getName()).log(Level.INFO, "Pronto!");
    }

    @Test
    public void testeCriarInstanciaPeticao() {
        ProcessInstance id = startInstance();
        ProcessInstance id2 = Flow.getProcessInstance(id.getFullId());

        assertEqualsInstance(id, id2);
    }

    @Test
    public void executeTransitionWithoutTransitionName() {
        thrown.expect(SingularFlowException.class);

        ProcessInstance instanciaPeticao = startInstance();
        instanciaPeticao.executeTransition();
    }

    @Test
    public void executeHappyPath() {
        ProcessInstance ip = startInstance();
        ip.executeTransition(Peticao.APROVAR_TECNICO);
        ip.executeTransition(Peticao.APROVAR_GERENTE);
        ip.executeTransition(Peticao.PUBLICAR);

        assertLatestTaskName(PUBLICADO.getName(), ip);
    }

    @Test
    public void grantApplication() {
        ProcessInstance ip = startInstance();
        ip.executeTransition(Peticao.APROVAR_TECNICO);
        ip.executeTransition(Peticao.DEFERIR);

        assertLatestTaskName(DEFERIDO.getName(), ip);
    }

    @Test
    public void rejectApplication() {
        ProcessInstance ip = startInstance();
        ip.executeTransition(Peticao.INDEFERIR);

        assertLatestTaskName(INDEFERIDO.getName(), ip);
    }

    @Test
    public void fuzzyFlow() {
        ProcessInstance ip = startInstance();
        ip.executeTransition(Peticao.APROVAR_TECNICO);
        ip.executeTransition(Peticao.SOLICITAR_AJUSTE_ANALISE);
        ip.executeTransition(Peticao.COLOCAR_EM_EXIGENCIA);
        ip.executeTransition(Peticao.CUMPRIR_EXIGENCIA);
        ip.executeTransition(Peticao.APROVAR_TECNICO);
        ip.executeTransition(Peticao.APROVAR_GERENTE);
        ip.executeTransition(Peticao.PUBLICAR);

        assertLatestTaskName(PUBLICADO.getName(), ip);
    }

    @Test
    public void naoDeveriaTerDataDeFim() {
        ProcessInstance ip = startInstance();
        ip.executeTransition(Peticao.APROVAR_TECNICO);

        assertNull("Instancia não deveria ter uma data de fim", ip.getEndDate());
        assertNull("Tarefa não deveria ter uma data de fim", ip.getLatestTask().getEndDate());
    }

//
//    @Test
//    public void testeComUsuarioCriador() {
//        Peticao p = new Peticao();
//        ProcessInstance ip = p.newInstance();
//        p
//        ip.executeTransition(Peticao.APROVAR_TECNICO);
//
//        assertNull("Instancia não deveria ter uma data de fim", ip.getEndDate());
//        assertNull("Tarefa não deveria ter uma data de fim", ip.getLatestTask().getEndDate());
//    }

    @Test
    public void deveriaTerDataDeFim() {
        ProcessInstance ip = startInstance();
        ip.executeTransition(Peticao.INDEFERIR);

        assertNotNull("Instancia deveria ter uma data de fim", ip.getEndDate());
        assertNotNull("Tarefa deveria ter uma data de fim", ip.getLatestTask().getEndDate());
    }

    @Test
    public void expirarAprovaGerente() {
        ProcessInstance ip = startInstance();
        System.out.println("Id - " + ip.getId());
        ip.executeTransition(Peticao.APROVAR_TECNICO);

        TaskInstanceEntity currentTask = ip.getCurrentTask().getEntityTaskInstance();
        addDaysToTaskTargetDate(currentTask, -3);
        testDAO.update(currentTask);

        new ExecuteWaitingTasksJob(null).run();

        assertLatestTaskName(AGUARDANDO_PUBLICACAO.getName(), ip);
    }

    @Test
    public void verificarUserTemPermissaoAcesso() {
        Actor user1 = testDAO.getSomeUser(1);
        ProcessInstance ip = startInstance();
        ip.addOrReplaceUserRole(Peticao.PAPEL_GERENTE, user1);
        ip.executeTransition(Peticao.APROVAR_TECNICO);
        assertTrue("Usuário não tem permissao", ip.canExecuteTask(user1));
    }

    @Test
    public void verificarUserNaoPermissaoAcesso() {
        Actor user1 = testDAO.getSomeUser(1);
        ProcessInstance ip = startInstance();
        ip.addOrReplaceUserRole(Peticao.PAPEL_GERENTE, user1);
        assertFalse("Usuário não deveria ter permissao", ip.canExecuteTask(user1));
    }

    @Test
    public void trocarUsuarioPapel() {
        ProcessInstance ip = startInstance();
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

        ProcessInstance ip = startInstance();
        ip.addOrReplaceUserRole("Inexistente", user1);
    }

    @Test
    public void atribuirPapelExistenteEmOutraTask() {
        Actor user1 = testDAO.getSomeUser(1);
        ProcessInstance ip = startInstance();
        ip.addOrReplaceUserRole(Peticao.PAPEL_GERENTE, user1);
    }

    @Test
    public void verificarHistoricoAlocacaoTarefa() {
        Actor user1 = testDAO.getSomeUser(1);
        Actor user2 = testDAO.getSomeUser(2);
        Integer counterHistory = testDAO.countHistoty();
        assertNotNull(counterHistory);

        ProcessInstance ip = startInstance();
        ip.addOrReplaceUserRole(Peticao.PAPEL_ANALISTA, user1);
        assertEquals(++counterHistory, testDAO.countHistoty());

        ip.getCurrentTask().relocateTask(null, user2, false, "Testando...");
        assertEquals(++counterHistory, testDAO.countHistoty());

        ip.executeTransition(Peticao.APROVAR_TECNICO);
        ip.addOrReplaceUserRole(Peticao.PAPEL_GERENTE, user1);
        assertEquals(++counterHistory, testDAO.countHistoty());

        ip.getCurrentTask().relocateTask(Flow.getUserIfAvailable(), user1, false, "Testando...");
        assertEquals(++counterHistory, testDAO.countHistoty());

        List<TaskInstanceHistoryEntity> lastHistories = testDAO.retrieveLastHistories(4);
        assertEquals(Flow.getUserIfAvailable(), lastHistories.get(0).getAllocatorUser());
        assertEquals(user1, lastHistories.get(0).getAllocatedUser());
        assertEquals("Alocação", lastHistories.get(0).getType().getDescription());
        assertEquals("Papel definido", lastHistories.get(1).getType().getDescription());
        assertEquals(user2, lastHistories.get(2).getAllocatedUser());
        assertEquals("Alocação Automática", lastHistories.get(2).getType().getDescription());
        assertNotNull(String.format("Descrição: %s não finalizada", lastHistories.get(2).getType().getDescription()), lastHistories.get(2).getEndDateAllocation());
        assertEquals("Papel definido", lastHistories.get(3).getType().getDescription());
    }


    @Test
    public void allocationHistory() {
        Actor user1 = testDAO.getSomeUser(1);
        Actor user2 = testDAO.getSomeUser(2);

        ProcessInstance ip = startInstance();
        ip.getCurrentTask().relocateTask(null, user1, false, "Primeira...");
        ip.getCurrentTask().relocateTask(null, user2, false, "Segunda...");
        ip.getCurrentTask().relocateTask(null, user1, false, "Volta para o inicial...");

        List<TaskInstanceHistoryEntity> lastHistories = testDAO.retrieveLastHistories(3);
        assertNull(String.format("Descrição: %s Causa: %s finalizada incorretamente",
                lastHistories.get(0).getType().getDescription(),
                lastHistories.get(0).getDescription()),
                lastHistories.get(0).getEndDateAllocation());
        assertNotNull(String.format("Descrição: %s Causa: %s não finalizada",
                lastHistories.get(1).getType().getDescription(),
                lastHistories.get(1).getDescription()),
                lastHistories.get(1).getEndDateAllocation());
        assertNotNull(String.format("Descrição: %s Causa: %s não finalizada",
                lastHistories.get(2).getType().getDescription(),
                lastHistories.get(2).getDescription()),
                lastHistories.get(2).getEndDateAllocation());

    }

    @Test
    public void verificarHistoricoTransicaoAutomatica() {
        Integer counterHistory = testDAO.countHistoty();
        assertNotNull(counterHistory);

        ProcessInstance ip = startInstance();
        ip.executeTransition(Peticao.APROVAR_TECNICO);

        TaskInstanceEntity currentTask = ip.getCurrentTask().getEntityTaskInstance();
        addDaysToTaskTargetDate(currentTask, -3);
        testDAO.update(currentTask);
        new ExecuteWaitingTasksJob(null).run();
        assertEquals(++counterHistory, testDAO.countHistoty());

        List<TaskInstanceHistoryEntity> lastHistories = testDAO.retrieveLastHistories(1);
        assertEquals("Transição Automática", lastHistories.get(0).getType().getDescription());
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    //// MÉTODOS UTILITÁRIOS ////
    /////////////////////////////////////////////////////////////////////////////////////////////////
    private ProcessInstance startInstance() {
        Peticao p = new Peticao();
        ProcessInstance id = p.newInstance();
        id.start();
        return id;
    }

    private void assertEqualsInstance(ProcessInstance instance1, ProcessInstance instance2) {
        Serializable cod1 = instance1.getEntity().getCod();
        Serializable cod2 = instance2.getEntity().getCod();

        assertEquals("As instâncias de processo são diferentes", cod1, cod2);
    }

    private void assertLatestTaskName(String expectedCurrentTaskName, ProcessInstance instanciaPeticao) {
        assertEquals("Situação diferente do esperado",
                expectedCurrentTaskName,
                instanciaPeticao.getLatestTask().getName());
    }

    private void addDaysToTaskTargetDate(TaskInstanceEntity taskInstance, int days) {
        Calendar newEndDate = Calendar.getInstance();
        newEndDate.setTime(taskInstance.getTargetEndDate());
        newEndDate.add(Calendar.DATE, days);

        taskInstance.setTargetEndDate(newEndDate.getTime());
    }
}
