package br.net.mirante.singular.persistence.util;

import br.net.mirante.singular.flow.core.SingularFlowConfigurationBean;
import br.net.mirante.singular.flow.core.service.IPersistenceService;
import br.net.mirante.singular.flow.core.service.IProcessEntityService;
import br.net.mirante.singular.flow.core.service.IUserService;
import br.net.mirante.singular.persistence.entity.util.SessionLocator;
import br.net.mirante.singular.persistence.service.DefaultHibernatePersistenceService;
import br.net.mirante.singular.persistence.service.DefaultHibernateProcessDefinitionService;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.util.Assert;

public class HibernateSingularFlowConfigurationBean extends SingularFlowConfigurationBean {

    private String   definitionsBasePackage;
    private IUserService userService;
    private SessionLocator sessionLocator = new SessionLocator() {
        @Override
        public Session getCurrentSession() {
            return sessionFactory.getCurrentSession();
        }
    };
    private SessionFactory sessionFactory;

    @Override
    protected String getDefinitionsBasePackage() {
        return this.definitionsBasePackage;
    }

    public void setDefinitionsBasePackage(String definitionsBasePackage) {
        Assert.hasLength(definitionsBasePackage, "O pacote base onde estao as classe de definicao nao pode ser nulo ou vazio");
        this.definitionsBasePackage = definitionsBasePackage;
    }

    @Override
    protected IUserService getUserService() {
        return this.userService;
    }

    public void setUserService(IUserService userService) {
        this.userService = userService;
    }

    @Override
    protected IPersistenceService<?, ?, ?, ?, ?, ?, ?, ?, ?> getPersistenceService() {
        return new DefaultHibernatePersistenceService(this.getSessionLocator());
    }

    @Override
    protected IProcessEntityService<?, ?, ?, ?, ?, ?> getProcessEntityService() {
        return new DefaultHibernateProcessDefinitionService(this.getSessionLocator());
    }

    public SessionLocator getSessionLocator() {
        return this.sessionLocator;
    }

    public void setSessionLocator(SessionLocator sessionLocator) {
        Assert.notNull(sessionLocator, "O SessionLocator  pode ser nulo");
        this.sessionLocator = sessionLocator;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        Assert.notNull(sessionFactory, "A session factory pode ser nula");
        this.sessionFactory = sessionFactory;
    }
}
