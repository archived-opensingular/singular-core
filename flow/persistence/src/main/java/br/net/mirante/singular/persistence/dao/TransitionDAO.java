package br.net.mirante.singular.persistence.dao;

import br.net.mirante.singular.persistence.entity.Task;
import br.net.mirante.singular.persistence.entity.Transition;
import br.net.mirante.singular.persistence.entity.util.SessionLocator;

import java.io.Serializable;

public class TransitionDAO extends AbstractHibernateDAO<Transition> {


    public TransitionDAO(SessionLocator sessionLocator) {
        super(sessionLocator);
    }

}
