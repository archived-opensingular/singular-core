package br.net.mirante.singular.service;

import br.net.mirante.singular.flow.core.AbstractMbpmBean;
import br.net.mirante.singular.flow.core.AbstractProcessNotifiers;
import br.net.mirante.singular.flow.core.MUser;
import br.net.mirante.singular.flow.core.ProcessDefinitionCache;
import br.net.mirante.singular.flow.core.ProcessInstance;
import br.net.mirante.singular.flow.core.TaskInstance;
import br.net.mirante.singular.flow.core.service.IPersistenceService;
import br.net.mirante.singular.flow.schedule.IScheduleService;
import br.net.mirante.singular.flow.util.view.Lnk;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * Essa classe deve ficar na persistencia padrao ou todas as aplicacoes devem implementar???
 *
 */
@Component
public class MBPMBean extends AbstractMbpmBean {

    private static final String PACKAGE_DEFINICOES = "com.miranteinfo.alocpro.processo.mbpm";

    @Inject
    private MBPMService mbpmService;

    @Override
    protected ProcessDefinitionCache getDefinitionCache() {
        return ProcessDefinitionCache.get(PACKAGE_DEFINICOES);
    }

    @Override
    protected String generateID(ProcessInstance instance) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected String generateID(TaskInstance taskInstance) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected MappingId parseId(String instanceID) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Lnk getDefaultHrefFor(ProcessInstance processInstance) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Lnk getDefaultHrefFor(TaskInstance taskInstance) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MUser getUserIfAvailable() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean canBeAllocated(MUser user) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected AbstractProcessNotifiers getNotifiers() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected IPersistenceService<?, ?, ?, ?, ?, ?, ?, ?> getPersistenceService() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected IScheduleService getScheduleService() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void notifyStateUpdate(ProcessInstance instanciaProcessoMBPM) {
        throw new UnsupportedOperationException();
    }
}
