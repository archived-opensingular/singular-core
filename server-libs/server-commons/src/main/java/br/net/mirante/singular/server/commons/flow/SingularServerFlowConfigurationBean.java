package br.net.mirante.singular.server.commons.flow;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import br.net.mirante.singular.commons.util.Loggable;
import br.net.mirante.singular.flow.core.Flow;
import br.net.mirante.singular.flow.core.renderer.IFlowRenderer;
import br.net.mirante.singular.flow.schedule.IScheduleService;
import br.net.mirante.singular.persistence.util.HibernateSingularFlowConfigurationBean;
import br.net.mirante.singular.server.commons.config.ConfigProperties;
import br.net.mirante.singular.server.commons.config.SingularServerConfiguration;
import br.net.mirante.singular.server.commons.flow.renderer.remote.YFilesFlowRemoteRenderer;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.transaction.Transactional;

public class SingularServerFlowConfigurationBean extends HibernateSingularFlowConfigurationBean implements Loggable {

    @Inject
    protected SingularServerConfiguration singularServerConfiguration;
    @Inject
    protected PlatformTransactionManager transactionManager;

    @Inject
    private IScheduleService scheduleService;

    @Inject
    private IFlowRenderer flowRenderer;

    @PostConstruct
    protected void postConstruct() {
        this.setProcessGroupCod(singularServerConfiguration.getProcessGroupCod());
        this.setDefinitionsPackages(singularServerConfiguration.getDefinitionsPackages());
        Flow.setConf(this);
        initializeFlowDefinitionsDatabase();
    }

    @Override
    public IFlowRenderer getFlowRenderer() {
        return flowRenderer;
    }
    
    @Override
    protected IScheduleService getScheduleService() {
        return scheduleService;
    }

    @Transactional
    public void initializeFlowDefinitionsDatabase() {
        if ("true".equals(ConfigProperties.get(ConfigProperties.SINGULAR_EAGER_LOAD_FLOW_DEFINITIONS))) {
            new TransactionTemplate(transactionManager).execute(status -> {
                getLogger().info("INITIALIZING FLOW DEFINITIONS");
                getDefinitions().stream().forEach(d -> {
                    try {
                        getLogger().info("INITIALIZING " + d.getName() + "....");
                        d.getEntityProcessVersion();
                    } catch (Exception e) {
                        getLogger().error(e.getMessage(), e);
                    }
                });
                return null;
            });
        }
    }

    @Override
    protected IScheduleService getScheduleService() {
        return new TransactionalQuartzScheduledService(transactionManager);
    }

}