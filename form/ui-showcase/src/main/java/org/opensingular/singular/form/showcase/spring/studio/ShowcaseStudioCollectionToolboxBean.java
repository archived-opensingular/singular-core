/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
