package br.net.mirante.singular.view.page.dashboard;

import javax.inject.Inject;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.util.string.StringValue;
import org.wicketstuff.annotation.mount.MountPath;

import br.net.mirante.singular.service.UIAdminFacade;
import br.net.mirante.singular.view.template.Content;
import br.net.mirante.singular.view.template.Template;

@MountPath("dashboard")
public class DashboardPage extends Template {

    public static final String PROCESS_DEFINITION_COD_PARAM = "pdCod";

    @Inject
    private UIAdminFacade uiAdminFacade;

    @Override
    protected Content getContent(String id) {
        StringValue processDefinitionCode = getPageParameters().get(PROCESS_DEFINITION_COD_PARAM);
        return new DashboardContent(id, processDefinitionCode.toString());
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        StringValue processDefinitionCode = getPageParameters().get(PROCESS_DEFINITION_COD_PARAM);
        if (processDefinitionCode.isNull()) {
            response.render(OnDomReadyHeaderItem.forScript("$('#_menuItemHome').addClass('active');"));
        } else {
            Pair<Long, Long> ids = uiAdminFacade.retrieveCategoryDefinitionIdsByCode(processDefinitionCode.toString());
            StringBuilder script = new StringBuilder();
            String menuId = String.format("_categoryMenu_%d", ids.getLeft());
            String itemId = String.format("_definitionMenu_%d", ids.getRight());
            script.append("$('#").append(menuId).append("').addClass('open');")
                    .append("$('#").append(menuId).append(">a>span.arrow').addClass('open');")
                    .append("$('#").append(menuId).append(">ul').show();")
                    .append("$('#").append(itemId).append("').addClass('active');");
            response.render(OnDomReadyHeaderItem.forScript(script));
        }
    }
}
