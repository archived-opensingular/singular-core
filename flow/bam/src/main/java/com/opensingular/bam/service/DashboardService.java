/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.opensingular.bam.service;

import java.util.List;

import org.opensingular.flow.persistence.entity.Dashboard;

public interface DashboardService {

    List<Dashboard> retrieveCustomDashboards();

    Dashboard retrieveDashboardById(String customDashboardCode);

}
