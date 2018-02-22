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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;
import org.opensingular.flow.core.entity.IEntityFlowInstance;
import org.opensingular.flow.core.service.IFlowDataService;
import org.opensingular.flow.core.service.IFlowDefinitionEntityService;
import org.opensingular.flow.core.service.IPersistenceService;
import org.opensingular.flow.core.service.IUserService;
import org.opensingular.flow.core.view.IViewLocator;
import org.opensingular.flow.schedule.IScheduleService;
import org.opensingular.flow.schedule.ScheduleDataBuilder;
import org.opensingular.flow.schedule.ScheduledJob;
import org.opensingular.flow.schedule.quartz.QuartzScheduleService;
import org.opensingular.lib.commons.base.SingularException;
import org.opensingular.lib.commons.util.Loggable;

public abstract class SingularFlowConfigurationBean implements Loggable {

    public static final String PREFIXO = "SGL";

    private String moduleCod;
    
    private List<FlowInstanceListener> notifiers = new ArrayList<>();

    /**
     * @param moduleCod - chave do sistema cadastrado no em <code>TB_MODULO</code>
     */
    protected SingularFlowConfigurationBean(String moduleCod) {
        super();
        this.moduleCod = moduleCod;
    }

    protected SingularFlowConfigurationBean() {
        super();
    }

    final void start() {
        for (final FlowDefinition<?> flowDefinition : getDefinitions()) {
            for (STaskJava task : flowDefinition.getFlowMap().getJavaTasks()) {
                if (!task.isImmediateExecution()) {
                    getScheduleService().schedule(new ScheduledJob(task.getCompleteName(), task.getScheduleData(), () -> executeTask(task)));
                }
            }
            for (FlowScheduledJob scheduledJob : flowDefinition.getScheduledJobs()) {
                getScheduleService().schedule(scheduledJob);
            }
        }
        configureWaitingTasksJobSchedule();
        init();
    }

    protected void configureWaitingTasksJobSchedule() {
        getScheduleService().schedule(new ExecuteWaitingTasksJob(ScheduleDataBuilder.buildHourly(1)));
    }


    protected void init() {

    }

    public final void setModuleCod(String moduleCod) {
        this.moduleCod = moduleCod;
    }

    @Nonnull
    public final String getModuleCod() {
        if (moduleCod == null) {
            throw new SingularFlowException("Não foi definido o moduleCod");
        }
        return moduleCod;
    }
    
    // ------- Método de recuperação de definições --------------------

    protected FlowDefinitionCache getDefinitionCache() {
        return FlowDefinitionCache.get(getDefinitionsPackages());
    }

    protected abstract String[] getDefinitionsPackages();

    @Nonnull
    public <K extends FlowDefinition<?>> K getFlowDefinition(@Nonnull Class<K> flowClass) {
        return FlowDefinitionCache.getDefinition(flowClass);
    }

    /**
     * @throws SingularFlowException <code> if there is no {@link FlowDefinition} associated with key</code>
     */
    @Nonnull
    protected FlowDefinition<?> getFlowDefinition(@Nonnull String key) {
        return getDefinitionCache().getDefinition(key);
    }

    /**
     * <code> this method does not throw a exception if there is no {@link FlowDefinition} associated with key</code>
     */
    @Nonnull
    protected Optional<FlowDefinition<?>> getFlowDefinitionOpt(@Nonnull String key) {
        return getDefinitionCache().getDefinitionOpt(key);
    }

    public List<FlowDefinition<?>> getDefinitions() {
        return getDefinitionCache().getDefinitions();
    }

    // ------- Método de recuperação de instâncias --------------------

    /** Retorna a {@link FlowInstance} referente a código infomado ou dispara exception senão encontrar. */
    @Nonnull
    private FlowInstance getFlowInstance(@Nonnull Integer entityCod) {
        return getFlowInstanceOpt(entityCod).orElseThrow(() -> new SingularFlowException(msgNotFound(entityCod)));
    }

