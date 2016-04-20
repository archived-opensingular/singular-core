package br.net.mirante.singular.server.commons.flow;

import br.net.mirante.singular.flow.core.Flow;
import br.net.mirante.singular.flow.core.renderer.IFlowRenderer;
import br.net.mirante.singular.persistence.util.HibernateSingularFlowConfigurationBean;
import br.net.mirante.singular.server.commons.config.SingularServerConfiguration;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

public class SingularServerFlowConfigurationBean extends HibernateSingularFlowConfigurationBean {

    @Inject
    private SingularServerConfiguration singularServerConfiguration;

    @PostConstruct
    protected void postConstruct() {
        this.setProcessGroupCod(singularServerConfiguration.getProcessGroupCod());
        this.setDefinitionsBasePackage(singularServerConfiguration.getDefinitionsBasePackage());
        Flow.setConf(this);
    }

    @Override
    public IFlowRenderer getFlowRenderer() {
        return JGraphFlowRenderer.INSTANCE;
    }
}
