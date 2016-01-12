package br.net.mirante.singular.view.page.dashboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import br.net.mirante.singular.bamclient.builder.amchart.AmChartValueField;
import br.net.mirante.singular.bamclient.chart.AreaChart;
import br.net.mirante.singular.bamclient.chart.ColumnSerialChart;
import br.net.mirante.singular.bamclient.chart.DonutPieChart;
import br.net.mirante.singular.bamclient.chart.LineSerialChart;
import br.net.mirante.singular.bamclient.chart.PieChart;
import br.net.mirante.singular.bamclient.chart.SingularChart;
import br.net.mirante.singular.bamclient.portlet.AmChartPortletConfig;
import br.net.mirante.singular.bamclient.portlet.MorrisChartPortletConfig;
import br.net.mirante.singular.bamclient.portlet.PortletConfig;
import br.net.mirante.singular.bamclient.portlet.PortletQuickFilter;
import br.net.mirante.singular.bamclient.portlet.PortletSize;
import br.net.mirante.singular.flow.core.authorization.AccessLevel;
import br.net.mirante.singular.flow.core.dto.IStatusDTO;
import br.net.mirante.singular.util.wicket.resource.Color;
import br.net.mirante.singular.util.wicket.resource.Icone;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.$m;
import br.net.mirante.singular.view.component.PortletView;
import br.net.mirante.singular.view.page.processo.MetadadosPage;
import br.net.mirante.singular.view.page.processo.ProcessosPage;
import br.net.mirante.singular.view.template.Content;

@SuppressWarnings("serial")
public class DashboardContent extends Content {

    private String processDefinitionCode;
    private RepeatingView rows;
    private RepeatingView portlets;
    private List<PortletConfig<?>> configs = new ArrayList<>();

    public DashboardContent(String id, String processDefinitionCode) {
        super(id, false, false, false, true);
        this.processDefinitionCode = processDefinitionCode;
    }

    @Override
    protected IModel<?> getContentTitlelModel() {
        if (processDefinitionCode == null) {
            return new ResourceModel("label.content.title");
        } else {
            return $m.ofValue(uiAdminFacade.retrieveProcessDefinitionName(processDefinitionCode));
        }
    }

    @Override
    protected IModel<?> getContentSubtitlelModel() {
        return new ResourceModel("label.content.subtitle");
    }

    protected WebMarkupContainer getBreadcrumbLinks(String id) {
        RepeatingView breadCrumb = new RepeatingView(id);
        if (processDefinitionCode == null) {
            breadCrumb.add(createBreadCrumbLink(breadCrumb.newChildId(),
                    urlFor(ProcessosPage.class, new PageParameters()),
                    getString("breadcrumb.flow.process")));
        } else {
            PageParameters pageParameters = new PageParameters().set(Content.PROCESS_DEFINITION_COD_PARAM, processDefinitionCode);

            breadCrumb.add(createActiveBreadCrumbLink(breadCrumb.newChildId(),
                    urlFor(DashboardPage.class, pageParameters).toString(),
                    getString("breadcrumb.dashboard")));
            breadCrumb.add(createBreadCrumbLink(breadCrumb.newChildId(),
                    urlFor(ProcessosPage.class, pageParameters).toString(),
                    getString("breadcrumb.instances")));
            breadCrumb.add(createBreadCrumbLink(breadCrumb.newChildId(),
                    urlFor(MetadadosPage.class, pageParameters).toString(),
                    getString("breadcrumb.metadata")));
            return breadCrumb;
        }
        return breadCrumb;
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(JavaScriptReferenceHeaderItem.forUrl("resources/admin/page/scripts/settings.js"));
        StringBuilder script = new StringBuilder();
        script.append("jQuery(document).ready(function () {\n")
                .append("    SettingUI.init(); // init settings features\n")
                .append("});");
        response.render(OnDomReadyHeaderItem.forScript(script));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(rows = new RepeatingView("rows"));
        add(portlets = new RepeatingView("portlets"));

        if (processDefinitionCode == null || flowMetadataFacade.hasAccessToProcessDefinition(processDefinitionCode, getUserId(), AccessLevel.LIST)) {
            Set<String> processCodeWithAccess = flowMetadataFacade.listProcessDefinitionKeysWithAccess(getUserId(), AccessLevel.LIST);
            if (!processCodeWithAccess.isEmpty()) {
                addStatusesPanel(processCodeWithAccess);
                populateConfigs();
                buildDashboard(processCodeWithAccess);
            }
        } else {
            error(getString("error.user.without.access.to.process"));
        }

    }

