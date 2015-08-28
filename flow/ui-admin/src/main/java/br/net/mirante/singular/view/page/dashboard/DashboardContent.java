package br.net.mirante.singular.view.page.dashboard;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.ListModel;

import br.net.mirante.singular.dao.FeedDTO;
import br.net.mirante.singular.service.FeedService;
import br.net.mirante.singular.view.template.Content;

import static br.net.mirante.singular.view.Behaviors.$b;
import static br.net.mirante.singular.view.Models.$m;

@SuppressWarnings("serial")
public class DashboardContent extends Content {


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
                "label.chart.mean.time.process.subtitle", "MEAN", "NOME", " dia(s)"));
    }

}
