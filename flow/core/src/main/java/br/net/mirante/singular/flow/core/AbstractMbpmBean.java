package br.net.mirante.singular.flow.core;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import br.net.mirante.singular.flow.core.entity.IEntityProcessInstance;
import br.net.mirante.singular.flow.core.entity.persistence.IPersistenceService;
import br.net.mirante.singular.flow.schedule.IScheduleService;
import br.net.mirante.singular.flow.util.view.Lnk;

public abstract class AbstractMbpmBean {

    protected void init() {

    }

    // ------- Método de recuperação de definições --------------------

    protected abstract ProcessDefinitionCache getDefinitionCache();

    public <K extends ProcessDefinition<?>> K getProcessDefinition(Class<K> processClass) {
        return ProcessDefinitionCache.getDefinition(processClass);
    }

    public ProcessDefinition<?> getProcessDefinition(String abbreviation) {
        return getDefinitionCache().getDefinition(abbreviation);
    }

    public List<ProcessDefinition<?>> getDefinitions() {
        return getDefinitionCache().getDefinitions();
    }

    private <T extends ProcessInstance> ProcessDefinition<?> getDefinicaoForInstanciaOrException(Class<T> instanceClass) {
        ProcessDefinition<?> def = getDefinitionCache().getDefinitionForInstance(instanceClass);
        if (def == null) {
            throw new RuntimeException("Não existe definição de processo para '" + instanceClass.getName() + "'");
        }
        return def;
    }

    // ------- Método de recuperação de instâncias --------------------

    private ProcessInstance getProcessInstanceByEntityCod(Integer cod) {
        IEntityProcessInstance dadosInstanciaProcesso = getPersistenceService().retrieveProcessInstanceByCod(cod);
        ProcessDefinition<?> def = getProcessDefinition(dadosInstanciaProcesso.getDefinicao().getSigla());
        return def.convertToProcessInstance(dadosInstanciaProcesso);
    }

    public ProcessInstance getProcessInstance(IEntityProcessInstance entityProcessInstance) {
        return getProcessInstanceByEntityCod(entityProcessInstance.getCod());
    }

    public final <T extends ProcessInstance> T findProcessInstance(Class<T> instanceClass, Integer cod) {
        return instanceClass.cast(getDefinicaoForInstanciaOrException(instanceClass).retrieveProcessInstance(cod));
    }

    public final <T extends ProcessInstance> T findProcessInstanceOrException(Class<T> instanceClass, String id) {
        T instance = findProcessInstance(instanceClass, id);
        if (instance == null) {
            throw new RuntimeException("Não foi encontrada a instancia '" + id + "' do tipo " + instanceClass.getName());
        }
        return instance;
    }

    public final <T extends ProcessInstance> T findProcessInstance(Class<T> instanceClass, String id) {
        if (StringUtils.isNumeric(id)) {
            return findProcessInstance(instanceClass, Integer.parseInt(id));
        } else {
            return instanceClass.cast(findProcessInstance(id));
        }
    }

    @SuppressWarnings("unchecked")
    public <X extends ProcessInstance> X findProcessInstance(String instanceID) {
        if (instanceID == null) {
            return null;
        }
        MappingId mapeamento = parseId(instanceID);
        if (mapeamento.abbreviation == null) {
            return (X) getProcessInstanceByEntityCod(mapeamento.cod);
        } else {
            final ProcessDefinition<?> def = getProcessDefinition(mapeamento.abbreviation);
            if (def == null) {
                throw new RuntimeException("Não existe definição de processo '" + mapeamento.abbreviation + "'");
            }
            return (X) def.retrieveProcessInstance(mapeamento.cod);
        }
    }

    // ------- Manipulação de ID --------------------------------------

    protected abstract String generateID(ProcessInstance instance);

    protected abstract String generateID(TaskInstance taskInstance);

    protected abstract MappingId parseId(String instanceID);

    protected static class MappingId {
        public final String abbreviation;
        public final Integer cod;

        public MappingId(String abbreviation, int cod) {
            this.abbreviation = abbreviation;
            this.cod = cod;
        }
    }

    // ------- Geração de link ----------------------------------------

    public abstract Lnk getDefaultHrefFor(ProcessInstance INSTANCE);

    public abstract Lnk getDefaultHrefFor(TaskInstance taskInstance);

    // ------- Manipulação de Usuário ---------------------------------

    public abstract MUser getUserIfAvailable();

    public abstract boolean canBeAllocated(MUser user);

    protected abstract AbstractProcessNotifiers getNotifiers();

    // ------- Consultas ----------------------------------------------

    public final List<ProcessDefinition<?>> getEnabledProcessForCreationBy(MUser user) {
        return getDefinitions().stream().filter(d -> d.canBeCreatedBy(user)).sorted().collect(Collectors.toList());
    }

    // ------- Outros -------------------------------------------------

    protected abstract IPersistenceService<?, ?, ?, ?, ?, ?, ?, ?> getPersistenceService();

    protected abstract IScheduleService getScheduleService();

    protected abstract void notifyStateUpdate(ProcessInstance instanciaProcessoMBPM);

    public final Object executeTask(MTaskJava task) {
        final ProcessDefinition<?> definicao = task.getFlowMap().getProcessDefinition();
        final Collection<? extends ProcessInstance> instancias = definicao.getInstanciasNoEstado(task);
        if (task.isCalledInBlock()) {
            return task.executarByBloco(instancias);
        } else {
            for (final ProcessInstance instanciaProcessoMBPM : instancias) {
                EngineProcessamentoMBPM.executarTransicaoAgendada(task, instanciaProcessoMBPM);
            }
            return null;
        }
    }

}
