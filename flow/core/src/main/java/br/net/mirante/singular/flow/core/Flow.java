package br.net.mirante.singular.flow.core;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import br.net.mirante.singular.flow.core.entity.IEntityProcessInstance;
import br.net.mirante.singular.flow.core.entity.IEntityTaskInstance;
import br.net.mirante.singular.flow.core.view.Lnk;

@SuppressWarnings("unchecked")
public final class Flow {

    private static SingularFlowConfigurationBean configBean;

    private Flow() {
    }

    public static synchronized void setConf(SingularFlowConfigurationBean conf, boolean force) {
        if (!force) {
            if (configBean != null && configBean != conf) {
                throw new SingularFlowException("O contexto já foi configurado.");
            }
        }
        configBean = conf;
        configBean.start();
    }

    public static synchronized void setConf(SingularFlowConfigurationBean conf) {
        setConf(conf, false);
    }

    public static SingularFlowConfigurationBean getConfigBean() {
        Objects.requireNonNull(configBean, "Configuração do fluxo não realizada");
        return configBean;
    }

    public static <K extends ProcessDefinition<?>> K getProcessDefinition(Class<K> classe) {
        return getConfigBean().getProcessDefinition(classe);
    }

    /**
     * <code> this method does not throw a exception if there is no ProcessDefinition associated with key</code>
     * @param key
     * @return
     */
    public static ProcessDefinition<?> getProcessDefinitionUnchecked(String key) {
        return getConfigBean().getProcessDefinitionUnchecked(key);
    }

    /**
     * @throws SingularFlowException <code> if there is no ProcessDefinition associated with key</code>
     */
    public static <K extends ProcessDefinition<?>> K getProcessDefinitionWith(String key) {
        return (K) getConfigBean().getProcessDefinition(key);
    }

    public static <K extends ProcessDefinition<?>> List<K> getDefinitions() {
        return (List<K>) getConfigBean().getDefinitions();
    }

    public static TaskInstance getTaskInstance(IEntityTaskInstance entityTaskInstance) {
        return new TaskInstance(entityTaskInstance);
    }

    public static ProcessInstance getProcessInstance(IEntityProcessInstance dadosInstanciaProcesso) {
        return getConfigBean().getProcessInstance(dadosInstanciaProcesso);
    }

    public static <X extends ProcessInstance, K extends ProcessDefinition<X>> X getProcessInstance(Class<K> expectedType, IEntityProcessInstance dadosInstanciaProcesso) {
        return getConfigBean().getProcessInstance(expectedType, dadosInstanciaProcesso);
    }

    public static final <X extends ProcessInstance, T extends ProcessDefinition<X>> X getProcessInstance(Class<T> processClass, Integer cod) {
        return getConfigBean().getProcessInstance(processClass, cod);
    }

    public static final <X extends ProcessInstance, T extends ProcessDefinition<X>> X getProcessInstance(Class<T> processClass, String id) {
        return getConfigBean().getProcessInstance(processClass, id);
    }

    public static final <X extends ProcessInstance, T extends ProcessDefinition<X>> X getProcessInstanceOrException(Class<T> processClass, String id) {
        return getConfigBean().getProcessInstanceOrException(processClass, id);
    }

    public static <X extends ProcessInstance> X getProcessInstance(String processInstanceID) {
        return (X) getConfigBean().getProcessInstance(processInstanceID);
    }

    public static final <T extends ProcessInstance> T getProcessInstanceOrException(String processInstanceID) {
        return (T) getConfigBean().getProcessInstanceOrException(processInstanceID);
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
        return getConfigBean().generateID(instancia);
    }

    public static String generateID(TaskInstance instanciaTarefa) {
        return getConfigBean().generateID(instanciaTarefa);
    }

    public static MUser getUserIfAvailable() {
        return getConfigBean().getUserService().getUserIfAvailable();
    }

    public static void notifyListeners(Consumer<ProcessNotifier> operation) {
        getConfigBean().notifyListeners(operation);
    }

    static boolean canBeAllocated(MUser pessoa) {
        return getConfigBean().getUserService().canBeAllocated(pessoa);
    }

    public static Lnk getDefaultHrefFor(ProcessInstance instanciaProcesso) {
        return getConfigBean().getViewLocator().getDefaultHrefFor(instanciaProcesso);
    }

    public static Lnk getDefaultHrefFor(TaskInstance instanciaTarefa) {
        return getConfigBean().getViewLocator().getDefaultHrefFor(instanciaTarefa);
    }
}
