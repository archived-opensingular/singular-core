/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.opensingular.bam.service;

import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;

import com.opensingular.bam.dao.DashboardDAO;
import org.springframework.stereotype.Service;

import org.opensingular.flow.persistence.entity.Dashboard;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Inject
    private DashboardDAO dashboardDAO;

    @Override
    @Transactional
    public List<Dashboard> retrieveCustomDashboards() {
        return dashboardDAO.retrieveCustomDashboards();
    }

    @Override
    @Transactional
    public Dashboard retrieveDashboardById(String customDashboardCode) {
        return dashboardDAO.retrieveDashboardById(Long.valueOf(customDashboardCode));
    }
}
