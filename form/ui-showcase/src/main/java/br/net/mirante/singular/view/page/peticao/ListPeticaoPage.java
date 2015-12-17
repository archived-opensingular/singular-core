package br.net.mirante.singular.view.page.peticao;

import br.net.mirante.singular.view.template.Content;
import br.net.mirante.singular.view.template.Template;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.wicketstuff.annotation.mount.MountPath;

@MountPath("peticao/listar")
public class ListPeticaoPage extends Template {
    @Override
    protected Content getContent(String id) {
        return new ListPeticaoContent(id);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(OnDomReadyHeaderItem.forScript("$('#_menuItemPeticionamento').addClass('active');"));
    }
}