    protected void buildDashboard(Set<String> processCodeWithAccess) {
        configs.forEach(c -> portlets.add(new PortletView<>(portlets.newChildId(), c, processDefinitionCode)));
        final FeedPanel feed = new FeedPanel("feed", processDefinitionCode, processCodeWithAccess);
        feed.add($b.classAppender(PortletSize.LARGE.getBootstrapSize()));
        portlets.add(feed);
    }

    protected DashboardRow addDashboardRow() {
        DashboardRow dashboardRow = new DashboardRow(rows.newChildId());
        rows.add(dashboardRow);
        return dashboardRow;
    }

    private void addStatusesPanel(Set<String> processCodeWithAccess) {
        IStatusDTO statusDTO = uiAdminFacade.retrieveActiveInstanceStatus(processDefinitionCode, processCodeWithAccess);

        DashboardRow row = addDashboardRow();
        row.addSmallColumn(new StatusPanel("active-instances-status-panel", "label.active.instances.status", statusDTO.getAmount())
                .setIcon(Icone.SPEEDOMETER).setColor(Color.GREEN_SHARP));
        row.addSmallColumn(new StatusPanel("active-average-status-panel", "label.active.average.status",
                statusDTO.getAverageTimeInDays())
                .setUnit(getString("label.active.average.status.unit"))
                .setIcon(Icone.HOURGLASS).setColor(Color.PURPLE_PLUM));
        row.addSmallColumn(new StatusPanel("opened-instances-status-panel", "label.opened.instances.status",
                statusDTO.getOpenedInstancesLast30Days()));
        row.addSmallColumn(new StatusPanel("finished-instances-status-panel", "label.finished.instances.status",
                statusDTO.getFinishedInstancesLast30Days()).setColor(Color.RED_SUNGLO));
    }

    public void populateConfigs() {
        if (processDefinitionCode == null) {
            configs.add(buildPortletConfigMeanTimeByProcess());
        }

        configs.add(buildPortletConfigNewInstancesQuantityLastYear());
        configs.add(buildPortletConfigCounterActiveInstances());

        if (processDefinitionCode != null) {
            configs.add(buildPortletConfigMeanTimeActiveInstances());
            configs.add(buildPortletConfigStatsByActiveTask());
            configs.add(buildPortletConfigMeanTimeByTask());
            configs.add(buildPortletConfigMeanTimeFinishedInstances());
            configs.add(buildPortletConfigEndStatusQuantityByPeriod());
            configs.add(buildPortletConfigAverageTimesActiveInstances());
            configs.add(buildPortletConfigStatsTimeByActiveTask());
        }
    }

    public PortletConfig<?> buildPortletConfigMeanTimeByProcess() {

        final List<AmChartValueField> valueFields = new ArrayList<>();
        valueFields.add(new AmChartValueField("MEAN", "", "dia(s)"));

        final SingularChart chart = new ColumnSerialChart(valueFields, "NOME");
        final AmChartPortletConfig config = new AmChartPortletConfig("/rest/meanTimeByProcess", chart);

        addPeriodQuickFilter(config.getQuickFilter());

        config.setPortletSize(PortletSize.LARGE);
        config.setTitle(getString("label.chart.mean.time.process.title"));
        config.setSubtitle(getString("label.chart.mean.time.process.subtitle"));

        return config;
    }

    public PortletConfig<?> buildPortletConfigMeanTimeActiveInstances() {

        final List<AmChartValueField> valueFields = new ArrayList<>();
        valueFields.add(new AmChartValueField("TEMPO", ""));

        final SingularChart chart = new LineSerialChart(valueFields, "MES");

        return new AmChartPortletConfig("/rest/meanTimeActiveInstances", chart)
                .setPortletSize(PortletSize.MEDIUM)
                .setTitle(getString("label.chart.active.instances.mean.time.title"))
                .setSubtitle(getString("label.chart.active.instances.mean.time.subtitle"));
    }

    public PortletConfig<?> buildPortletConfigNewInstancesQuantityLastYear() {

        final List<AmChartValueField> valueFields = new ArrayList<>();
        valueFields.add(new AmChartValueField("QTD_NEW", getString("label.chart.new.instance.quantity.new")));
        valueFields.add(new AmChartValueField("QTD_CLS", getString("label.chart.new.instance.quantity.finished")));

        final SingularChart chart = new LineSerialChart(valueFields, "MES");

        return new AmChartPortletConfig("/rest/newInstancesQuantityLastYear", chart)
                .setPortletSize(PortletSize.MEDIUM)
                .setTitle(getString("label.chart.new.instance.quantity.title"))
                .setSubtitle(getString("label.chart.new.instance.quantity.title"));
    }

