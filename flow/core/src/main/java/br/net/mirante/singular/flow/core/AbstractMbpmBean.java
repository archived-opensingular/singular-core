package br.net.mirante.singular.flow.core;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import br.net.mirante.singular.flow.core.entity.IEntityProcessInstance;
import br.net.mirante.singular.flow.core.renderer.IFlowRenderer;
import br.net.mirante.singular.flow.core.renderer.YFilesFlowRenderer;
import br.net.mirante.singular.flow.core.service.IPersistenceService;
import br.net.mirante.singular.flow.core.service.IProcessDataService;
import br.net.mirante.singular.flow.core.service.IProcessEntityService;
import br.net.mirante.singular.flow.schedule.IScheduleService;
import br.net.mirante.singular.flow.util.view.Lnk;

//TODO implementacao default, essa classe deveria vir implementada por default, muita coisa para definir
public abstract class AbstractMbpmBean {

    protected void init() {

    }

    // ------- Método de recuperação de definições --------------------

    protected abstract ProcessDefinitionCache getDefinitionCache();

    public <K extends ProcessDefinition<?>> K getProcessDefinition(Class<K> processClass) {
        return ProcessDefinitionCache.getDefinition(processClass);
    }

    protected ProcessDefinition<?> getProcessDefinition(String abbreviation) {
        return getDefinitionCache().getDefinition(abbreviation);
    }

    protected List<ProcessDefinition<?>> getDefinitions() {
        return getDefinitionCache().getDefinitions();
    }

    /**
     *
     * @deprecated mover para a implementacao do alocpro
     */
    //TODO moverparaalocpro
    @Deprecated
    private <T extends ProcessInstance> ProcessDefinition<?> getDefinicaoForInstanciaOrException(Class<T> instanceClass) {
        ProcessDefinition<?> def = getDefinitionCache().getDefinitionForInstance(instanceClass);
        if (def == null) {
            throw new SingularFlowException("Não existe definição de processo para '" + instanceClass.getName() + "'");
        }
        return def;
    }

    // ------- Método de recuperação de instâncias --------------------

    private ProcessInstance getProcessInstanceByEntityCod(Integer cod) {
        IEntityProcessInstance dadosInstanciaProcesso = getPersistenceService().retrieveProcessInstanceByCod(cod);
        ProcessDefinition<?> def = getProcessDefinition(dadosInstanciaProcesso.getProcess().getAbbreviation());
        return def.convertToProcessInstance(dadosInstanciaProcesso);
    }

    protected ProcessInstance getProcessInstance(IEntityProcessInstance entityProcessInstance) {
        return getProcessInstanceByEntityCod(entityProcessInstance.getCod());
    }

    protected final <T extends ProcessInstance> T getProcessInstance(Class<T> instanceClass, IEntityProcessInstance entityProcessInstance) {
        return instanceClass.cast(getProcessInstanceByEntityCod(entityProcessInstance.getCod()));
    }

    protected final <T extends ProcessInstance> T getProcessInstance(Class<T> instanceClass, Integer cod) {
        return instanceClass.cast(getDefinicaoForInstanciaOrException(instanceClass).getDataService().retrieveInstance(cod));
    }

    protected final <T extends ProcessInstance> T getProcessInstanceOrException(Class<T> instanceClass, String id) {
        T instance = getProcessInstance(instanceClass, id);
        if (instance == null) {
            throw new SingularFlowException("Não foi encontrada a instancia '" + id + "' do tipo " + instanceClass.getName());
        }
        return instance;
    }

    protected final <T extends ProcessInstance> T getProcessInstance(Class<T> instanceClass, String id) {
        if (StringUtils.isNumeric(id)) {
            return getProcessInstance(instanceClass, Integer.parseInt(id));
        } else {
            return instanceClass.cast(getProcessInstance(id));
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


    //TODO rever generateID e parseId, deveria ser tipado, talvez nem devesse estar nesse lugar
    @Deprecated
    protected abstract String generateID(ProcessInstance instance);

    //TODO rever generateID e parseId, deveria ser tipado, talvez nem devesse estar nesse lugar
    @Deprecated
    protected abstract String generateID(TaskInstance taskInstance);

    //TODO rever generateID e parseId, deveria ser tipado, talvez nem devesse estar nesse lugar
    @Deprecated
    protected abstract MappingId parseId(String instanceID);

    //TODO rever generateID e parseId, deveria ser tipado, talvez nem devesse estar nesse lugar
    @Deprecated
    protected static class MappingId {
        public final String abbreviation;
        public final Integer cod;

        public MappingId(String abbreviation, int cod) {
            this.abbreviation = abbreviation;
            this.cod = cod;
        }
    }

    // ------- Geração de link ----------------------------------------

    @Deprecated
    //TODO deveria ser opcional esse tipo de definicao, deveria ser simplificado
    public abstract Lnk getDefaultHrefFor(ProcessInstance processInstance);

    @Deprecated
    //TODO deveria ser opcional esse tipo de definico, deveria ser simplificado
    public abstract Lnk getDefaultHrefFor(TaskInstance taskInstance);

    // ------- Manipulação de Usuário ---------------------------------

    /**
     * Deveria delegar algo para que a aplicaçào cliente possa prover o usuário.
     */
    @Deprecated
    public abstract MUser getUserIfAvailable();

    public abstract boolean canBeAllocated(MUser user);

    /**
     * @deprecated deveria ser opcional
     */
    @Deprecated
    protected abstract AbstractProcessNotifiers getNotifiers();

    // ------- Consultas ----------------------------------------------

    public final List<? extends ProcessDefinition<?>> getEnabledProcessForCreationBy(MUser user) {
        return getDefinitions().stream().filter(d -> d.canBeCreatedBy(user)).sorted().collect(Collectors.toList());
    }

    // ------- Outros -------------------------------------------------

    public IFlowRenderer getFlowRenderer() {
        return YFilesFlowRenderer.getInstance();
    }

    protected abstract IPersistenceService<?, ?, ?, ?, ?, ?, ?, ?, ?> getPersistenceService();

    protected abstract IScheduleService getScheduleService();

    protected abstract IProcessEntityService<?, ?, ?, ?, ?, ?> getProcessEntityService();

    protected abstract void notifyStateUpdate(ProcessInstance instanciaProcessoMBPM);

    public final Object executeTask(MTaskJava task) {
        final IProcessDataService<?> dataService = task.getFlowMap().getProcessDefinition().getDataService();
        final Collection<? extends ProcessInstance> instancias = dataService.retrieveAllInstancesIn(task);
        if (task.isCalledInBlock()) {
            return task.executarByBloco(instancias);
        } else {
            for (final ProcessInstance instanciaProcessoMBPM : instancias) {
                EngineProcessamentoMBPM.executeScheduledTransition(task, instanciaProcessoMBPM);
            }
            return null;
        }
    }
}
