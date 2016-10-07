package org.opensingular.singular.form.showcase.spring.studio;

import org.opensingular.form.SType;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.form.document.TypeLoader;
import org.opensingular.singular.form.showcase.dao.form.studio.ShowcaseStudioTypeLoader;
import org.opensingular.singular.form.showcase.view.page.form.crud.services.ShowcaseDocumentFactory;
//import com.opensingular.studio.core.CollectionGallery;
//import com.opensingular.studio.persistence.StudioCollectionPersistenceFactory;
//import com.opensingular.studio.spring.StudioCollectionToolboxBean;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
//
//@Component
//public class ShowcaseStudioCollectionToolboxBean extends StudioCollectionToolboxBean {
//
//    @Inject
//    private StudioCollectionPersistenceFactory factory;
//
//    @Inject
//    private CollectionGallery gallery;
//
//    @Inject
//    private ShowcaseDocumentFactory showcaseDocumentFactory;
//
//    @Inject
//    private ShowcaseStudioTypeLoader typeLoader;
//
//    @Override
//    public StudioCollectionPersistenceFactory getPersistenceFactory() {
//        return factory;
//    }
//
//    @Override
//    public CollectionGallery getCollectionGallery() {
//        return gallery;
//    }
//
//    @Override
//    public SDocumentFactory getDocumentFactory() {
//        return showcaseDocumentFactory;
//    }
//
//    @Override
//    public TypeLoader<Class<SType<?>>> getTypeLoader() {
//        return typeLoader;
//    }
//}
