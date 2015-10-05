package br.net.mirante.singular.test;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runners.MethodSorters;

import br.net.mirante.singular.ConstantesUtil;
import br.net.mirante.singular.definicao.InstanciaPeticao;
import br.net.mirante.singular.definicao.Peticao;
import br.net.mirante.singular.flow.core.ExecuteWaitingTasksJob;
import br.net.mirante.singular.flow.core.MBPM;
import br.net.mirante.singular.flow.core.ProcessDefinitionCache;
import br.net.mirante.singular.flow.core.ProcessInstance;
import br.net.mirante.singular.flow.core.SingularFlowException;
import br.net.mirante.singular.flow.core.entity.IEntityRole;
import br.net.mirante.singular.persistence.entity.TaskInstance;
import br.net.mirante.singular.persistence.entity.TaskInstanceHistory;

import static br.net.mirante.singular.definicao.Peticao.PeticaoTask.AGUARDANDO_PUBLICACAO;
import static br.net.mirante.singular.definicao.Peticao.PeticaoTask.DEFERIDO;
import static br.net.mirante.singular.definicao.Peticao.PeticaoTask.INDEFERIDO;
import static br.net.mirante.singular.definicao.Peticao.PeticaoTask.PUBLICADO;
import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PeticaoTest extends TestSupport {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setup() {
        assertNotNull(mbpmBean);
        MBPM.setConf(mbpmBean);
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
        InstanciaPeticao id = startInstance();
        InstanciaPeticao id2 = MBPM.getProcessInstance(id.getFullId());

        assertEqualsInstance(id, id2);
    }

    @Test
    public void executeTransitionWithoutTransitionName() {
        thrown.expect(SingularFlowException.class);

        InstanciaPeticao instanciaPeticao = startInstance();
        instanciaPeticao.executeTransition();
    }

    @Test
    public void executeHappyPath() {
        InstanciaPeticao ip = startInstance();
        ip.executeTransition(Peticao.APROVAR_TECNICO);
        ip.executeTransition(Peticao.APROVAR_GERENTE);
        ip.executeTransition(Peticao.PUBLICAR);

        assertLatestTaskName(PUBLICADO.getName(), ip);
    }

    @Test
    public void grantApplication() {
        InstanciaPeticao ip = startInstance();
        ip.executeTransition(Peticao.APROVAR_TECNICO);
        ip.executeTransition(Peticao.DEFERIR);

        assertLatestTaskName(DEFERIDO.getName(), ip);
    }

    @Test
    public void rejectApplication() {
        InstanciaPeticao ip = startInstance();
        ip.executeTransition(Peticao.INDEFERIR);

        assertLatestTaskName(INDEFERIDO.getName(), ip);
    }

    @Test
    public void fuzzyFlow() {
        InstanciaPeticao ip = startInstance();
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
        InstanciaPeticao ip = startInstance();
        ip.executeTransition(Peticao.APROVAR_TECNICO);

        assertNull("Instancia não deveria ter uma data de fim", ip.getEndDate());
        assertNull("Tarefa não deveria ter uma data de fim", ip.getLatestTask().getEndDate());
    }

    @Test
    public void deveriaTerDataDeFim() {
        InstanciaPeticao ip = startInstance();
        ip.executeTransition(Peticao.INDEFERIR);

        assertNotNull("Instancia deveria ter uma data de fim", ip.getEndDate());
        assertNotNull("Tarefa deveria ter uma data de fim", ip.getLatestTask().getEndDate());
    }

    @Test
    public void expirarAprovaGerente() {
        InstanciaPeticao ip = startInstance();
        System.out.println("Id - " + ip.getId());
        ip.executeTransition(Peticao.APROVAR_TECNICO);

        TaskInstance currentTask = (TaskInstance) ip.getEntity().getCurrentTask();
        addDaysToTaskTargetDate(currentTask, -3);
        testDAO.update(currentTask);

        new ExecuteWaitingTasksJob(null).run();

        assertLatestTaskName(AGUARDANDO_PUBLICACAO.getName(), ip);
    }

    @Test
    public void verificarUserTemPermissaoAcesso() {
        InstanciaPeticao ip = startInstance();
        ip.addOrReplaceUserRole(Peticao.PAPEL_GERENTE, ConstantesUtil.USER_1);
        ip.executeTransition(Peticao.APROVAR_TECNICO);
        assertTrue("Usuário não tem permissao", ip.canExecuteTask(ConstantesUtil.USER_1));
    }

    @Test
    public void verificarUserNaoPermissaoAcesso() {
        InstanciaPeticao ip = startInstance();
        ip.addOrReplaceUserRole(Peticao.PAPEL_GERENTE, ConstantesUtil.USER_1);
        assertFalse("Usuário não deveria ter permissao", ip.canExecuteTask(ConstantesUtil.USER_1));
    }

    @Test
    public void trocarUsuarioPapel() {
        InstanciaPeticao ip = startInstance();
        ip.addOrReplaceUserRole(Peticao.PAPEL_ANALISTA, ConstantesUtil.USER_1);
        ip.addOrReplaceUserRole(Peticao.PAPEL_ANALISTA, ConstantesUtil.USER_2);
        ip.addOrReplaceUserRole(Peticao.PAPEL_ANALISTA, ConstantesUtil.USER_3);
        ip.addOrReplaceUserRole(Peticao.PAPEL_ANALISTA, ConstantesUtil.USER_4);

        IEntityRole role = null;
        testDAO.refresh(ip.getEntity());
        for (IEntityRole entityRole : ip.getEntity().getRoles()) {
            if (entityRole.getRole().getAbbreviation().equalsIgnoreCase(Peticao.PAPEL_ANALISTA)) {
                role = entityRole;
            }
        }

        assertNotNull(role);
        assertEquals("Usuário diferente do esperado.", ConstantesUtil.USER_4, role.getUser());
    }

    @Test
    public void atribuirPapelInexistente() {
        thrown.expect(SingularFlowException.class);
        thrown.expectMessage("Não foi possível encontrar a role: Inexistente");

        InstanciaPeticao ip = startInstance();
        ip.addOrReplaceUserRole("Inexistente", ConstantesUtil.USER_1);
    }

    @Test
    public void atribuirPapelExistenteEmOutraTask() {
        InstanciaPeticao ip = startInstance();
        ip.addOrReplaceUserRole(Peticao.PAPEL_GERENTE, ConstantesUtil.USER_1);
    }

    @Test
    public void verificarHistoricoAlocacaoTarefa() {
        Integer counterHistory = testDAO.countHistoty();
        assertNotNull(counterHistory);

        InstanciaPeticao ip = startInstance();
        ip.addOrReplaceUserRole(Peticao.PAPEL_ANALISTA, ConstantesUtil.USER_2);
        assertEquals(++counterHistory, testDAO.countHistoty());

        ip.getCurrentTask().relocateTask(null, ConstantesUtil.USER_2, false, "Testando...");
        assertEquals(++counterHistory, testDAO.countHistoty());

        ip.executeTransition(Peticao.APROVAR_TECNICO);
        ip.addOrReplaceUserRole(Peticao.PAPEL_GERENTE, ConstantesUtil.USER_1);
        assertEquals(++counterHistory, testDAO.countHistoty());

        ip.getCurrentTask().relocateTask(mbpmBean.getUserIfAvailable(), ConstantesUtil.USER_1, false, "Testando...");
        assertEquals(++counterHistory, testDAO.countHistoty());

        List<TaskInstanceHistory> lastHistories = testDAO.retrieveLastHistories(4);
        assertEquals(mbpmBean.getUserIfAvailable(), lastHistories.get(0).getAllocatorUser());
        assertEquals(ConstantesUtil.USER_1, lastHistories.get(0).getAllocatedUser());
        assertEquals("Alocação", lastHistories.get(0).getTaskHistoryType().getDescription());
        assertEquals("Papel definido", lastHistories.get(1).getTaskHistoryType().getDescription());
        assertEquals(ConstantesUtil.USER_2, lastHistories.get(2).getAllocatedUser());
        assertEquals("Alocação Automática", lastHistories.get(2).getTaskHistoryType().getDescription());
        assertEquals("Papel definido", lastHistories.get(3).getTaskHistoryType().getDescription());
    }

    @Test
    public void verificarHistoricoTransicaoAutomatica() {
        Integer counterHistory = testDAO.countHistoty();
        assertNotNull(counterHistory);

        InstanciaPeticao ip = startInstance();
        ip.executeTransition(Peticao.APROVAR_TECNICO);

        TaskInstance currentTask = (TaskInstance) ip.getEntity().getCurrentTask();
        addDaysToTaskTargetDate(currentTask, -3);
        testDAO.update(currentTask);
        new ExecuteWaitingTasksJob(null).run();
        assertEquals(++counterHistory, testDAO.countHistoty());

        List<TaskInstanceHistory> lastHistories = testDAO.retrieveLastHistories(1);
        assertEquals("Transição Automática", lastHistories.get(0).getTaskHistoryType().getDescription());
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    ////                               MÉTODOS UTILITÁRIOS                                       ////
    /////////////////////////////////////////////////////////////////////////////////////////////////
    private InstanciaPeticao startInstance() {
        InstanciaPeticao id = new InstanciaPeticao();
        id.start();
        return id;
    }

    private void assertEqualsInstance(ProcessInstance instance1, ProcessInstance instance2) {
        Serializable cod1 = instance1.getEntity().getCod();
        Serializable cod2 = instance2.getEntity().getCod();

        assertEquals("As instâncias de processo são diferentes", cod1, cod2);
    }

    private void assertLatestTaskName(String expectedCurrentTaskName, InstanciaPeticao instanciaPeticao) {
        assertEquals("Situação diferente do esperado",
                expectedCurrentTaskName,
                instanciaPeticao.getLatestTask().getName());
    }

    private void addDaysToTaskTargetDate(TaskInstance taskInstance, int days) {
        Calendar newEndDate = Calendar.getInstance();
        newEndDate.setTime(taskInstance.getTargetEndDate());
        newEndDate.add(Calendar.DATE, days);

        taskInstance.setTargetEndDate(newEndDate.getTime());
    }
}
