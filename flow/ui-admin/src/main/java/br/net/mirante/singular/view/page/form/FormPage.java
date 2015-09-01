package br.net.mirante.singular.view.page.form;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.wicketstuff.annotation.mount.MountPath;

import br.net.mirante.singular.view.template.Content;
import br.net.mirante.singular.view.template.Template;

@MountPath("form")
public class FormPage extends Template {

    @Override
    protected Content getContent(String id) {
        return new FormContent(id, withSideBar());
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
