package org.opensingular.singular.showcase.spring.studio;

import org.opensingular.singular.form.SType;
import org.opensingular.singular.form.document.SDocumentFactory;
import org.opensingular.singular.form.document.TypeLoader;
import org.opensingular.singular.showcase.dao.form.studio.ShowcaseStudioTypeLoader;
import org.opensingular.singular.showcase.view.page.form.crud.services.ShowcaseDocumentFactory;
import org.opensingular.singular.studio.core.CollectionGallery;
import org.opensingular.singular.studio.persistence.StudioCollectionPersistenceFactory;
import org.opensingular.singular.studio.spring.StudioCollectionToolboxBean;
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
