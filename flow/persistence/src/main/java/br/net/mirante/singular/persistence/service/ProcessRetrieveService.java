package br.net.mirante.singular.persistence.service;

import org.springframework.transaction.annotation.Transactional;

import br.net.mirante.singular.persistence.entity.ProcessInstanceEntity;
import br.net.mirante.singular.persistence.entity.util.SessionLocator;

@Transactional(readOnly = true)
public class ProcessRetrieveService extends AbstractHibernateService {

    public void setSessionLocator(SessionLocator sessionLocator) {
        this.sessionLocator = sessionLocator;
    }

    public ProcessInstanceEntity retrieveProcessInstanceByCod(Integer cod) {
        return getSession().retrieve(ProcessInstanceEntity.class, cod);
    }

}
