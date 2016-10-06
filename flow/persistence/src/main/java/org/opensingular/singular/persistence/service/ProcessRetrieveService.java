/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.persistence.service;

import org.hibernate.Hibernate;
import org.springframework.transaction.annotation.Transactional;

import org.opensingular.singular.persistence.entity.ProcessInstanceEntity;
import org.opensingular.singular.persistence.entity.util.SessionLocator;

@Transactional(readOnly = true)
public class ProcessRetrieveService extends AbstractHibernateService {

    public void setSessionLocator(SessionLocator sessionLocator) {
        this.sessionLocator = sessionLocator;
    }

    /**
     *
     * @param cod
     * @return
     * @deprecated
     * Transformar em DTO essa busca da vários problemas de lazy para o historico (HistoricoContent)
     */
    @Deprecated
    public ProcessInstanceEntity retrieveProcessInstanceByCod(Integer cod) {
        ProcessInstanceEntity pi =  getSession().retrieve(ProcessInstanceEntity.class, cod);
        pi.getTasks().forEach(t -> {
            Hibernate.initialize(t.getTask());
            Hibernate.initialize(t.getAllocatedUser());
        });
        return pi;
    }

}
