package br.net.mirante.singular.server.commons.config;

import java.util.Map;
import java.util.Optional;

import javax.servlet.ServletContext;

import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import br.net.mirante.singular.flow.core.ProcessDefinition;
import br.net.mirante.singular.flow.core.service.IFlowMetadataREST;
import br.net.mirante.singular.server.commons.flow.SingularServerFlowConfigurationBean;
import br.net.mirante.singular.server.commons.flow.rest.DefaultServerMetadataREST;
import br.net.mirante.singular.server.commons.flow.rest.DefaultServerREST;
import br.net.mirante.singular.server.commons.service.IServerMetadataREST;

public abstract class FlowInitializer {


    public abstract Class<? extends IFlowMetadataREST> flowMetadataProvider();

    public Class<? extends IServerMetadataREST> serverMetadataProvider() {
        return DefaultServerMetadataREST.class;
    }

    public Class<? extends DefaultServerREST> serverActionProvider() {
        return DefaultServerREST.class;
    }

    //TODO (lucas.lopes) - definir outra forma. Anotação no processo talvez.
    public abstract Map<Class<? extends ProcessDefinition>, String> processDefinitionFormNameMap();


    public Class<? extends br.net.mirante.singular.flow.core.SingularFlowConfigurationBean> singularFlowConfiguration() {
        return SingularServerFlowConfigurationBean.class;
    }

    public abstract String[] definitionsBasePackage();

    public abstract String processGroupCod();

    public void init(ServletContext ctx, AnnotationConfigWebApplicationContext applicationContext) {
        applicationContext.register(singularFlowConfiguration());
        Optional
                .ofNullable(flowMetadataProvider())
                .ifPresent(applicationContext::register);
        Optional
                .ofNullable(serverMetadataProvider())
                .ifPresent(applicationContext::register);
        Optional
                .ofNullable(serverActionProvider())
                .ifPresent(applicationContext::register);
    }
}
