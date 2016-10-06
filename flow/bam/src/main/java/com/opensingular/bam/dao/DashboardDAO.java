/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.opensingular.bam.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import org.opensingular.flow.persistence.entity.Dashboard;

@Repository
public class DashboardDAO extends BaseDAO {

    public List<Dashboard> retrieveCustomDashboards() {
        return getSession().createCriteria(Dashboard.class)
                .setFetchMode("portlets", FetchMode.JOIN)
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .list();
    }

    public Dashboard retrieveDashboardById(Long customDashboardCode) {
        return (Dashboard) getSession().createCriteria(Dashboard.class)
                .setFetchMode("portlets", FetchMode.JOIN)
                .add(Restrictions.eq("id", customDashboardCode))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .uniqueResult();
    }
}
