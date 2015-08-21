package br.net.mirante.singular.view.page.dashboard;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;

import br.net.mirante.singular.view.template.Content;
import br.net.mirante.singular.view.template.Template;

public class DashboardPage extends Template {

    @Override
    protected Content getContent(String id) {
        return new DashboardContent(id);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(OnDomReadyHeaderItem.forScript("$('#_menuItemHome').addClass('active');"));
    }
}
