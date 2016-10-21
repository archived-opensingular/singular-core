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
import org.opensingular.flow.core.entity.IEntityProcessInstance;
import org.opensingular.flow.core.renderer.IFlowRenderer;
import org.opensingular.flow.core.service.IProcessDataService;
import org.opensingular.flow.core.service.IUserService;
import org.opensingular.flow.core.view.IViewLocator;
import org.opensingular.lib.commons.base.SingularException;
import org.opensingular.flow.core.defaults.NullViewLocator;
import org.opensingular.flow.core.service.IPersistenceService;
import org.opensingular.flow.core.service.IProcessDefinitionEntityService;
import org.opensingular.flow.schedule.IScheduleService;
import org.opensingular.flow.schedule.ScheduleDataBuilder;
import org.opensingular.flow.schedule.ScheduledJob;
import org.opensingular.flow.schedule.quartz.QuartzScheduleService;
import org.apache.commons.lang3.StringUtils;
import org.opensingular.lib.commons.util.Loggable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class SingularFlowConfigurationBean implements Loggable {

    public static final String PREFIXO = "SGL";

    private String processGroupCod;
    
    private List<ProcessNotifier> notifiers = new ArrayList<>();

    /**
     * @param processGroupCod - chave do sistema cadastrado no em <code>TB_GRUPO_PROCESSO</code>
     */
    protected SingularFlowConfigurationBean(String processGroupCod) {
        super();
        this.processGroupCod = processGroupCod;
    }

    protected SingularFlowConfigurationBean() {
        super();
    }

    final void start() {
        for (final ProcessDefinition<?> processDefinition : getDefinitions()) {
            for (final MTaskJava task : processDefinition.getFlowMap().getJavaTasks()) {
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

    public final void setProcessGroupCod(String processGroupCod) {
        this.processGroupCod = processGroupCod;
    }
    
    public final String getProcessGroupCod() {
//        Objects.requireNonNull(processGroupCod);
        if (processGroupCod == null) {
            throw new SingularFlowException("Não foi definido o ProcessGroupCod");
        }
        return processGroupCod;
    }
    
    // ------- Método de recuperação de definições --------------------

    protected ProcessDefinitionCache getDefinitionCache() {
        return ProcessDefinitionCache.get(getDefinitionsPackages());
    }

    protected abstract String[] getDefinitionsPackages();

    public <K extends ProcessDefinition<?>> K getProcessDefinition(Class<K> processClass) {
        return ProcessDefinitionCache.getDefinition(processClass);
    }

    /**
     * @throws SingularFlowException <code> if there is no ProcessDefinition associated with key</code>
     */
    protected ProcessDefinition<?> getProcessDefinition(String key) {
        return getDefinitionCache().getDefinition(key);
    }

    /**
     * <code> this method does not throw a exception if there is no ProcessDefinition associated with key</code>
     * @param key
     * @return
     */
    protected ProcessDefinition<?> getProcessDefinitionUnchecked(String key) {
        return getDefinitionCache().getDefinitionUnchecked(key);
    }

    public List<ProcessDefinition<?>> getDefinitions() {
        return getDefinitionCache().getDefinitions();
    }

    // ------- Método de recuperação de instâncias --------------------

    private ProcessInstance getProcessInstanceByEntityCod(Integer cod) {
        IEntityProcessInstance dadosInstanciaProcesso = getPersistenceService().retrieveProcessInstanceByCod(cod);
        ProcessDefinition<?> def = getProcessDefinition(dadosInstanciaProcesso.getProcessVersion().getAbbreviation());
        return def.convertToProcessInstance(dadosInstanciaProcesso);
    }

    protected ProcessInstance getProcessInstance(IEntityProcessInstance entityProcessInstance) {
        return getProcessInstanceByEntityCod(entityProcessInstance.getCod());
    }

    protected final <X extends ProcessInstance, T extends ProcessDefinition<X>> X getProcessInstance(Class<T> instanceClass, IEntityProcessInstance entityProcessInstance) {
        return (X) getProcessInstanceByEntityCod(entityProcessInstance.getCod());
    }

    protected final <X extends ProcessInstance, T extends ProcessDefinition<X>> X getProcessInstance(Class<T> processClass, Integer cod) {
        return getProcessDefinition(processClass).getDataService().retrieveInstance(cod);
    }

    protected final <X extends ProcessInstance, T extends ProcessDefinition<X>> X getProcessInstanceOrException(Class<T> processClass, String id) {
        X instance = getProcessInstance(processClass, id);
        if (instance == null) {
            throw new SingularFlowException("Não foi encontrada a instancia '" + id + "' do tipo " + processClass.getName());
        }
        return instance;
    }

    protected final <X extends ProcessInstance, T extends ProcessDefinition<X>> X getProcessInstance(Class<T> processClass, String id) {
        if (StringUtils.isNumeric(id)) {
            return getProcessInstance(processClass, Integer.parseInt(id));
        } else {
            return (X) getProcessInstance(id);
        }
    }

    protected ProcessInstance getProcessInstance(String processInstanceID) {
        if (processInstanceID == null) {
            return null;
        }
        MappingId mapeamento = parseId(processInstanceID);
        if (mapeamento.abbreviation == null) {
            return getProcessInstanceByEntityCod(mapeamento.cod);
        } else {
            final ProcessDefinition<?> def = getProcessDefinition(mapeamento.abbreviation);
            if (def == null) {
                throw new SingularFlowException("Não existe definição de processo '" + mapeamento.abbreviation + "'");
            }
            return def.getDataService().retrieveInstance(mapeamento.cod);
        }
    }

    protected ProcessInstance getProcessInstanceOrException(String processInstanceID) {
        ProcessInstance instance = getProcessInstance(processInstanceID);
        if (instance == null) {
            throw new SingularFlowException("Não foi encontrada a instancia '" + processInstanceID + "'");
        }
        return instance;
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
            throw new SingularException("O ID da instância não pode ser nulo ou vazio");
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

    public final List<? extends ProcessDefinition<?>> getEnabledProcessForCreationBy(MUser user) {
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

    public final Object executeTask(MTaskJava task) {
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
