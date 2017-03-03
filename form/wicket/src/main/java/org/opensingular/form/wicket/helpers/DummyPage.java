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

package org.opensingular.form.wicket.helpers;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.opensingular.form.*;
import org.opensingular.form.context.SFormConfig;
import org.opensingular.form.document.*;
import org.opensingular.form.wicket.SingularFormContextWicket;
import org.opensingular.form.wicket.UIBuilderWicket;
import org.opensingular.form.wicket.component.SingularFormWicket;
import org.opensingular.form.wicket.component.SingularValidationButton;
import org.opensingular.form.wicket.enums.AnnotationMode;
import org.opensingular.form.wicket.enums.ViewMode;
import org.opensingular.form.wicket.panel.SingularFormPanel;
import org.opensingular.lib.commons.lambda.IConsumer;
import org.opensingular.lib.commons.lambda.IFunction;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Classe utilitária de teste para dar suporte ao {@link SingularFormBaseTest}
 * Não deve ser referenciada fora do código de teste.
 */
public class DummyPage extends WebPage {

    final public transient SFormConfig<String> mockFormConfig = new MockFormConfig();

    protected ViewMode viewMode = ViewMode.EDIT;

    protected AnnotationMode annotationMode = AnnotationMode.NONE;

    protected transient SIComposite currentInstance;

    protected IConsumer<STypeComposite> typeBuilder;

    protected IFunction<RefType, SIComposite> instanceCreator;

    private final List<IConsumer<SIComposite>> instancePopulators = new ArrayList<>();

    private SingularFormWicket<?> form = new SingularFormWicket<>("form");

    private SingularFormPanel<String> singularFormPanel        = new SingularFormPanel<String>("singularFormPanel", mockFormConfig) {
        @Override
        protected SInstance createInstance(SFormConfig<String> singularFormConfig) {
            Optional<RefType> baseType = mockFormConfig.getTypeLoader().loadRefType("mockType");
            if (baseType.isPresent()) {
                RefType refType = baseType.get();
                if (refType.get().isComposite()) {
                    typeBuilder.accept((STypeComposite) refType.get());
                }
                if (instanceCreator != null) {
                    currentInstance = instanceCreator.apply(refType);
                } else{
                    SDocumentFactory factory = mockFormConfig.getDocumentFactory();
                    currentInstance = (SIComposite) factory.createInstance(refType);
                }
                instancePopulators.forEach(populator -> populator.accept(currentInstance));
            }
            return currentInstance;
        }

        @Override
        public ViewMode getViewMode() {
            return viewMode;
        }

        @Override
        public AnnotationMode getAnnotationMode() {
            return annotationMode;
        }
    };
    private SingularValidationButton  singularValidationButton = new SingularValidationButton("validate-btn", singularFormPanel.getRootInstance()) {
        @Override
        protected void onValidationSuccess(AjaxRequestTarget target, Form<?> form, IModel<? extends SInstance> instanceModel) {
        }
    };

    public DummyPage() {
        add(form.add(singularFormPanel, singularValidationButton));
    }

    public Form<?> getForm() {
        return form;
    }

    public SingularFormPanel<String> getSingularFormPanel() {
        return singularFormPanel;
    }

    public SingularValidationButton getSingularValidationButton() {
        return singularValidationButton;
    }

    public void setAsVisualizationView() {
        viewMode = ViewMode.READ_ONLY;
    }

    public void setAsEditView() {
        viewMode = ViewMode.EDIT;
    }

    public void enableAnnotation() {
        annotationMode = AnnotationMode.EDIT;
    }

    public SIComposite getCurrentInstance() {
        return currentInstance;
    }

    public void setInstanceCreator(IFunction<RefType, SIComposite> instanceCreator) {
        this.instanceCreator = instanceCreator;
    }

    final IFunction<RefType, SIComposite> getInstanceCreator() {
        return instanceCreator;
    }

    public void setTypeBuilder(IConsumer<STypeComposite> typeBuilder) {
        this.typeBuilder = typeBuilder;
    }

    final IConsumer<STypeComposite> getTypeBuilder() {
        return typeBuilder;
    }

    public final void addInstancePopulator(IConsumer<SIComposite> populator) {
        instancePopulators.add(populator);
    }
}

class MockSDocumentFactory extends SDocumentFactory implements Serializable {

    private final transient DefaultServiceRegistry defaultServiceRegistry = new DefaultServiceRegistry();

    private final transient SingularFormContextWicket singularFormContextWicket = new Context();

    {
        defaultServiceRegistry.bindLocalService(SingularFormContextWicket.class, () -> singularFormContextWicket);
    }

    @Override
    public RefSDocumentFactory getDocumentFactoryRef() {
        final MockSDocumentFactory documentFactory = this;
        return new RefSDocumentFactory() {
            @Override
            protected SDocumentFactory retrieve() {
                return documentFactory;
            }
        };
    }

    @Override
    public ServiceRegistry getServiceRegistry() {
        return defaultServiceRegistry;
    }

    @Override
    protected void setupDocument(SDocument document) {
    }

    private class Context implements SingularFormContextWicket, Serializable {
        @Override
        public UIBuilderWicket getUIBuilder() {
            return new UIBuilderWicket();
        }

        @Override
        public ServiceRegistry getServiceRegistry() {
            return defaultServiceRegistry;
        }
    }
}

class MockTypeLoader extends TypeLoader<String> implements Serializable {

    transient private final SDictionary dictionary;

    {
        dictionary = SDictionary.create();
    }

    @Override
    protected Optional<RefType> loadRefTypeImpl(String typeId) {
        return Optional.of(RefType.of(() -> loadTypeImpl2(typeId)));
    }

    @Override
    protected Optional<SType<?>> loadTypeImpl(String typeId) {
        return Optional.of(loadTypeImpl2(typeId));
    }

    @Nonnull
    private SType<?> loadTypeImpl2(String typeId) {
        SType<?> typeOptional = dictionary.getTypeOptional(typeId);
        if (typeOptional != null) {
            return typeOptional;
        }
        return dictionary.createNewPackage(typeId).createCompositeType("mockRoot");
    }
}