    /** Retorna a {@link FlowInstance} referente a entidade infomado ou dispara exception senão encontrar. */
    @Nonnull
    protected FlowInstance getFlowInstance(@Nonnull IEntityFlowInstance entityFlowInstance) {
        Objects.requireNonNull(entityFlowInstance);
        return getFlowInstance(entityFlowInstance.getCod());
    }

    @Nonnull
    protected final <X extends FlowInstance, T extends FlowDefinition<X>> X getFlowInstance(
            @Nonnull Class<T> flowClass, @Nonnull IEntityFlowInstance entityFlowInstance) {
        Objects.requireNonNull(entityFlowInstance);
        return (X) getFlowInstance(getFlowDefinition(flowClass), entityFlowInstance.getCod());
    }

    public <X extends FlowInstance, K extends FlowDefinition<X>> X getFlowInstance(
            @Nonnull K flowDefinition, @Nonnull Integer cod) {
        return getFlowInstanceOpt(flowDefinition, cod).orElseThrow(
                () -> new SingularFlowException(msgNotFound(cod)));
    }

    @Nonnull
    protected final <X extends FlowInstance, T extends FlowDefinition<X>> X getFlowInstance(@Nonnull Class<T> flowClass, @Nonnull Integer cod) {
        return getFlowInstanceOpt(flowClass, cod).orElseThrow(() -> new SingularFlowException(msgNotFound(cod)));
    }

    @Nonnull
    protected final <X extends FlowInstance, T extends FlowDefinition<X>> X getFlowInstance(
            @Nonnull Class<T> flowClass, @Nonnull String id) {
        return getFlowInstanceOpt(flowClass, id).orElseThrow(() -> new SingularFlowException(msgNotFound(id)));
    }

    @Nonnull
    protected <X extends FlowInstance> X getFlowInstance(@Nonnull String id) {
        return (X) getFlowInstanceOpt(id).orElseThrow(() -> new SingularFlowException(msgNotFound(id)));
    }

    @Nonnull
    protected final <X extends FlowInstance, T extends FlowDefinition<X>> Optional<X> getFlowInstanceOpt(@Nonnull Class<T> flowClass, @Nonnull Integer cod) {
        Objects.requireNonNull(flowClass);
        return getFlowInstanceOpt(getFlowDefinition(flowClass), cod);
    }

    @Nonnull
    public <X extends FlowInstance, K extends FlowDefinition<X>> Optional<X> getFlowInstanceOpt(
            @Nonnull K flowDefinition, @Nonnull Integer cod) {
        Objects.requireNonNull(flowDefinition);
        Objects.requireNonNull(cod);
        return flowDefinition.getDataService().retrieveInstanceOpt(cod);
    }


    @Nonnull
    protected final <X extends FlowInstance, T extends FlowDefinition<X>> Optional<X> getFlowInstanceOpt(@Nonnull Class<T> flowClass, @Nonnull String id) {
        if (StringUtils.isNumeric(id)) {
            return getFlowInstanceOpt(flowClass, Integer.valueOf(id));
        }
        Optional<FlowInstance> instance = getFlowInstanceOpt(id);
        if (instance.isPresent()) {
            getFlowDefinition(flowClass).checkIfCompatible(instance.get());
        }
        return (Optional<X>) instance;
    }

    @Nonnull
    protected <X extends FlowInstance> Optional<X> getFlowInstanceOpt(@Nonnull String flowInstanceID) {
        Objects.requireNonNull(flowInstanceID);
        MappingId mappingId = parseId(flowInstanceID);
        Optional<FlowInstance> instance = getFlowInstanceOpt(mappingId.cod);
        if (instance.isPresent() && mappingId.abbreviation != null) {
            getFlowDefinition(mappingId.abbreviation).checkIfCompatible(instance.get());
        }
        return (Optional<X>) instance;
    }

    /** Retorna a {@link FlowInstance} referente a código infomado. */
    @Nonnull
    private Optional<FlowInstance> getFlowInstanceOpt(@Nonnull Integer entityCod) {
        return getPersistenceService().retrieveFlowInstanceByCod(entityCod)
                .map(entity -> {
                    FlowDefinition<?> def = getFlowDefinition(entity.getFlowVersion().getAbbreviation());
                    return def.convertToFlowInstance(entity);
                });
    }

