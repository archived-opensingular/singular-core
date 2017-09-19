package org.opensingular.studio.core.config;

import org.opensingular.form.context.ServiceRegistry;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.lib.commons.context.SingularSingletonStrategy;
import org.opensingular.studio.core.menu.StudioMenu;
import org.opensingular.studio.core.wicket.StudioApplication;

import java.util.List;

public interface StudioAppConfig {
    /**
     * @return the Wicket Application
     */
    StudioApplication getWicketApplication();

    /**
     * @return the app menu
     */
    StudioMenu getAppMenu();

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