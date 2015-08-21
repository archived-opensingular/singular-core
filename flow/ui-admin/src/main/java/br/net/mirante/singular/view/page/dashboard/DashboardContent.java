package br.net.mirante.singular.view.page.dashboard;

import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;

import br.net.mirante.singular.view.template.Content;

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
}
