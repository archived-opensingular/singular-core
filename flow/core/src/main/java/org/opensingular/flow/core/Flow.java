/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.flow.core;

import org.apache.commons.lang3.StringUtils;
import org.opensingular.flow.core.entity.IEntityProcessInstance;
import org.opensingular.flow.core.entity.IEntityTaskInstance;
import org.opensingular.lib.commons.net.Lnk;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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

    @Nonnull
    public static SingularFlowConfigurationBean getConfigBean() {
        Objects.requireNonNull(configBean, "Configuração do fluxo não realizada");
        return configBean;
    }

    @Nonnull
    public static <K extends FlowDefinition<?>> K getProcessDefinition(@Nonnull Class<K> classe) {
        return getConfigBean().getProcessDefinition(classe);
    }

    /**
     * <code> this method does not throw a exception if there is no ProcessDefinition associated with key</code>
     */
    @Nonnull
    public static Optional<FlowDefinition<?>> getProcessDefinitionOpt(@Nonnull String key) {
        return getConfigBean().getProcessDefinitionOpt(key);
    }

    /**
     * @throws SingularFlowException <code> if there is no ProcessDefinition associated with key</code>
     */
    @Nonnull
    public static <K extends FlowDefinition<?>> K getProcessDefinition(@Nonnull String key) {
        return (K) getConfigBean().getProcessDefinition(key);
    }

    public static <K extends FlowDefinition<?>> List<K> getDefinitions() {
        return (List<K>) getConfigBean().getDefinitions();
    }

    public static TaskInstance getTaskInstance(IEntityTaskInstance entityTaskInstance) {
        if (entityTaskInstance == null){
            return null;
        }
        return new TaskInstance(entityTaskInstance);
    }

    /** Retorna a ProcessInstance referente a entidade infomado ou dispara exception senão encontrar. */
    @Nonnull
    public static FlowInstance getProcessInstance(@Nonnull IEntityProcessInstance dadosInstanciaProcesso) {
        Objects.requireNonNull(dadosInstanciaProcesso);
        return Objects.requireNonNull(getConfigBean().getProcessInstance(dadosInstanciaProcesso));
    }

    @Nonnull
    public static <X extends FlowInstance, K extends FlowDefinition<X>> X getProcessInstance(@Nonnull Class<K> expectedType, @Nonnull IEntityProcessInstance dadosInstanciaProcesso) {
        return getConfigBean().getProcessInstance(expectedType, dadosInstanciaProcesso);
    }

    @Nonnull
    public static <X extends FlowInstance, K extends FlowDefinition<X>> X getProcessInstance(@Nonnull K processDefinition, @Nonnull Integer cod) {
        return getConfigBean().getProcessInstance(processDefinition, cod);
    }

    @Nonnull
    public static final <X extends FlowInstance, T extends FlowDefinition<X>> X getProcessInstance(@Nonnull Class<T> processClass, @Nonnull Integer cod) {
        return getConfigBean().getProcessInstance(processClass, cod);
    }

    @Nonnull
    public static final <X extends FlowInstance, T extends FlowDefinition<X>> X getProcessInstance(@Nonnull Class<T> processClass, @Nonnull String id) {
        return getConfigBean().getProcessInstance(processClass, id);
    }

    public static final <X extends FlowInstance, T extends FlowDefinition<X>> Optional<X> getProcessInstanceOpt(Class<T> processClass, String id) {
        return getConfigBean().getProcessInstanceOpt(processClass, id);
    }

    @Nonnull
    public static <X extends FlowInstance> Optional<X> getProcessInstanceOpt(@Nonnull String processInstanceID) {
        return getConfigBean().getProcessInstanceOpt(processInstanceID);
    }

    @Nonnull
    public static final <T extends FlowInstance> T getProcessInstance(@Nonnull String processInstanceID) {
        return getConfigBean().getProcessInstance(processInstanceID);
    }

    /**
     * Converte a lista de entidades nos respectivos ProcessInstance.
     * @return Uma lista que pode ser alterada
     */
    @Nonnull
    public static List<FlowInstance> getProcessInstances(@Nonnull Collection<? extends IEntityProcessInstance> entities) {
        return entities.stream().map(e -> getProcessInstance(e)).collect(Collectors.toList());
    }

    public static String generateID(FlowInstance instancia) {
        return getConfigBean().generateID(instancia);
    }

    public static String generateID(TaskInstance instanciaTarefa) {
        return getConfigBean().generateID(instanciaTarefa);
    }

    public static SUser getUserIfAvailable() {
        return getConfigBean().getUserService().getUserIfAvailable();
    }

    public static void notifyListeners(Consumer<ProcessNotifier> operation) {
        getConfigBean().notifyListeners(operation);
    }

    static boolean canBeAllocated(SUser pessoa) {
        return getConfigBean().getUserService().canBeAllocated(pessoa);
    }

    public static Lnk getDefaultHrefFor(FlowInstance instanciaProcesso) {
        return getConfigBean().getViewLocator().getDefaultHrefFor(instanciaProcesso);
    }

    public static Lnk getDefaultHrefFor(TaskInstance instanciaTarefa) {
        return getConfigBean().getViewLocator().getDefaultHrefFor(instanciaTarefa);
    }

    public static String getKeyFromDefinition(Class<? extends FlowDefinition> clazz) {
        if (clazz == null) {
            throw new SingularFlowException(" A classe de definição do fluxo não pode ser nula ");
        }
        if (!clazz.isAnnotationPresent(DefinitionInfo.class)) {
            throw new SingularFlowException("A definição de fluxo deve ser anotada com " + DefinitionInfo.class.getName());
        }
        String key = clazz.getAnnotation(DefinitionInfo.class).value();
        if (StringUtils.isBlank(key)) {
            throw new SingularFlowException("A chave definida na anitação" + DefinitionInfo.class.getName() + " não pode ser nula");
        }
        return key;
    }
}
