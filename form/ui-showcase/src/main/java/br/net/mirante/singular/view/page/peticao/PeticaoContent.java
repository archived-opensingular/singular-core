package br.net.mirante.singular.view.page.peticao;

import br.net.mirante.singular.view.SingularWicketContainer;
import br.net.mirante.singular.view.template.Content;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

public class PeticaoContent extends Content implements SingularWicketContainer<PeticaoContent, Void> {

    public PeticaoContent(String id) {
        super(id);
    }

    @Override
    protected IModel<?> getContentTitlelModel() {
        return new ResourceModel("label.content.title");
    }

    @Override
    protected IModel<?> getContentSubtitlelModel() {
        return new ResourceModel("label.content.subtitle");
    }
}
