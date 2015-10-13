package br.net.mirante.singular.flow.core;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.net.mirante.singular.flow.core.entity.IEntityProcessInstance;
import br.net.mirante.singular.flow.core.entity.IEntityTaskInstance;
import br.net.mirante.singular.flow.schedule.ScheduleDataBuilder;
import br.net.mirante.singular.flow.schedule.ScheduledJob;
import br.net.mirante.singular.flow.util.view.Lnk;

public class Flow {

    private static final Logger LOGGER = LoggerFactory.getLogger(Flow.class);

    private static SingularFlowConfigurationBean mbpmBean;

    private Flow() {
    }

    private static void init() {
        for (final ProcessDefinition<?> processDefinition : getMbpmBean().getDefinitions()) {
            for (final MTaskJava task : processDefinition.getFlowMap().getJavaTasks()) {
                if (!task.isImmediateExecution()) {
                    getMbpmBean().getScheduleService().schedule(new ScheduledJob(task.getCompleteName(), task.getScheduleData(), () -> getMbpmBean().executeTask(task)));
                }
            }
            for (ProcessScheduledJob scheduledJob : processDefinition.getScheduledJobs()) {
                getMbpmBean().getScheduleService().schedule(scheduledJob);
            }
        }
        getMbpmBean().getScheduleService().schedule(new ExecuteWaitingTasksJob(ScheduleDataBuilder.buildHourly(1)));
        getMbpmBean().init();
    }

    public static synchronized void setConf(SingularFlowConfigurationBean conf) {
        if(mbpmBean != null
                && mbpmBean != conf){
            throw new SingularFlowException("O contexto já foi configurado.");
        }
        mbpmBean = conf;
        init();
    }

    public static SingularFlowConfigurationBean getMbpmBean() {
        Objects.requireNonNull(mbpmBean, "Configuração do fluxo não realizada");
        return mbpmBean;
    }

    public static <K extends ProcessDefinition<?>> K getProcessDefinition(Class<K> classe) {
        return getMbpmBean().getProcessDefinition(classe);
    }
    public static ProcessDefinition<?> getProcessDefinition(String abbreviation){
        return getMbpmBean().getProcessDefinition(abbreviation);
    }

    @SuppressWarnings("unchecked")
    public static <K extends ProcessDefinition<?>> K getProcessDefinitionWith(String abbreviation) {
        return (K) getMbpmBean().getProcessDefinition(abbreviation);
    }

    @SuppressWarnings("unchecked")
    public static <K extends ProcessDefinition<?>> List<K> getDefinitions() {
        return (List<K>) getMbpmBean().getDefinitions();
    }

    public static TaskInstance getTaskInstance(IEntityTaskInstance entityTaskInstance) {
        return new TaskInstance(entityTaskInstance);
    }

    public static ProcessInstance getProcessInstance(IEntityProcessInstance dadosInstanciaProcesso) {
        return getMbpmBean().getProcessInstance(dadosInstanciaProcesso);
    }

    public static <K extends ProcessInstance> K getProcessInstance(Class<K> expectedType, IEntityProcessInstance dadosInstanciaProcesso) {
        return getMbpmBean().getProcessInstance(expectedType, dadosInstanciaProcesso);
    }

    public static final <T extends ProcessInstance> T getProcessInstance(Class<T> expectedType, Integer cod) {
        return getMbpmBean().getProcessInstance(expectedType, cod);
    }

    public static final <T extends ProcessInstance> T getProcessInstance(Class<T> expectedType, String id) {
        return getMbpmBean().getProcessInstance(expectedType, id);
    }

    public static final <T extends ProcessInstance> T getProcessInstanceOrException(Class<T> expectedType, String id) {
        return getMbpmBean().getProcessInstanceOrException(expectedType, id);
    }

    public static <X extends ProcessInstance> X getProcessInstance(String processInstanceID) {
        return (X) getMbpmBean().getProcessInstance(processInstanceID);
    }

    public static final <T extends ProcessInstance> T getProcessInstanceOrException(String processInstanceID) {
        return (T) getMbpmBean().getProcessInstanceOrException(processInstanceID);
    }

    /**
     * Converte a lista de entidades nos respectivos ProcessInstance.
     *
     * @return Uma lista que pode ser alterada
     */
    public static List<ProcessInstance> getProcessInstances(Collection<? extends IEntityProcessInstance> entities) {
        return entities.stream().map(e -> getProcessInstance(e)).collect(Collectors.toList());
    }

    public static String generateID(ProcessInstance instancia) {
        return getMbpmBean().generateID(instancia);
    }

    public static String generateID(TaskInstance instanciaTarefa) {
        return getMbpmBean().generateID(instanciaTarefa);
    }

    public static MUser getUserIfAvailable() {
        return getMbpmBean().getUserService().getUserIfAvailable();
    }

    public static AbstractProcessNotifiers getNotifiers() {
        return getMbpmBean().getNotifiers();
    }

    static boolean canBeAllocated(MUser pessoa) {
        return getMbpmBean().getUserService().canBeAllocated(pessoa);
    }

    public static Lnk getDefaultHrefFor(ProcessInstance instanciaProcesso) {
        return getMbpmBean().getViewLocator().getDefaultHrefFor(instanciaProcesso);
    }

    public static Lnk getDefaultHrefFor(TaskInstance instanciaTarefa) {
        return getMbpmBean().getViewLocator().getDefaultHrefFor(instanciaTarefa);
    }
}
