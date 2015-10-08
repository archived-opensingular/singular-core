package br.net.mirante.singular;

import br.net.mirante.singular.flow.core.SingularFlowConfigurationBean;
import br.net.mirante.singular.flow.core.service.IPersistenceService;
import br.net.mirante.singular.flow.core.service.IProcessEntityService;
import br.net.mirante.singular.flow.core.service.IUserService;
import br.net.mirante.singular.persistence.service.DefaultHibernatePersistenceService;
import br.net.mirante.singular.persistence.service.DefaultHibernateProcessDefinitionService;
import org.hibernate.SessionFactory;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class TestFlowConfiguration extends SingularFlowConfigurationBean {

    public static final String BASE_PACKAGE_TO_SCAN = "br.net.mirante.singular";

    @Inject
    private TestUserService testUserService;

    @Inject
    private SessionFactory sessionFactory;


    @Override
    protected String getDefinitionsBasePackage() {
        return BASE_PACKAGE_TO_SCAN;
    }

    @Override
    protected IUserService getUserService() {
        return testUserService;
    }

    @Override
    protected IPersistenceService<?, ?, ?, ?, ?, ?, ?, ?, ?> getPersistenceService() {
        return new DefaultHibernatePersistenceService(() -> sessionFactory.getCurrentSession());
    }

    @Override
    protected IProcessEntityService<?, ?, ?, ?, ?, ?> getProcessEntityService() {
        return new DefaultHibernateProcessDefinitionService(() -> sessionFactory.getCurrentSession());
    }

}
