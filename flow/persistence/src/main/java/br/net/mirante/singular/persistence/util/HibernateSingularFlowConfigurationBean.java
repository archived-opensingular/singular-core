/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.persistence.util;

import org.hibernate.SessionFactory;
import org.springframework.util.Assert;

import br.net.mirante.singular.flow.core.SingularFlowConfigurationBean;
import br.net.mirante.singular.flow.core.service.IPersistenceService;
import br.net.mirante.singular.flow.core.service.IProcessDefinitionEntityService;
import br.net.mirante.singular.flow.core.service.IUserService;
import br.net.mirante.singular.persistence.entity.util.SessionLocator;
import br.net.mirante.singular.persistence.service.DefaultHibernatePersistenceService;
import br.net.mirante.singular.persistence.service.DefaultHibernateProcessDefinitionService;

public class HibernateSingularFlowConfigurationBean extends SingularFlowConfigurationBean {
    private String   definitionsBasePackage;
    private IUserService userService;
    private SessionFactory sessionFactory;
    private SessionLocator sessionLocator = ()-> sessionFactory.getCurrentSession();

    public HibernateSingularFlowConfigurationBean() {
        super(null);
    }
    
    /**
     * @param processGroupCod - chave do sistema cadastrado no em <code>TB_GRUPO_PROCESSO</code>
     */
    protected HibernateSingularFlowConfigurationBean(String processGroupCod) {
        super(processGroupCod);
    }
    
    @Override
    protected String getDefinitionsBasePackage() {
        return this.definitionsBasePackage;
    }

    public void setDefinitionsBasePackage(String definitionsBasePackage) {
        Assert.hasLength(definitionsBasePackage, "O pacote base onde estao as classe de definicao nao pode ser nulo ou vazio");
        this.definitionsBasePackage = definitionsBasePackage;
    }

    @Override
    public IUserService getUserService() {
        return this.userService;
    }

    public void setUserService(IUserService userService) {
        this.userService = userService;
    }

    @Override
    protected IPersistenceService<?, ?, ?, ?, ?, ?, ?, ?, ?, ?> getPersistenceService() {
        return new DefaultHibernatePersistenceService(getSessionLocator());
    }

    @Override
    protected IProcessDefinitionEntityService<?, ?, ?, ?, ?, ?, ?> getProcessEntityService() {
        return new DefaultHibernateProcessDefinitionService(getSessionLocator());
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
