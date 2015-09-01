package br.net.mirante.singular.view.page.processo;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.util.string.StringValue;
import org.wicketstuff.annotation.mount.MountPath;

import br.net.mirante.singular.view.template.Content;
import br.net.mirante.singular.view.template.Template;

@MountPath("processos")
public class ProcessosPage extends Template {

    public static final String PROCESS_DEFINITION_ID_PARAM = "_PD_ID";

    @Override
    protected Content getContent(String id) {
        StringValue processDefinitionId = getPageParameters().get(PROCESS_DEFINITION_ID_PARAM);
        if (processDefinitionId.isNull()) {
            return new ProcessosContent(id, withSideBar());
        } else {
            return new InstanciasContent(id, withSideBar(), processDefinitionId.toLong());
        }
    }

    @Override
    protected boolean withSideBar() {
        return false;
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        StringBuilder script = new StringBuilder();
        script.append("$('#_menuSubFlow').addClass('open');")
                .append("$('#_menuSubFlow').addClass('open');")
                .append("$('#_menuSubFlow>a>span.arrow').addClass('open');")
                .append("$('#_menuSubFlow>ul').show();")
                .append("$('#_menuItemFlowProcess').addClass('active');");
        response.render(OnDomReadyHeaderItem.forScript(script));
    }
}
