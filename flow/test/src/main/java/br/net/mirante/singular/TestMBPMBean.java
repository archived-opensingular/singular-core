package br.net.mirante.singular;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.util.Assert;

import br.net.mirante.singular.flow.core.AbstractMbpmBean;
import br.net.mirante.singular.flow.core.AbstractProcessNotifiers;
import br.net.mirante.singular.flow.core.MUser;
import br.net.mirante.singular.flow.core.ProcessDefinitionCache;
import br.net.mirante.singular.flow.core.ProcessInstance;
import br.net.mirante.singular.flow.core.TaskInstance;
import br.net.mirante.singular.flow.core.service.IPersistenceService;
import br.net.mirante.singular.flow.core.service.IProcessEntityService;
import br.net.mirante.singular.flow.schedule.IScheduleService;
import br.net.mirante.singular.flow.schedule.quartz.QuartzScheduleService;
import br.net.mirante.singular.flow.util.view.Lnk;
import br.net.mirante.singular.persistence.entity.Actor;
import br.net.mirante.singular.persistence.service.DefaultHibernatePersistenceService;
import br.net.mirante.singular.persistence.service.DefaultHibernateProcessDefinitionService;

@Named
public class TestMBPMBean extends AbstractMbpmBean {

    public static final String PREFIXO = "SGL";

    @Inject
    private SessionFactory sessionFactory;

    @Override
    protected ProcessDefinitionCache getDefinitionCache() {
        return ProcessDefinitionCache.get(CoisasQueDeviamSerParametrizadas.PACKAGES_TO_SCAN);
    }

    //TODO rever generateID e parseId, deveria ser tipado, talvez nem devesse estar nesse lugar
    @Override
    protected String generateID(ProcessInstance instancia) {
        return new StringBuilder(50)
                .append(PREFIXO)
                .append('.')
                .append(instancia.getProcessDefinition().getAbbreviation())
                .append('.')
                .append(instancia.getId()).toString();
    }

    //TODO rever generateID e parseId, deveria ser tipado, talvez nem devesse estar nesse lugar
    @Override
    protected String generateID(TaskInstance instanciaTarefa) {
        ProcessInstance instanciaProcesso = instanciaTarefa.getProcessInstance();
        return new StringBuilder(generateID(instanciaProcesso))
                .append('.')
                .append(instanciaTarefa.getId())
                .toString();
    }

    //TODO rever generateID e parseId, deveria ser tipado, talvez nem devesse estar nesse lugar
    @Override
    protected MappingId parseId(String instanciaID) {
        Assert.hasLength(instanciaID);
        String parts[] = instanciaID.split("\\.");
        String sigla = parts[parts.length - 2];
        String id = parts[parts.length - 1];
        return new MappingId(sigla, Integer.parseInt(id));
    }

    @Override
    public Lnk getDefaultHrefFor(ProcessInstance processInstance) {
        return CoisasQueDeviamSerParametrizadas.LINK_INSTANCE;
    }

    @Override
    public Lnk getDefaultHrefFor(TaskInstance taskInstance) {
        return CoisasQueDeviamSerParametrizadas.LINK_TASK;
    }

    @Override
    public MUser getUserIfAvailable() {
        return (MUser) sessionFactory.getCurrentSession().createCriteria(Actor.class)
                .add(Restrictions.idEq(CoisasQueDeviamSerParametrizadas.USER.getCod())).uniqueResult();
    }

    @Override
    public boolean canBeAllocated(MUser user) {
        return false;
    }

    @Override
    protected AbstractProcessNotifiers getNotifiers() {
        return CoisasQueDeviamSerParametrizadas.NOTIFIER;
    }

    @Override
    protected IPersistenceService<?, ?, ?, ?, ?, ?, ?, ?, ?> getPersistenceService() {
        return new DefaultHibernatePersistenceService(() -> sessionFactory.getCurrentSession());
    }

    @Override
    protected IScheduleService getScheduleService() {
        return new QuartzScheduleService();
    }

    @Override
    protected IProcessEntityService<?, ?, ?, ?, ?, ?> getProcessEntityService() {
        return new DefaultHibernateProcessDefinitionService(() -> sessionFactory.getCurrentSession());
    }

    /**
     * @param instanciaProcessoMBPM
     * @deprecated esse método deveria ir para o componente de notificação.
     */
    @Deprecated
    @Override
    protected void notifyStateUpdate(ProcessInstance instanciaProcessoMBPM) {
    }
}
