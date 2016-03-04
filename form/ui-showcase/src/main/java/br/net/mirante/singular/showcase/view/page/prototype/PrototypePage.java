package br.net.mirante.singular.showcase.view.page.prototype;

import br.net.mirante.singular.showcase.view.page.form.crud.FormContent;
import br.net.mirante.singular.showcase.view.template.Content;
import br.net.mirante.singular.showcase.view.template.Template;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.util.string.StringValue;
import org.wicketstuff.annotation.mount.MountPath;

/**
 * Created by nuk on 03/03/16.
 */
@MountPath("prototype/edit")
public class PrototypePage extends Template {
    @Override
    protected Content getContent(String id) {
        return new PrototypeContent(id);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(OnDomReadyHeaderItem.forScript("$('#_menuItemPrototype').addClass('active');"));
    }
}
