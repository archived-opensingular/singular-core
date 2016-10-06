/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.server.commons.persistence.dao;

import javax.transaction.Transactional;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import org.opensingular.flow.core.entity.IEntityProcessGroup;
import org.opensingular.singular.server.commons.persistence.entity.parameter.ParameterEntity;
import org.opensingular.lib.support.persistence.BaseDAO;

@Transactional(Transactional.TxType.MANDATORY)
public class ParameterDAO extends BaseDAO<ParameterEntity, Long> {

    public ParameterDAO() {
        super(ParameterEntity.class);
    }

    public ParameterEntity findByNameAndProcessGroup(String name, IEntityProcessGroup processGroup) {
        Criteria c = getSession().createCriteria(tipo);
        c.add(Restrictions.eq("name", name));
        c.add(Restrictions.eq("codProcessGroup", processGroup.getCod()));
        c.addOrder(Order.asc("cod"));
        c.setMaxResults(1);
        return (ParameterEntity) c.uniqueResult();
    }
}
