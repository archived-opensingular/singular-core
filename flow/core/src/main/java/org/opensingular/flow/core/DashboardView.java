/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.core;

import java.util.List;
import java.util.Map;

public abstract class DashboardView<C extends DashboardContext> {

    private String name;
    private String title;
    private String subtitle;
    private Class<? extends DashboardFilter> dashboardFilterClass;

    public DashboardView(String title, String subtitle) {
        this.name = getClass().getSimpleName();
        this.title = title;
        this.subtitle = subtitle;
    }

    public DashboardView(String name, String title, String subtitle) {
        this.name = name;
        this.title = title;
        this.subtitle = subtitle;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public Class<? extends DashboardFilter> getDashboardFilterClass() {
        return dashboardFilterClass;
    }

    public void setDashboardFilterClass(Class<? extends DashboardFilter> dashboardFilterClass) {
        this.dashboardFilterClass = dashboardFilterClass;
    }

    public abstract List<Map<String, String>> getData(C context);

}
