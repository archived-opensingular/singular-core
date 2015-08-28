package br.net.mirante.singular.view.page.dashboard;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;

import br.net.mirante.singular.service.PesquisaService;
import br.net.mirante.singular.view.template.Content;

@SuppressWarnings("serial")
public class DashboardContent extends Content {

    @Inject
    private PesquisaService pesquisaService;

    public DashboardContent(String id) {
        super(id);
    }

    @Override
    protected String getContentTitlelKey() {
        return "label.content.title";
    }

    @Override
    protected String getContentSubtitlelKey() {
        return "label.content.subtitle";
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssReferenceHeaderItem.forUrl("/resources/admin/page/css/page.css"));
        response.render(JavaScriptReferenceHeaderItem.forUrl("/resources/admin/page/scripts/demo.js"));
        response.render(JavaScriptReferenceHeaderItem.forUrl("/resources/admin/page/scripts/page.js"));
        StringBuilder script = new StringBuilder();
        script.append("jQuery(document).ready(function () {\n")
                .append("    Demo.init(); // init demo features\n")
                .append("    Index.init();\n")
                .append("});");
        response.render(OnDomReadyHeaderItem.forScript(script));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new FeedPanel("feed"));
        add(new BarChartPanel("process-mean-time-chart", "label.chart.mean.time.process.title",
                "label.chart.mean.time.process.subtitle", "MEAN", "NOME", " dia(s)", true) {
            @Override
            protected List<Map<String, String>> retrieveData(PeriodType periodType) {
                return pesquisaService.retrieveMeanTimeByProcess(periodType.getPeriod());
            }
        });
        add(new BarChartPanel("new-instances-quantity-chart", "label.chart.new.instance.quantity.title",
                "label.chart.new.instance.quantity.subtitle", "QUANTIDADE", "MES") {
            @Override
            protected List<Map<String, String>> retrieveData(PeriodType periodType) {
                return pesquisaService.retrieveNewInstancesQuantityLastYear();
            }
        });
        add(new BarChartPanel("status-hours-quantity-chart", "label.chart.status.hour.quantity.title",
                "label.chart.status.hour.quantity.subtitle", "QUANTIDADE", "SITUACAO", true) {
            @Override
            protected List<Map<String, String>> retrieveData(PeriodType periodType) {
                return pesquisaService.retrieveStatusQuantityByPeriod(periodType.getPeriod());
            }
        });
    }
}
