package br.net.mirante.singular.flow.core;

import java.util.Objects;

import br.net.mirante.singular.flow.core.entity.IEntityProcessInstance;
import br.net.mirante.singular.flow.core.entity.IEntityTaskInstance;
import br.net.mirante.singular.flow.schedule.ScheduleDataBuilder;
import br.net.mirante.singular.flow.schedule.ScheduledJob;
import br.net.mirante.singular.flow.util.view.Lnk;

import com.google.common.base.Preconditions;

public class MBPM {

    private static AbstractMbpmBean mbpmBean;

    private MBPM() {
    }

    private static void init() {
        for (final ProcessDefinition<?> processDefinition : getMbpmBean().getDefinitions()) {
            for (final MTaskJava task : processDefinition.getFlowMap().getJavaTasks()) {
                if (!task.isImmediateExecution()) {
                    getMbpmBean().getScheduleService().schedule(new ScheduledJob(task.getCompleteName(), task.getScheduleData(), () -> getMbpmBean().executeTask(task)));
                }
            }
            for (ProcessScheduledJob scheduledJob : processDefinition.getFlowMap().getScheduledJobs()) {
                getMbpmBean().getScheduleService().schedule(scheduledJob);
            }
        }
        getMbpmBean().getScheduleService().schedule(new ExecuteWaitingTasksJob(ScheduleDataBuilder.buildHourly(1)));
        getMbpmBean().init();
    }

    public static synchronized void setConf(AbstractMbpmBean conf) {
        Preconditions.checkArgument(mbpmBean == null, "O contexto já foi configurado.");
        mbpmBean = conf;
        init();
    }

    public static AbstractMbpmBean getMbpmBean() {
        Objects.requireNonNull(mbpmBean, "Configuração do fluxo não realizada");
        return mbpmBean;
    }

    public static <K extends ProcessDefinition<?>> K getDefinicao(Class<K> classe) {
        return getMbpmBean().getProcessDefinition(classe);
    }

    @SuppressWarnings("unchecked")
    public static <K extends ProcessDefinition<?>> K getProcessDefinitionWith(String abbreviation) {
        return (K) getMbpmBean().getProcessDefinition(abbreviation);
    }

    public static <T extends VariableWrapper> T newInitialVariables(Class<? extends ProcessDefinition<?>> processDefinitionClass,
            Class<T> variableWrapperClass) {
        ProcessDefinition<?> processDefinition = getDefinicao(processDefinitionClass);
        return processDefinition.newInitialVariables(variableWrapperClass);
    }

    public static TaskInstance getTaskInstance(IEntityTaskInstance entityTaskInstance) {
        return new TaskInstance(entityTaskInstance);
    }

    @SuppressWarnings("unchecked")
    public static <K extends ProcessInstance> K getProcessInstance(IEntityProcessInstance dadosInstanciaProcesso) {
        return (K) getMbpmBean().getProcessInstance(dadosInstanciaProcesso);
    }

    public static <X extends ProcessInstance> X findProcessInstance(String id) {
        return getMbpmBean().findProcessInstance(id);
    }

    public static String generateID(ProcessInstance instancia) {
        return getMbpmBean().generateID(instancia);
    }

    public static String generateID(TaskInstance instanciaTarefa) {
        return getMbpmBean().generateID(instanciaTarefa);
    }

    public static MUser getUserIfAvailable() {
        return getMbpmBean().getUserIfAvailable();
    }

    public static AbstractProcessNotifiers getNotifiers() {
        return getMbpmBean().getNotifiers();
    }

    static boolean canBeAllocated(MUser pessoa) {
        return getMbpmBean().canBeAllocated(pessoa);
    }

    public static Lnk getDefaultHrefFor(ProcessInstance instanciaProcesso) {
        return getMbpmBean().getDefaultHrefFor(instanciaProcesso);
    }

    public static Lnk getDefaultHrefFor(TaskInstance instanciaTarefa) {
        return getMbpmBean().getDefaultHrefFor(instanciaTarefa);
    }
}
