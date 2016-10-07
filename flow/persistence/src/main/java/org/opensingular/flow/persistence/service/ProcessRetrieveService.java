/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.flow.persistence.service;

import org.hibernate.Hibernate;
import org.opensingular.flow.persistence.entity.util.SessionLocator;
import org.springframework.transaction.annotation.Transactional;

import org.opensingular.flow.persistence.entity.ProcessInstanceEntity;

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
