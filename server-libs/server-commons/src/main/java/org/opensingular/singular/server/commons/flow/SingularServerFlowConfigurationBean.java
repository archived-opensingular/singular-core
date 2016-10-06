package org.opensingular.singular.server.commons.flow;

import org.opensingular.singular.commons.util.Loggable;
import org.opensingular.flow.core.Flow;
import org.opensingular.flow.core.renderer.IFlowRenderer;
import org.opensingular.singular.flow.schedule.IScheduleService;
import org.opensingular.singular.persistence.util.HibernateSingularFlowConfigurationBean;
import org.opensingular.singular.server.commons.config.ConfigProperties;
import org.opensingular.singular.server.commons.config.SingularServerConfiguration;
import org.springframework.transaction.PlatformTransactionManager;
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
                getDefinitions().forEach(d -> {
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

}