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
import org.opensingular.form.SDictionary;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.context.SFormConfig;
import org.opensingular.form.document.RefSDocumentFactory;
import org.opensingular.form.document.RefType;
import org.opensingular.form.document.SDocument;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.form.document.TypeLoader;
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
    private final List<IConsumer<SIComposite>> instancePopulators = new ArrayList<>();
    private final SingularFormPanel singularFormPanel = new SingularFormPanel("singularFormPanel");
    protected IConsumer<STypeComposite> typeBuilder;
    protected IFunction<RefType, SIComposite> instanceCreator;
    private SingularFormWicket<?> form = new SingularFormWicket<>("form");
    private SingularValidationButton singularValidationButton = new SingularValidationButton("validate-btn", singularFormPanel.getInstanceModel()) {
        @Override
        protected void onValidationSuccess(AjaxRequestTarget target, Form<?> form, IModel<? extends SInstance> instanceModel) {
        }
    };

    public DummyPage() {
        singularFormPanel.setAnnotationMode(AnnotationMode.NONE);
        singularFormPanel.setInstanceCreator(this::createInstance);
        add(form.add(singularFormPanel, singularValidationButton));
    }

    private SInstance createInstance() {
        SIComposite currentInstance;
        RefType refType = mockFormConfig.getTypeLoader().loadRefTypeOrException("mockType");
        if (refType.get().isComposite()) {
            typeBuilder.accept((STypeComposite) refType.get());
        }
        if (instanceCreator != null) {
            currentInstance = instanceCreator.apply(refType);
        } else {
            SDocumentFactory factory = mockFormConfig.getDocumentFactory();
            currentInstance = (SIComposite) factory.createInstance(refType);
        }
        instancePopulators.forEach(populator -> populator.accept(currentInstance));
        return currentInstance;
    }

    public Form<?> getForm() {
        return form;
    }

    public SingularFormPanel getSingularFormPanel() {
        return singularFormPanel;
    }

    public SingularValidationButton getSingularValidationButton() {
        return singularValidationButton;
    }

    public void setAsVisualizationView() {
        singularFormPanel.setViewMode(ViewMode.READ_ONLY);
    }

    public void setAsEditView() {
        singularFormPanel.setViewMode(ViewMode.EDIT);
    }

    public void enableAnnotation() {
        singularFormPanel.setAnnotationMode(AnnotationMode.EDIT);
    }

    public SIComposite getInstance() {
        return (SIComposite) singularFormPanel.getInstance();
    }

    public void setInstanceCreator(IFunction<RefType, SIComposite> instanceCreator) {
        this.instanceCreator = instanceCreator;
    }

    final IConsumer<STypeComposite> getTypeBuilder() {
        return typeBuilder;
    }

    public void setTypeBuilder(IConsumer<STypeComposite> typeBuilder) {
        this.typeBuilder = typeBuilder;
    }

    public final void addInstancePopulator(IConsumer<SIComposite> populator) {
        instancePopulators.add(populator);
    }


    public static class MockSDocumentFactory extends SDocumentFactory {

        @Override
        protected RefSDocumentFactory createDocumentFactoryRef() {
            return new RefMockDocumentFactory(this);
        }

        @Override
        protected void setupDocument(SDocument document) {
        }
    }

    public static class RefMockDocumentFactory extends RefSDocumentFactory {

        public RefMockDocumentFactory(MockSDocumentFactory factory) {
            super(factory);
        }

        @Nonnull
        @Override
        protected SDocumentFactory retrieve() {
            return new MockSDocumentFactory();
        }
    }

    public static class MockTypeLoader extends TypeLoader<String> implements Serializable {

        transient private final SDictionary dictionary;

        {
            dictionary = SDictionary.create();
        }

        @Nonnull
        @Override
        protected Optional<RefType> loadRefTypeImpl(@Nonnull String typeId) {
            return Optional.of(RefType.of(() -> loadTypeImpl2(typeId)));
        }

        @Nonnull
        @Override
        protected Optional<SType<?>> loadTypeImpl(@Nonnull String typeId) {
            return Optional.of(loadTypeImpl2(typeId));
        }

        @Nonnull
        private SType<?> loadTypeImpl2(String typeId) {
            return dictionary.getTypeOptional(typeId).orElseGet(() -> (SType<?>) dictionary.createNewPackage(typeId)
                    .createCompositeType("mockRoot"));
        }
    }
}