    public PortletConfig<?> buildPortletConfigCounterActiveInstances() {

        final List<AmChartValueField> valueFields = new ArrayList<>();
        valueFields.add(new AmChartValueField("QUANTIDADE", ""));

        final SingularChart chart = new LineSerialChart(valueFields, "MES");

        return new AmChartPortletConfig("/rest/counterActiveInstances", chart)
                .setPortletSize(PortletSize.MEDIUM)
                .setTitle(getString("label.chart.active.instance.quantity.title"))
                .setSubtitle(getString("label.chart.active.instance.quantity.subtitle"));
    }

    public PortletConfig<?> buildPortletConfigStatsByActiveTask() {

        final SingularChart chart = new PieChart("QUANTIDADE", "NOME");

        return new AmChartPortletConfig("/rest/statsByActiveTask", chart)
                .setPortletSize(PortletSize.MEDIUM)
                .setTitle(getString("label.chart.count.task.title"))
                .setSubtitle(getString("label.chart.count.task.subtitle"));
    }

    public PortletConfig<?> buildPortletConfigMeanTimeByTask() {

        final SingularChart chart = new PieChart("MEAN", "NOME");
        final AmChartPortletConfig config = new AmChartPortletConfig("/rest/meanTimeByTask", chart);

        addPeriodQuickFilter(config.getQuickFilter());

        return config.setPortletSize(PortletSize.LARGE)
                .setTitle(getString("label.chart.mean.time.task.title"))
                .setSubtitle(getString("label.chart.mean.time.task.subtitle"));
    }

    private void addPeriodQuickFilter(List<PortletQuickFilter> list) {
        list.add(new PortletQuickFilter("1 Semana", String.valueOf(PeriodType.WEEKLY)));
        list.add(new PortletQuickFilter("1 Mês", String.valueOf(PeriodType.MONTHLY)));
        list.add(new PortletQuickFilter("1 Ano", String.valueOf(PeriodType.YEARLY)));
    }

    private PortletConfig<?> buildPortletConfigMeanTimeFinishedInstances() {
        final List<AmChartValueField> valueFields = new ArrayList<>();
        valueFields.add(new AmChartValueField("TEMPO", ""));

        final SingularChart chart = new LineSerialChart(valueFields, "MES");

        return new AmChartPortletConfig("/rest/meanTimeFinishedInstances", chart)
                .setPortletSize(PortletSize.MEDIUM)
                .setTitle(getString("label.chart.finished.instances.mean.time.title"))
                .setSubtitle(getString("label.chart.finished.instances.mean.time.subtitle"));
    }

    private PortletConfig<?> buildPortletConfigEndStatusQuantityByPeriod() {
        final SingularChart chart = new DonutPieChart("QUANTIDADE", "SITUACAO");
        final AmChartPortletConfig config = new AmChartPortletConfig("/rest/endStatusQuantityByPeriod", chart);

        addPeriodQuickFilter(config.getQuickFilter());

        return config.setPortletSize(PortletSize.MEDIUM)
                .setTitle(getString("label.chart.status.hour.quantity.title"))
                .setSubtitle(getString("label.chart.status.hour.quantity.subtitle"));
    }

    private PortletConfig<?> buildPortletConfigStatsTimeByActiveTask() {

        final SingularChart chart = new PieChart("TEMPO", "NOME");

        return new AmChartPortletConfig("/rest/statsByActiveTask", chart)
                .setPortletSize(PortletSize.MEDIUM)
                .setTitle(getString("label.chart.active.task.mean.time.title"))
                .setSubtitle(getString("label.chart.active.task.mean.time.subtitle"));
    }

    private PortletConfig<?> buildPortletConfigAverageTimesActiveInstances() {

        final SingularChart chart = new AreaChart("DATA", "TEMPO", "TEMPO2").
                labels(getString("label.chart.active.instances.average.time.3"), getString("label.chart.active.instances.average.time.6"));

        return new MorrisChartPortletConfig("/rest/averageTimesActiveInstances", chart)
                .setPortletSize(PortletSize.MEDIUM)
                .setTitle(getString("label.chart.active.instances.average.time.title"))
                .setSubtitle(getString("label.chart.active.instances.average.time.subtitle"));
    }

}

