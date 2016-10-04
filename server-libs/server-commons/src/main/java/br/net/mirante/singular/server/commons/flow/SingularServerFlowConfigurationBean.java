package br.net.mirante.singular.server.commons.flow;

import br.net.mirante.singular.commons.util.Loggable;
import br.net.mirante.singular.flow.core.Flow;
import br.net.mirante.singular.flow.core.renderer.IFlowRenderer;
import br.net.mirante.singular.flow.schedule.IScheduleService;
import br.net.mirante.singular.persistence.util.HibernateSingularFlowConfigurationBean;
import br.net.mirante.singular.server.commons.config.ConfigProperties;
import br.net.mirante.singular.server.commons.config.SingularServerConfiguration;
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