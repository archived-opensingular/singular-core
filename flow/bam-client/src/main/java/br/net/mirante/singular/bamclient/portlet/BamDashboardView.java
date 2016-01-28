package br.net.mirante.singular.bamclient.portlet;

import java.util.function.Supplier;

import br.net.mirante.singular.bamclient.chart.SingularChart;
import br.net.mirante.singular.flow.core.DashboardView;

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
