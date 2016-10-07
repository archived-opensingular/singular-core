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
