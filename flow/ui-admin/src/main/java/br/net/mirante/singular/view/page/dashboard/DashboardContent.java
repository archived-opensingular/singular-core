package br.net.mirante.singular.view.page.dashboard;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.spring.injection.annot.SpringBean;

import br.net.mirante.singular.dao.FeedDTO;
import br.net.mirante.singular.service.FeedService;
import br.net.mirante.singular.view.template.Content;

public class DashboardContent extends Content {

    @SpringBean
    FeedService feedService;

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
        add(new BarChartPanel("process-mean-time-chart", "label.chart.title", "label.chart.subtitle"));
        add(new ListView<FeedDTO>("atividades", feedService.retrieveFeed()) {
            @Override
            protected void populateItem(ListItem<FeedDTO> item) {
                final FeedDTO feedDto = item.getModelObject();
                item.add(new Label("descricao", feedDto.getDescricao()));
                item.add(new Label("tempoDeAtraso", feedDto.getTempoAtraso()));

                WebMarkupContainer iconColor = new WebMarkupContainer("feedIconColor");
                iconColor.add(new AttributeAppender("class", feedDto.getFeedIconColor().getDescricao()));
                item.add(iconColor);

            }

        });
    }
}
