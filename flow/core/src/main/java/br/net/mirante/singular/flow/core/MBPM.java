package br.net.mirante.singular.flow.core;

import br.net.mirante.singular.flow.core.entity.IEntityProcessInstance;
import br.net.mirante.singular.flow.core.entity.IEntityTaskInstance;
import br.net.mirante.singular.flow.util.view.Lnk;
import br.net.mirante.singular.flow.schedule.ScheduleDataBuilder;
import br.net.mirante.singular.flow.schedule.ScheduledJob;

public class MBPM {

    private static AbstractMbpmBean mbpmBean;

    private MBPM() {
    }

    private static void init() {
        for (final ProcessDefinition<?> processDefinition : getMbpmBean().getDefinicoes()) {
            for (final MTaskJava task : processDefinition.getFluxo().getJavaTasks()) {
                if (!task.isImmediateExecution()) {
                    getMbpmBean().getScheduleService().schedule(new ScheduledJob(task.getCompleteName(), task.getScheduleData(), () -> getMbpmBean().executeTask(task)));
                }
            }
            for (ProcessScheduledJob scheduledJob : processDefinition.getFluxo().getScheduledJobs()) {
                getMbpmBean().getScheduleService().schedule(scheduledJob);
            }
        }
        getMbpmBean().getScheduleService().schedule(new ExecuteWaitingTasksJob(ScheduleDataBuilder.buildHourly(1)));
        getMbpmBean().getScheduleService().schedule(new DeleteOldCompletedTasksJob(ScheduleDataBuilder.buildDaily(6, 0)));
        getMbpmBean().init();
    }

    public static void setConf(AbstractMbpmBean conf) {
        mbpmBean = conf;
        init();
    }

    public static AbstractMbpmBean getMbpmBean() {
        if (mbpmBean == null) {
            throw new RuntimeException("Configuração do fluxo não realizada");
        }
        return mbpmBean;
    }

    public static <K extends ProcessDefinition<?>> K getDefinicao(Class<K> classe) {
        return getMbpmBean().getProcessDefinition(classe);
    }

    @SuppressWarnings("unchecked")
    public static <K extends ProcessDefinition<?>> K getDefinicao(String sigla) {
        return (K) getMbpmBean().getProcessDefinition(sigla);
    }

    public static <T extends VariableWrapper> T newInitialVariables(Class<? extends ProcessDefinition<?>> processDefinitionClass,
            Class<T> variableWrapperClass) {
        ProcessDefinition<?> processDefinition = getDefinicao(processDefinitionClass);
        return processDefinition.newInitialVariables(variableWrapperClass);
    }

    public static TaskInstance getInstanciaTarefa(IEntityTaskInstance dadosTarefa) {
        return new TaskInstance(dadosTarefa);
    }

    @SuppressWarnings("unchecked")
    public static <K extends ProcessInstance> K getInstancia(IEntityProcessInstance dadosInstanciaProcesso) {
        return (K) getMbpmBean().getInstancia(dadosInstanciaProcesso);
    }

    public static <X extends ProcessInstance> X getInstancia(String id) {
        return getMbpmBean().getInstancia(id);
    }

    public static String generateID(ProcessInstance instancia) {
        return getMbpmBean().generateID(instancia);
    }

    public static String generateID(TaskInstance instanciaTarefa) {
        return getMbpmBean().generateID(instanciaTarefa);
    }

    public static MUser getUserSeDisponivel() {
        return getMbpmBean().getUserSeDisponivel();
    }

    public static AbstractNotificadores getNotificadores() {
        return getMbpmBean().getNotificadores();
    }

    static boolean isPessoaAtivaParaTerTarefa(MUser pessoa) {
        return getMbpmBean().isPessoaAtivaParaTerTarefa(pessoa);
    }

    public static Lnk getHrefPadrao(ProcessInstance instanciaProcesso) {
        return getMbpmBean().getHrefPadrao(instanciaProcesso);
    }

    public static Lnk getHrefPadrao(TaskInstance instanciaTarefa) {
        return getMbpmBean().getHrefPadrao(instanciaTarefa);
    }
}
