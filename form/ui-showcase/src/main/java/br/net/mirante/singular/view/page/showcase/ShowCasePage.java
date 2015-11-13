package br.net.mirante.singular.view.page.showcase;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.wicketstuff.annotation.mount.MountPath;

import br.net.mirante.singular.view.template.Content;
import br.net.mirante.singular.view.template.Template;

@MountPath("showcase/menu")
@SuppressWarnings("serial")
public class ShowCasePage extends Template {

    @Override
    protected Content getContent(String id) {
        return new ShowCaseContent(id);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(OnDomReadyHeaderItem.forScript("$('#_menuItemShowCase').addClass('active');"));
    }
}