package br.net.mirante.singular.view.page.dashboard;

import br.net.mirante.singular.bamclient.builder.ChartDataProvider;
import br.net.mirante.singular.bamclient.chart.AbstractSerialChart;
import br.net.mirante.singular.bamclient.chart.ChartValueField;
import br.net.mirante.singular.bamclient.chart.ColumnSerialChart;
import br.net.mirante.singular.bamclient.chart.LineSerialChart;
import br.net.mirante.singular.bamclient.portlet.AmChartPortletConfig;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.$m;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import br.net.mirante.singular.flow.core.authorization.AccessLevel;
import br.net.mirante.singular.flow.core.dto.IStatusDTO;
import br.net.mirante.singular.util.wicket.resource.Color;
import br.net.mirante.singular.util.wicket.resource.Icone;
import br.net.mirante.singular.view.component.PortletView;
import br.net.mirante.singular.view.page.processo.MetadadosPage;
import br.net.mirante.singular.view.page.processo.ProcessosPage;
import br.net.mirante.singular.view.template.Content;

@SuppressWarnings("serial")
public class DashboardContent extends Content {

    private String processDefinitionCode;

    private RepeatingView rows;

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
        if (processDefinitionCode == null || flowMetadataFacade.hasAccessToProcessDefinition(processDefinitionCode, getUserId(), AccessLevel.LIST)) {
            Set<String> processCodeWithAccess = flowMetadataFacade.listProcessDefinitionKeysWithAccess(getUserId(), AccessLevel.LIST);
            if (!processCodeWithAccess.isEmpty()) {
                addStatusesPanel(processCodeWithAccess);
                addWelcomeChart(processCodeWithAccess);
                addDefaultCharts(processCodeWithAccess);

                addSpecificCharts(processCodeWithAccess);
            }
        } else {
            error(getString("error.user.without.access.to.process"));
        }
    }

    protected DashboardRow addDashboardRow() {
        DashboardRow dashboardRow = new DashboardRow(rows.newChildId());
        rows.add(dashboardRow);
        return dashboardRow;
    }

    protected DashboardRow getLastRow() {
        return (DashboardRow) rows.get(String.valueOf(rows.size() - 1));
    }

    private void addDefaultCharts(Set<String> processCodeWithAccess) {
        DashboardRow row = addDashboardRow();


        final List<Map<String, String>> graphData =  uiAdminFacade.retrieveNewInstancesQuantityLastYear(processDefinitionCode, processCodeWithAccess);
        final ChartDataProvider provider = new ChartDataProvider().addAll(graphData);
        final ChartValueField novas  = new ChartValueField("QTD_NEW", "Novas", "");
        final ChartValueField concluidas  = new ChartValueField("QTD_CLS", "Concluidas", "");

        final List<ChartValueField> values = Arrays.asList(novas, concluidas);

        final LineSerialChart serialChart = new LineSerialChart(provider, values, "MES");
        final AmChartPortletConfig amChartPortletConfig = new AmChartPortletConfig();

        serialChart.withLegend();
        amChartPortletConfig.setDefinition(serialChart.getDefinition());

        row.addMediumColumn(new PortletView<>("mySerialChart2", amChartPortletConfig));

        row.addMediumColumn(new SerialChartPanel("new-instances-quantity-chart", "label.chart.new.instance.quantity.title",
                "label.chart.new.instance.quantity.title", ImmutablePair.of("QTD_NEW", getString("label.chart.new.instance.quantity.new")),
                "MES", "smoothedLine") {
            @Override
            protected List<Map<String, String>> retrieveData(PeriodType periodType) {
                return uiAdminFacade.retrieveNewInstancesQuantityLastYear(processDefinitionCode, processCodeWithAccess);
            }
        }.addGraph("QTD_CLS", getString("label.chart.new.instance.quantity.finished")).addLegend());

        row.addMediumColumn(new SerialChartPanel("active-instances-quantity-chart", "label.chart.active.instance.quantity.title",
                "label.chart.active.instance.quantity.subtitle", "QUANTIDADE", "MES", "smoothedLine") {
            @Override
            protected List<Map<String, String>> retrieveData(PeriodType periodType) {
                return uiAdminFacade.retrieveCounterActiveInstances(processDefinitionCode, processCodeWithAccess);
            }
        });
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

    private void addWelcomeChart(Set<String> processCodeWithAccess) {


        final List<Map<String, String>> graphData = uiAdminFacade.retrieveMeanTimeByProcess(PeriodType.YEARLY.getPeriod(), null, processCodeWithAccess);

        final ChartDataProvider provider = new ChartDataProvider().addAll(graphData);
        final ColumnSerialChart serialChart = new ColumnSerialChart(provider,
                Collections.singletonList(new ChartValueField("MEAN", "", "dia(s)")), "NOME");

        final AmChartPortletConfig amChartPortletConfig = new AmChartPortletConfig();
        amChartPortletConfig.setDefinition(serialChart.getDefinition());

        addDashboardRow().addMediumColumn(new PortletView<>("mySerialChart", amChartPortletConfig));

        if (processDefinitionCode == null) {
            addDashboardRow().addMediumColumn(new SerialChartPanel("instances-mean-time-chart", "label.chart.mean.time.process.title",
                    "label.chart.mean.time.process.subtitle", "MEAN", "NOME", " dia(s)", true) {
                @Override
                protected List<Map<String, String>> retrieveData(PeriodType periodType) {
                    return graphData;
                }
            });
        } else {
            DashboardRow row = addDashboardRow();
            row.addMediumColumn(new SerialChartPanel("active-instances-mean-time-chart",
                    "label.chart.active.instances.mean.time.title", "label.chart.active.instances.mean.time.subtitle",
                    "TEMPO", "MES", "smoothedLine") {
                @Override
                protected List<Map<String, String>> retrieveData(PeriodType periodType) {
                    return uiAdminFacade.retrieveMeanTimeActiveInstances(processDefinitionCode, processCodeWithAccess);
                }
            });
            row.addMediumColumn(new PieChartPanel("task-count-chart", "label.chart.count.task.title",
                    "label.chart.count.task.subtitle", null, "QUANTIDADE", "NOME", false, false) {
                @Override
                protected List<Map<String, String>> retrieveData(PeriodType periodType) {
                    return uiAdminFacade.retrieveStatsByActiveTask(processDefinitionCode);
                }
            });
        }
    }

    private void addSpecificCharts(Set<String> processCodeWithAccess) {
        if (processDefinitionCode != null) {
            DashboardRow row = addDashboardRow();
            row.addMediumColumn(new PieChartPanel("task-mean-time-chart", "label.chart.mean.time.task.title",
                    "label.chart.mean.time.task.subtitle", null, "MEAN", "NOME", true, false) {
                @Override
                protected List<Map<String, String>> retrieveData(PeriodType periodType) {
                    return uiAdminFacade.retrieveMeanTimeByTask(periodType.getPeriod(), processDefinitionCode);
                }
            });
            row.addMediumColumn(new FeedPanel("feed", processDefinitionCode, processCodeWithAccess));
            row = addDashboardRow();
            row.addMediumColumn(new SerialChartPanel("finished-instances-mean-time-chart",
                    "label.chart.finished.instances.mean.time.title",
                    "label.chart.finished.instances.mean.time.subtitle", "TEMPO", "MES", "smoothedLine", null) {
                @Override
                protected List<Map<String, String>> retrieveData(PeriodType periodType) {
                    return uiAdminFacade.retrieveMeanTimeFinishedInstances(processDefinitionCode, processCodeWithAccess);
                }
            });
            row.addMediumColumn(new PieChartPanel("status-hours-quantity-chart", "label.chart.status.hour.quantity.title",
                    "label.chart.status.hour.quantity.subtitle", null, "QUANTIDADE", "SITUACAO", true, true) {
                @Override
                protected List<Map<String, String>> retrieveData(PeriodType periodType) {
                    return uiAdminFacade.retrieveEndStatusQuantityByPeriod(periodType.getPeriod(), processDefinitionCode);
                }
            });
            row = addDashboardRow();
            row.addMediumColumn(new AreaChartPanel("active-instances-average-time-chart",
                    "label.chart.active.instances.average.time.title",
                    "label.chart.active.instances.average.time.subtitle",
                    ImmutablePair.of("TEMPO", getString("label.chart.active.instances.average.time.3")),
                    "DATA", false, true) {
                @Override
                protected List<Map<String, String>> retrieveData(PeriodType periodType) {
                    return uiAdminFacade.retrieveAverageTimesActiveInstances(processDefinitionCode, processCodeWithAccess);
                }
            }.addGraph("TEMPO2", getString("label.chart.active.instances.average.time.6")));

            row.addMediumColumn(new PieChartPanel("active-task-mean-time-chart",
                    "label.chart.active.task.mean.time.title",
                    "label.chart.active.task.mean.time.subtitle", null, "TEMPO", "NOME", false, false) {
                @Override
                protected List<Map<String, String>> retrieveData(PeriodType periodType) {
                    return uiAdminFacade.retrieveStatsByActiveTask(processDefinitionCode);
                }
            });
        } else {
            addDashboardRow().addMediumColumn(new FeedPanel("feed", processDefinitionCode, processCodeWithAccess));
        }
    }
}
