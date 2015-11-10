package br.net.mirante.singular.view.page.form.crud;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.wicketstuff.annotation.mount.MountPath;

import br.net.mirante.singular.view.template.Content;
import br.net.mirante.singular.view.template.Template;

@MountPath("form/crud")
@SuppressWarnings("serial")
public class CrudPage extends Template {

    @Override
    protected Content getContent(String id) {
        return new CrudContent(id);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(OnDomReadyHeaderItem.forScript("$('#_menuItemDemo').addClass('active');"));
    }
}