    private static String msgNotFound(Object id) {
        return "Nao foi encontrada a instancia de fluxo com id=" + id;
    }

    // ------- Manipulação de ID --------------------------------------

    // TODO rever generateID e parseId, deveria ser tipado, talvez nem devesse
    // estar nesse lugar
    protected String generateID(FlowInstance instance) {
        return new StringBuilder(50)
            .append(PREFIXO)
            .append('.')
            .append(instance.getFlowDefinition().getKey())
            .append('.')
            .append(instance.getId()).toString();
    }

    // TODO rever generateID e parseId, deveria ser tipado, talvez nem devesse
    // estar nesse lugar

    protected String generateID(TaskInstance taskInstance) {
        FlowInstance flowInstance = taskInstance.getFlowInstance();
        return new StringBuilder(generateID(flowInstance))
            .append('.')
            .append(taskInstance.getId())
            .toString();
    }

    // TODO rever generateID e parseId, deveria ser tipado, talvez nem devesse
    // estar nesse lugar
    protected MappingId parseId(String instanceID) {
        if (instanceID == null || instanceID.length() < 1) {
            throw SingularException.rethrow("O ID da instância não pode ser nulo ou vazio");
        }
        String parts[] = instanceID.split("\\.");
        String abbreviation = parts[parts.length - 2];
        String id = parts[parts.length - 1];
        return new MappingId(abbreviation, Integer.parseInt(id));
    }

    // TODO rever generateID e parseId, deveria ser tipado, talvez nem devesse
    // estar nesse lugar
    protected static class MappingId {
        public final String abbreviation;
        public final Integer cod;

        public MappingId(String abbreviation, int cod) {
            this.abbreviation = abbreviation;
            this.cod = cod;
        }
    }

    // ------- Geração de link ----------------------------------------
    protected IViewLocator getViewLocator() {
        return SFlowUtil.dummyIViewLocator();
    }

    // ------- Manipulação de Usuário ---------------------------------
    public abstract IUserService getUserService();

    /**
     * Notifica os listeners registrados sobre um evento.
     * 
     * @param operation
     */
    public void notifyListeners(Consumer<FlowInstanceListener> operation) {
        for (FlowInstanceListener n : notifiers) {
            operation.accept(n);
        }
    }

    /**
     * Registra um listener para receber notificações do Engine
     * 
     * @param p
     */
    public void addListener(FlowInstanceListener p) {
        notifiers.add(p);
    }

    // ------- Consultas ----------------------------------------------

    public final List<? extends FlowDefinition<?>> getUserAllowedFlowsForStart(SUser user) {
        return getDefinitions().stream().filter(d -> d.canBeCreatedBy(user)).sorted().collect(Collectors.toList());
    }

    // ------- Outros -------------------------------------------------
    
    protected abstract IPersistenceService<?, ?, ?, ?, ?, ?, ?, ?, ?, ?> getPersistenceService();

    protected abstract IFlowDefinitionEntityService<?, ?, ?, ?, ?, ?, ?, ?> getFlowEntityService();

    protected IScheduleService getScheduleService() {
        return new QuartzScheduleService();
    }

    public final Object executeTask(STaskJava task) {
        try {
            final IFlowDataService<?>                dataService = task.getFlowMap().getFlowDefinition().getDataService();
            final Collection<? extends FlowInstance> instances   = dataService.retrieveAllInstancesIn(task);
            getLogger().info("Start running job: "+ task.getName()+" "+Optional.ofNullable(instances).map(Collection::size).orElse(0)+" instances. ");
            if (task.isCalledInBlock()) {
                return task.executarByBloco(instances);
            } else {
                for (final FlowInstance instance : instances) {
                    FlowEngine.executeScheduledTransition(task, instance);
                }
                return null;
            }
        } finally {
            getLogger().info("Job executed : "+ task.getName());
        }
    }
}
