/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.persistence.service;

import org.hibernate.Hibernate;
import org.springframework.transaction.annotation.Transactional;

import br.net.mirante.singular.persistence.entity.ProcessInstanceEntity;
import br.net.mirante.singular.persistence.entity.util.SessionLocator;

@Transactional(readOnly = true)
public class ProcessRetrieveService extends AbstractHibernateService {

    public void setSessionLocator(SessionLocator sessionLocator) {
        this.sessionLocator = sessionLocator;
    }

    public ProcessInstanceEntity retrieveProcessInstanceByCod(Integer cod) {
        ProcessInstanceEntity pi =  getSession().retrieve(ProcessInstanceEntity.class, cod);
        pi.getTasks().forEach(t -> Hibernate.initialize(t.getTask()));
        return pi;
    }

}
