package br.net.mirante.singular.view.page.showcase;

import br.net.mirante.singular.view.template.Content;
import br.net.mirante.singular.view.template.Template;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.wicketstuff.annotation.mount.MountPath;

@MountPath("showcase/menu")
@SuppressWarnings("serial")
public class ShowCasePage extends Template {

    private Integer showCaseComponentNameHash;

    public ShowCasePage(PageParameters parameters) {
       this.showCaseComponentNameHash = parameters.get("ch").toInt();
    }


    @Override
    protected Content getContent(String id) {
        return new ShowCaseContent(id, showCaseComponentNameHash);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(OnDomReadyHeaderItem.forScript("$('#_menuItemShowCase').addClass('active');"));
    }
}