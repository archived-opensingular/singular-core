package br.net.mirante.singular.showcase.view.page.studio;

import br.net.mirante.singular.showcase.view.template.Content;
import br.net.mirante.singular.studio.core.CollectionCanvas;
import br.net.mirante.singular.studio.core.CollectionGallery;
import br.net.mirante.singular.studio.wicket.SingularStudioCollectionPanel;
import org.apache.wicket.model.IModel;

import javax.inject.Inject;

import static br.net.mirante.singular.util.wicket.util.Shortcuts.$m;

public class StudioContentExample extends Content {

    @Inject
    private CollectionGallery collectionGallery;

    public StudioContentExample(String id) {
        super(id);
    }

    private CollectionCanvas mockCanvas() {
        //retornando a primeira encontrara só a critério de exemplo.
        return collectionGallery.getCollectionCanvas().get(0);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new SingularStudioCollectionPanel("content", mockCanvas()));
    }

    @Override
    protected IModel<?> getContentTitleModel() {
        return $m.ofValue(mockCanvas().getCollectionInfo().getTitle());
    }

    @Override
    protected IModel<?> getContentSubtitleModel() {
        return $m.ofValue(mockCanvas().getCollectionInfo().getTitle());
    }
}
