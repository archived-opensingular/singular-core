package br.net.mirante.singular.server.commons.config;

import br.net.mirante.singular.flow.core.ProcessDefinition;
import br.net.mirante.singular.flow.core.service.IFlowMetadataREST;
import br.net.mirante.singular.server.commons.flow.DefaultServerMetadataREST;
import br.net.mirante.singular.server.commons.flow.SingularServerFlowConfigurationBean;
import br.net.mirante.singular.server.commons.service.IServerMetadataREST;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import javax.servlet.ServletContext;
import java.util.Map;
import java.util.Optional;

public abstract class FlowInitializer {


    public abstract Class<? extends IFlowMetadataREST> flowMetadataProvider();

    public Class<? extends IServerMetadataREST> serverMetadataProvider() {
        return DefaultServerMetadataREST.class;
    }

    public abstract Map<Class<? extends ProcessDefinition>, String> processDefinitionFormNameMap();


    public Class<? extends br.net.mirante.singular.flow.core.SingularFlowConfigurationBean> singularFlowConfiguration() {
        return SingularServerFlowConfigurationBean.class;
    }

    public abstract String definitionsBasePackage();

    public abstract String processGroupCod();

    public void init(ServletContext ctx, AnnotationConfigWebApplicationContext applicationContext) {
        applicationContext.register(singularFlowConfiguration());
        Optional
                .ofNullable(flowMetadataProvider())
                .ifPresent(f -> applicationContext.register(f));
        Optional
                .ofNullable(serverMetadataProvider())
                .ifPresent(f -> applicationContext.register(f));
    }
}
