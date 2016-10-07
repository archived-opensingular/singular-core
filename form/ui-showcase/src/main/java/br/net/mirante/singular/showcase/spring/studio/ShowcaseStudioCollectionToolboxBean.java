package br.net.mirante.singular.showcase.spring.studio;

import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.document.SDocumentFactory;
import br.net.mirante.singular.form.document.TypeLoader;
import br.net.mirante.singular.showcase.dao.form.studio.ShowcaseStudioTypeLoader;
import br.net.mirante.singular.showcase.view.page.form.crud.services.ShowcaseDocumentFactory;
import br.net.mirante.singular.studio.core.CollectionGallery;
import br.net.mirante.singular.studio.persistence.StudioCollectionPersistenceFactory;
import br.net.mirante.singular.studio.spring.StudioCollectionToolboxBean;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class ShowcaseStudioCollectionToolboxBean extends StudioCollectionToolboxBean {

    @Inject
    private StudioCollectionPersistenceFactory factory;

    @Inject
    private CollectionGallery gallery;

    @Inject
    private ShowcaseDocumentFactory showcaseDocumentFactory;

    @Inject
    private ShowcaseStudioTypeLoader typeLoader;

    @Override
    public StudioCollectionPersistenceFactory getPersistenceFactory() {
        return factory;
    }

    @Override
    public CollectionGallery getCollectionGallery() {
        return gallery;
    }

    @Override
    public SDocumentFactory getDocumentFactory() {
        return showcaseDocumentFactory;
    }

    @Override
    public TypeLoader<Class<SType<?>>> getTypeLoader() {
        return typeLoader;
    }
}
