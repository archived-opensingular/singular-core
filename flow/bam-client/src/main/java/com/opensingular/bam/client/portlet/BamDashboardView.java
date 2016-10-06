/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.opensingular.bam.client.portlet;

import java.util.function.Supplier;

import com.opensingular.bam.client.chart.SingularChart;
import org.opensingular.singular.flow.core.DashboardView;

public abstract class BamDashboardView extends DashboardView<PortletContext> {

    private SingularChart chart;
    private PortletSize portletSize = PortletSize.MEDIUM;
    private Supplier<? extends ChartPortletConfig> supplier;

    public BamDashboardView(String titulo, String subtitulo,
                            SingularChart chart, PortletSize portletSize,
                            Supplier<? extends ChartPortletConfig> supplier) {
        super(titulo, subtitulo);
        this.chart = chart;
        this.portletSize = portletSize;
        this.supplier = supplier;
    }

    public SingularChart getChart() {
        return chart;
    }

    public PortletSize getPortletSize() {
        return portletSize;
    }

    public Supplier<? extends ChartPortletConfig> getSupplier() {
        return supplier;
    }
}
