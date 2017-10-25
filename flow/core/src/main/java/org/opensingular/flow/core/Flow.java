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
import org.opensingular.flow.core.entity.IEntityFlowInstance;
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
    public static <K extends FlowDefinition<?>> K getFlowDefinition(@Nonnull Class<K> flowDefinitionClass) {
        return getConfigBean().getFlowDefinition(flowDefinitionClass);
    }

    /**
     * <code> this method does not throw a exception if there is no {@link FlowDefinition} associated with key</code>
     */
    @Nonnull
    public static Optional<FlowDefinition<?>> getFlowDefinitionOpt(@Nonnull String key) {
        return getConfigBean().getFlowDefinitionOpt(key);
    }

    /**
     * @throws SingularFlowException <code> if there is no {@link FlowDefinition} associated with key</code>
     */
    @Nonnull
    public static <K extends FlowDefinition<?>> K getFlowDefinition(@Nonnull String key) {
        return (K) getConfigBean().getFlowDefinition(key);
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

    /** Retorna a {@link FlowInstance} referente a entidade infomado ou dispara exception senão encontrar. */
    @Nonnull
    public static FlowInstance getFlowInstance(@Nonnull IEntityFlowInstance entityFlowInstance) {
        Objects.requireNonNull(entityFlowInstance);
        return Objects.requireNonNull(getConfigBean().getFlowInstance(entityFlowInstance));
    }

    @Nonnull
    public static <X extends FlowInstance, K extends FlowDefinition<X>> X getFlowInstance(@Nonnull Class<K> expectedType, @Nonnull IEntityFlowInstance entityFlowInstance) {
        return getConfigBean().getFlowInstance(expectedType, entityFlowInstance);
    }

    @Nonnull
    public static <X extends FlowInstance, K extends FlowDefinition<X>> X getFlowInstance(@Nonnull K flowDefinition, @Nonnull Integer cod) {
        return getConfigBean().getFlowInstance(flowDefinition, cod);
    }

    @Nonnull
    public static final <X extends FlowInstance, T extends FlowDefinition<X>> X getFlowInstance(@Nonnull Class<T> flowClass, @Nonnull Integer cod) {
        return getConfigBean().getFlowInstance(flowClass, cod);
    }

    @Nonnull
    public static final <X extends FlowInstance, T extends FlowDefinition<X>> X getFlowInstance(@Nonnull Class<T> flowClass, @Nonnull String id) {
        return getConfigBean().getFlowInstance(flowClass, id);
    }

    public static final <X extends FlowInstance, T extends FlowDefinition<X>> Optional<X> getFlowInstanceOpt(Class<T> flowClass, String id) {
        return getConfigBean().getFlowInstanceOpt(flowClass, id);
    }

    @Nonnull
    public static <X extends FlowInstance> Optional<X> getFlowInstanceOpt(@Nonnull String flowInstanceID) {
        return getConfigBean().getFlowInstanceOpt(flowInstanceID);
    }

    @Nonnull
    public static final <T extends FlowInstance> T getFlowInstance(@Nonnull String flowInstanceID) {
        return getConfigBean().getFlowInstance(flowInstanceID);
    }

    /**
     * Converte a lista de entidades nos respectivos {@link FlowInstance}.
     * @return Uma lista que pode ser alterada
     */
    @Nonnull
    public static List<FlowInstance> getFlowInstances(@Nonnull Collection<? extends IEntityFlowInstance> entities) {
        return entities.stream().map(e -> getFlowInstance(e)).collect(Collectors.toList());
    }

    public static String generateID(FlowInstance instance) {
        return getConfigBean().generateID(instance);
    }

    public static String generateID(TaskInstance taskInstance) {
        return getConfigBean().generateID(taskInstance);
    }

    public static SUser getUserIfAvailable() {
        return getConfigBean().getUserService().getUserIfAvailable();
    }

    public static void notifyListeners(Consumer<FlowInstanceListener> operation) {
        getConfigBean().notifyListeners(operation);
    }

    static boolean canBeAllocated(SUser user) {
        return getConfigBean().getUserService().canBeAllocated(user);
    }

    public static Lnk getDefaultHrefFor(FlowInstance flowInstance) {
        return getConfigBean().getViewLocator().getDefaultHrefFor(flowInstance);
    }

    public static Lnk getDefaultHrefFor(TaskInstance taskInstance) {
        return getConfigBean().getViewLocator().getDefaultHrefFor(taskInstance);
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
