package br.net.mirante.singular.view.page.dashboard;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;

import br.net.mirante.singular.dao.StatusDTO;
import br.net.mirante.singular.service.PesquisaService;
import br.net.mirante.singular.util.wicket.resource.Color;
import br.net.mirante.singular.util.wicket.resource.Icone;
import br.net.mirante.singular.view.page.processo.ProcessosPage;
import br.net.mirante.singular.view.template.Content;
import br.net.mirante.singular.wicket.UIAdminWicketFilterContext;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.$m;

@SuppressWarnings("serial")
public class DashboardContent extends Content {

    @Inject
    private UIAdminWicketFilterContext uiAdminWicketFilterContext;

    @Inject
    private PesquisaService pesquisaService;

    private String processDefinitionCode;

    public DashboardContent(String id, String processDefinitionCode) {
        super(id, false, false, processDefinitionCode != null, false);
        this.processDefinitionCode = processDefinitionCode;
    }

    @Override
    protected IModel<?> getContentTitlelModel() {
        if (processDefinitionCode == null) {
            return new ResourceModel("label.content.title");
        } else {
            return $m.ofValue(pesquisaService.retrieveProcessDefinitionName(processDefinitionCode));
        }
    }

    @Override
    protected IModel<?> getContentSubtitlelModel() {
        return new ResourceModel("label.content.subtitle");
    }

    @Override
    protected WebMarkupContainer getInfoLink(String id) {
        WebMarkupContainer infoLink = new WebMarkupContainer(id);
        infoLink.add($b.attr("data-original-title",
                new StringResourceModel("label.content.info.title", this).getString()));
        infoLink.add($b.attr("href", uiAdminWicketFilterContext.getRelativeContext().concat("process")
                .concat("?").concat(ProcessosPage.PROCESS_DEFINITION_ID_PARAM)
                .concat("=").concat(pesquisaService.retrieveProcessDefinitionId(processDefinitionCode))));
        return infoLink;
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssReferenceHeaderItem.forUrl("resources/admin/page/css/page.css"));
        response.render(JavaScriptReferenceHeaderItem.forUrl("resources/admin/page/scripts/page.js"));
        response.render(JavaScriptReferenceHeaderItem.forUrl("resources/admin/page/scripts/settings.js"));
        StringBuilder script = new StringBuilder();
        script.append("jQuery(document).ready(function () {\n")
                .append("    SettingUI.init(); // init settings features\n")
                .append("    Index.init();\n")
                .append("});");
        response.render(OnDomReadyHeaderItem.forScript(script));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        addStatusesPanel();
        addWelcomeChart();
        addDefaultCharts();
        add(new FeedPanel("feed"));
    }

    private void addDefaultCharts() {
        add(new SerialChartPanel("new-instances-quantity-chart", "label.chart.new.instance.quantity.title",
                "label.chart.new.instance.quantity.subtitle", new ImmutablePair<>("QTD_NEW",
                new StringResourceModel("label.chart.new.instance.quantity.new", this).getString()),
                "MES", "smoothedLine") {
            @Override
            protected List<Map<String, String>> retrieveData(PeriodType periodType) {
                return pesquisaService.retrieveNewInstancesQuantityLastYear(processDefinitionCode);
            }
        }.addGraph("QTD_CLS", new StringResourceModel("label.chart.new.instance.quantity.finished", this).getString()));
        add(new PieChartPanel("status-hours-quantity-chart", "label.chart.status.hour.quantity.title",
                "label.chart.status.hour.quantity.subtitle",
                processDefinitionCode == null
                        ? new StringResourceModel("label.chart.status.hour.quantity.default", this).getString() : null,
                "QUANTIDADE", "SITUACAO", true, true) {
            @Override
            protected List<Map<String, String>> retrieveData(PeriodType periodType) {
                return pesquisaService.retrieveEndStatusQuantityByPeriod(periodType.getPeriod(),
                        processDefinitionCode != null ? processDefinitionCode : "LiberarLancamentoAtv");
            }
        });
        add(new PieChartPanel("task-mean-time-chart", "label.chart.mean.time.task.title",
                "label.chart.mean.time.task.subtitle",
                processDefinitionCode == null
                        ? new StringResourceModel("label.chart.mean.time.task.default", this).getString() : null,
                "MEAN", "NOME", true, false) {
            @Override
            protected List<Map<String, String>> retrieveData(PeriodType periodType) {
                return pesquisaService.retrieveMeanTimeByTask(periodType.getPeriod(),
                        processDefinitionCode != null ? processDefinitionCode : "PrevisaoFluxoCaixa");
            }
        });
    }

    private void addStatusesPanel() {
        StatusDTO statusDTO = pesquisaService.retrieveActiveInstanceStatus(processDefinitionCode);
        add(new StatusPanel("active-instances-status-panel", "label.active.instances.status", statusDTO.getAmount())
                .setIcon(Icone.SPEEDOMETER).setColor(Color.GREEN_SHARP));
        add(new StatusPanel("active-average-status-panel", "label.active.average.status",
                statusDTO.getAverageTimeInDays()).setUnit(
                new StringResourceModel("label.active.average.status.unit", this).getString())
                .setIcon(Icone.HOURGLASS).setColor(Color.PURPLE_PLUM));
        add(new StatusPanel("opened-instances-status-panel", "label.opened.instances.status",
                statusDTO.getOpenedInstancesLast30Days()));
        add(new StatusPanel("finished-instances-status-panel", "label.finished.instances.status",
                statusDTO.getFinishedInstancesLast30Days()).setColor(Color.RED_SUNGLO));
    }

    private void addWelcomeChart() {
        WebMarkupContainer globalContainer = new WebMarkupContainer("welcomeChartGlobal");
        WebMarkupContainer localContainer = new WebMarkupContainer("welcomeChartLocal");
        if (processDefinitionCode == null) {
            globalContainer.add(new SerialChartPanel("instances-mean-time-chart", "label.chart.mean.time.process.title",
                    "label.chart.mean.time.process.subtitle", "MEAN", "NOME", " dia(s)", true) {
                @Override
                protected List<Map<String, String>> retrieveData(PeriodType periodType) {
                    return pesquisaService.retrieveMeanTimeByProcess(periodType.getPeriod());
                }
            });
            localContainer.add($b.visibleIf($m.ofValue(false)));
        } else {
            localContainer.add(new SerialChartPanel("active-instances-mean-time-chart",
                    "label.chart.active.instances.mean.time.title", "label.chart.active.instances.mean.time.subtitle",
                    "TEMPO", "MES", "smoothedLine") {
                @Override
                protected List<Map<String, String>> retrieveData(PeriodType periodType) {
                    return pesquisaService.retrieveMeanTimeActiveInstances(processDefinitionCode);
                }
            });
            localContainer.add(new SerialChartPanel("finished-instances-mean-time-chart",
                    "label.chart.finished.instances.mean.time.title",
                    "label.chart.finished.instances.mean.time.subtitle", "TEMPO", "MES", "smoothedLine") {
                @Override
                protected List<Map<String, String>> retrieveData(PeriodType periodType) {
                    return pesquisaService.retrieveMeanTimeFinishedInstances(processDefinitionCode);
                }
            });
            globalContainer.add($b.visibleIf($m.ofValue(false)));
        }
        add(globalContainer);
        add(localContainer);
    }
}
