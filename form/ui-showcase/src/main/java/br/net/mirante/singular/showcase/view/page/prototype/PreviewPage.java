package br.net.mirante.singular.showcase.view.page.prototype;

import br.net.mirante.singular.showcase.view.template.Content;
import br.net.mirante.singular.showcase.view.template.Template;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;

import java.util.List;

/**
 * Created by nuk on 04/03/16.
 */
public class PreviewPage extends Template {

    private List<PrototypeContent.Field> fields;

    public PreviewPage(List<PrototypeContent.Field> fields){
        this.fields = fields;
    }

    @Override
    protected Content getContent(String id) {
        return new PreviewContent(id, fields);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(OnDomReadyHeaderItem.forScript("$('#_menuItemPrototype').addClass('active');"));
    }
}
