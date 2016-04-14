package br.net.mirante.singular.pet.commons.config;

import br.net.mirante.singular.flow.core.SingularFlowConfigurationBean;
import br.net.mirante.singular.flow.core.service.IFlowMetadataREST;
import br.net.mirante.singular.persistence.service.DefaultHibernatePersistenceService;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import javax.servlet.ServletContext;

public abstract class FlowInitializer {

    public abstract Class<? extends IFlowMetadataREST> flowMetadataProvider();

    public abstract Class<? extends SingularFlowConfigurationBean> singularFlowConfiguration();

    public void init(ServletContext ctx, AnnotationConfigWebApplicationContext applicationContext) {
        applicationContext.register(singularFlowConfiguration());
        applicationContext.register(flowMetadataProvider());
    }
}
