package br.net.mirante.singular.view.page.dashboard;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.util.string.StringValue;
import org.wicketstuff.annotation.mount.MountPath;

import br.net.mirante.singular.view.template.Content;
import br.net.mirante.singular.view.template.Template;

@MountPath("dashboard")
public class DashboardPage extends Template {

    public static final String PROCESS_DEFINITION_COD_PARAM = "pdCod";

    @Override
    protected Content getContent(String id) {
        StringValue processDefinitionCode = getPageParameters().get(PROCESS_DEFINITION_COD_PARAM);
        return new DashboardContent(id, processDefinitionCode.toString());
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(OnDomReadyHeaderItem.forScript("$('#_menuItemHome').addClass('active');"));
    }
}
