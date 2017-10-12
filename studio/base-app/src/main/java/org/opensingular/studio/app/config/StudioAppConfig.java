package org.opensingular.studio.app.config;

import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.lib.commons.context.ServiceRegistry;
import org.opensingular.lib.commons.context.SingularSingletonStrategy;
import org.opensingular.studio.core.config.StudioConfig;
import org.opensingular.studio.core.wicket.StudioApplication;

import java.util.List;

public interface StudioAppConfig extends StudioConfig{

    /**
     * @return the Wicket Application
     */
    StudioApplication getWicketApplication();


    /**
     * Allow register another classes to be registered as spring config
     * @return the configs
     */
    List<Class<?>> getSpringAnnotatedConfigs();

    /**
     *
     */
    SingularSingletonStrategy getSingularSingletonStrategy();

    /**
     *
     */
    SDocumentFactory getSDocumentFactory();

    /**
     *
     */
    ServiceRegistry getServiceRegistry();

}
