package br.net.mirante.singular.showcase.view.page.prototype;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.wicket.model.MInstanceRootModel;
import br.net.mirante.singular.showcase.view.template.Content;
import br.net.mirante.singular.showcase.view.template.Template;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;

import java.util.List;

/**
 * Created by nuk on 04/03/16.
 */
public class PreviewPage extends Template {

    private MInstanceRootModel<SIComposite> model;

    public PreviewPage(MInstanceRootModel<SIComposite>  model){
        this.model = model;
    }

    @Override
    protected Content getContent(String id) {
        return new PreviewContent(id,model);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(OnDomReadyHeaderItem.forScript("$('#_menuItemPrototype').addClass('active');"));
    }
}
