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

import net.vidageek.mirror.dsl.Mirror;
import org.apache.commons.lang3.StringUtils;
import org.opensingular.flow.core.defaults.NullViewLocator;
import org.opensingular.flow.core.entity.IEntityProcessInstance;
import org.opensingular.flow.core.renderer.IFlowRenderer;
import org.opensingular.flow.core.service.IPersistenceService;
import org.opensingular.flow.core.service.IProcessDataService;
import org.opensingular.flow.core.service.IProcessDefinitionEntityService;
import org.opensingular.flow.core.service.IUserService;
import org.opensingular.flow.core.view.IViewLocator;
import org.opensingular.flow.schedule.IScheduleService;
import org.opensingular.flow.schedule.ScheduleDataBuilder;
import org.opensingular.flow.schedule.ScheduledJob;
import org.opensingular.flow.schedule.quartz.QuartzScheduleService;
import org.opensingular.lib.commons.base.SingularException;
import org.opensingular.lib.commons.util.Loggable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class SingularFlowConfigurationBean implements Loggable {

    public static final String PREFIXO = "SGL";

    private String moduleCod;
    
    private List<ProcessNotifier> notifiers = new ArrayList<>();

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
        for (final ProcessDefinition<?> processDefinition : getDefinitions()) {
            for (STaskJava task : processDefinition.getFlowMap().getJavaTasks()) {
                if (!task.isImmediateExecution()) {
                    getScheduleService().schedule(new ScheduledJob(task.getCompleteName(), task.getScheduleData(), () -> executeTask(task)));
                }
            }
            for (ProcessScheduledJob scheduledJob : processDefinition.getScheduledJobs()) {
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

    protected ProcessDefinitionCache getDefinitionCache() {
        return ProcessDefinitionCache.get(getDefinitionsPackages());
    }

    protected abstract String[] getDefinitionsPackages();

    @Nonnull
    public <K extends ProcessDefinition<?>> K getProcessDefinition(@Nonnull Class<K> processClass) {
        return ProcessDefinitionCache.getDefinition(processClass);
    }

    /**
     * @throws SingularFlowException <code> if there is no ProcessDefinition associated with key</code>
     */
    @Nonnull
    protected ProcessDefinition<?> getProcessDefinition(@Nonnull String key) {
        return getDefinitionCache().getDefinition(key);
    }

    /**
     * <code> this method does not throw a exception if there is no ProcessDefinition associated with key</code>
     */
    @Nonnull
    protected Optional<ProcessDefinition<?>> getProcessDefinitionOpt(@Nonnull String key) {
        return getDefinitionCache().getDefinitionOpt(key);
    }

    public List<ProcessDefinition<?>> getDefinitions() {
        return getDefinitionCache().getDefinitions();
    }

    // ------- Método de recuperação de instâncias --------------------

    /** Retorna a ProcessInstance referente a código infomado ou dispara exception senão encontrar. */
    @Nonnull
    private ProcessInstance getProcessInstance(@Nonnull Integer entityCod) {
        return getProcessInstanceOpt(entityCod).orElseThrow(() -> new SingularFlowException(msgNotFound(entityCod)));
    }

    /** Retorna a ProcessInstance referente a entidade infomado ou dispara exception senão encontrar. */
    @Nonnull
    protected ProcessInstance getProcessInstance(@Nonnull IEntityProcessInstance entityProcessInstance) {
        Objects.requireNonNull(entityProcessInstance);
        return getProcessInstance(entityProcessInstance.getCod());
    }

    @Nonnull
    protected final <X extends ProcessInstance, T extends ProcessDefinition<X>> X getProcessInstance(
            @Nonnull Class<T> processClass, @Nonnull IEntityProcessInstance entityProcessInstance) {
        Objects.requireNonNull(entityProcessInstance);
        return (X) getProcessInstance(getProcessDefinition(processClass), entityProcessInstance.getCod());
    }

    public <X extends ProcessInstance, K extends ProcessDefinition<X>> X getProcessInstance(
            @Nonnull K processDefinition, @Nonnull Integer cod) {
        return getProcessInstanceOpt(processDefinition, cod).orElseThrow(
                () -> new SingularFlowException(msgNotFound(cod)));
    }

    @Nonnull
    protected final <X extends ProcessInstance, T extends ProcessDefinition<X>> X getProcessInstance(@Nonnull Class<T> processClass, @Nonnull Integer cod) {
        return getProcessInstanceOpt(processClass, cod).orElseThrow(() -> new SingularFlowException(msgNotFound(cod)));
    }

    @Nonnull
    protected final <X extends ProcessInstance, T extends ProcessDefinition<X>> X getProcessInstance(
            @Nonnull Class<T> processClass, @Nonnull String id) {
        return getProcessInstanceOpt(processClass, id).orElseThrow(() -> new SingularFlowException(msgNotFound(id)));
    }

    @Nonnull
    protected <X extends ProcessInstance> X getProcessInstance(@Nonnull String id) {
        return (X) getProcessInstanceOpt(id).orElseThrow(() -> new SingularFlowException(msgNotFound(id)));
    }

    @Nonnull
    protected final <X extends ProcessInstance, T extends ProcessDefinition<X>> Optional<X> getProcessInstanceOpt(@Nonnull Class<T> processClass, @Nonnull Integer cod) {
        Objects.requireNonNull(processClass);
        return getProcessInstanceOpt(getProcessDefinition(processClass), cod);
    }

    @Nonnull
    public <X extends ProcessInstance, K extends ProcessDefinition<X>> Optional<X> getProcessInstanceOpt(
            @Nonnull K processDefinition, @Nonnull Integer cod) {
        Objects.requireNonNull(processDefinition);
        Objects.requireNonNull(cod);
        return processDefinition.getDataService().retrieveInstanceOpt(cod);
    }


    @Nonnull
    protected final <X extends ProcessInstance, T extends ProcessDefinition<X>> Optional<X> getProcessInstanceOpt(@Nonnull Class<T> processClass, @Nonnull String id) {
        if (StringUtils.isNumeric(id)) {
            return getProcessInstanceOpt(processClass, Integer.valueOf(id));
        }
        Optional<ProcessInstance> instance = getProcessInstanceOpt(id);
        if (instance.isPresent()) {
            getProcessDefinition(processClass).checkIfCompatible(instance.get());
        }
        return (Optional<X>) instance;
    }

    @Nonnull
    protected <X extends ProcessInstance> Optional<X> getProcessInstanceOpt(@Nonnull String processInstanceID) {
        Objects.requireNonNull(processInstanceID);
        MappingId mapeamento = parseId(processInstanceID);
        Optional<ProcessInstance> instance = getProcessInstanceOpt(mapeamento.cod);
        if (instance.isPresent() && mapeamento.abbreviation != null) {
            getProcessDefinition(mapeamento.abbreviation).checkIfCompatible(instance.get());
        }
        return (Optional<X>) instance;
    }

    /** Retorna a ProcessInstance referente a código infomado. */
    @Nonnull
    private Optional<ProcessInstance> getProcessInstanceOpt(@Nonnull Integer entityCod) {
        return getPersistenceService().retrieveProcessInstanceByCod(entityCod)
                .map(entity -> {
                    ProcessDefinition<?> def = getProcessDefinition(entity.getProcessVersion().getAbbreviation());
                    return def.convertToProcessInstance(entity);
                });
    }

    private static String msgNotFound(Object id) {
        return "Nao foi encontrada a instancia de processo com id=" + id;
    }

    // ------- Manipulação de ID --------------------------------------

    // TODO rever generateID e parseId, deveria ser tipado, talvez nem devesse
    // estar nesse lugar
    protected String generateID(ProcessInstance instancia) {
        return new StringBuilder(50)
            .append(PREFIXO)
            .append('.')
            .append(instancia.getProcessDefinition().getKey())
            .append('.')
            .append(instancia.getId()).toString();
    }

    // TODO rever generateID e parseId, deveria ser tipado, talvez nem devesse
    // estar nesse lugar

    protected String generateID(TaskInstance instanciaTarefa) {
        ProcessInstance instanciaProcesso = instanciaTarefa.getProcessInstance();
        return new StringBuilder(generateID(instanciaProcesso))
            .append('.')
            .append(instanciaTarefa.getId())
            .toString();
    }

    // TODO rever generateID e parseId, deveria ser tipado, talvez nem devesse
    // estar nesse lugar
    protected MappingId parseId(String instanciaID) {
        if (instanciaID == null || instanciaID.length() < 1) {
            throw SingularException.rethrow("O ID da instância não pode ser nulo ou vazio");
        }
        String parts[] = instanciaID.split("\\.");
        String sigla = parts[parts.length - 2];
        String id = parts[parts.length - 1];
        return new MappingId(sigla, Integer.parseInt(id));
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
        return new NullViewLocator();
    }

    // ------- Manipulação de Usuário ---------------------------------
    public abstract IUserService getUserService();

    /**
     * Notifica os listeners registrados sobre um evento.
     * 
     * @param operation
     */
    public void notifyListeners(Consumer<ProcessNotifier> operation) {
        for (ProcessNotifier n : notifiers) {
            operation.accept(n);
        }
    }

    /**
     * Registra um listener para receber notificações do Engine
     * 
     * @param p
     */
    public void addListener(ProcessNotifier p) {
        notifiers.add(p);
    }

    // ------- Consultas ----------------------------------------------

    public final List<? extends ProcessDefinition<?>> getEnabledProcessForCreationBy(SUser user) {
        return getDefinitions().stream().filter(d -> d.canBeCreatedBy(user)).sorted().collect(Collectors.toList());
    }

    // ------- Outros -------------------------------------------------

    public IFlowRenderer getFlowRenderer() {
        try {
            Class<?> yfilesRendeder = Class.forName("com.opensingular.flow.extras.renderer");
            return (IFlowRenderer) new Mirror().on(yfilesRendeder).invoke().method("getInstance").withoutArgs();
        } catch (ClassNotFoundException e) {
            getLogger().info(e.getMessage(), e);
        }
        return null;
    }

    protected abstract IPersistenceService<?, ?, ?, ?, ?, ?, ?, ?, ?, ?> getPersistenceService();

    protected abstract IProcessDefinitionEntityService<?, ?, ?, ?, ?, ?, ?, ?> getProcessEntityService();

    protected IScheduleService getScheduleService() {
        return new QuartzScheduleService();
    }

    public final Object executeTask(STaskJava task) {
        final IProcessDataService<?> dataService = task.getFlowMap().getProcessDefinition().getDataService();
        final Collection<? extends ProcessInstance> instancias = dataService.retrieveAllInstancesIn(task);
        if (task.isCalledInBlock()) {
            return task.executarByBloco(instancias);
        } else {
            for (final ProcessInstance instanciaProcessoMBPM : instancias) {
                FlowEngine.executeScheduledTransition(task, instanciaProcessoMBPM);
            }
            return null;
        }
    }
}
