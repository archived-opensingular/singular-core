package org.opensingular.singular.server.commons.config;

import java.util.Map;
import java.util.Optional;

import javax.servlet.ServletContext;

import org.opensingular.flow.core.SingularFlowConfigurationBean;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import org.opensingular.flow.core.ProcessDefinition;
import org.opensingular.flow.core.service.IFlowMetadataREST;
import org.opensingular.singular.server.commons.flow.SingularServerFlowConfigurationBean;
import org.opensingular.singular.server.commons.flow.rest.DefaultServerMetadataREST;
import org.opensingular.singular.server.commons.flow.rest.DefaultServerREST;
import org.opensingular.singular.server.commons.service.IServerMetadataREST;

public abstract class FlowInitializer {


    public abstract Class<? extends IFlowMetadataREST> flowMetadataProvider();

    public Class<? extends IServerMetadataREST> serverMetadataProvider() {
        return DefaultServerMetadataREST.class;
    }

    public Class<? extends DefaultServerREST> serverActionProvider() {
        return DefaultServerREST.class;
    }

    public abstract Map<Class<? extends ProcessDefinition>, String> processDefinitionFormNameMap();


    public Class<? extends SingularFlowConfigurationBean> singularFlowConfiguration() {
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
