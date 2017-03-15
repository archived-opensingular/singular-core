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

package org.opensingular.singular.form.showcase.view.page.form.crud;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.string.StringValue;
import org.opensingular.form.SInstance;
import org.opensingular.form.context.SFormConfig;
import org.opensingular.form.document.RefType;
import org.opensingular.form.document.SDocument;
import org.opensingular.form.io.SFormXMLUtil;
import org.opensingular.form.wicket.component.SingularFormWicket;
import org.opensingular.form.wicket.component.SingularSaveButton;
import org.opensingular.form.wicket.component.SingularValidationButton;
import org.opensingular.form.wicket.enums.AnnotationMode;
import org.opensingular.form.wicket.enums.ViewMode;
import org.opensingular.form.wicket.panel.SingularFormPanel;
import org.opensingular.singular.form.showcase.dao.form.ExampleDataDAO;
import org.opensingular.singular.form.showcase.dao.form.ExampleDataDTO;
import org.opensingular.singular.form.showcase.view.SingularWicketContainer;
import org.opensingular.singular.form.showcase.view.template.Content;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Optional;

public class FormContent extends Content implements SingularWicketContainer<CrudContent, Void> {

    /**
     *
     */
    private static final long serialVersionUID = 327099871613673185L;

    private Long   idExampleData;
    private String typeName;

    private ExampleDataDTO currentModel;

    private final SingularFormPanel singularFormPanel;

    @Inject
    private ExampleDataDAO dao;

    @Inject
    @Named("formConfigWithDatabase")
    private SFormConfig<String> singularFormConfig;

    public FormContent(String id, StringValue type, StringValue idExampleData, StringValue viewMode, StringValue annotation) {
        super(id, false, false);
        singularFormPanel = new SingularFormPanel("singular-panel");

        if (!viewMode.isNull()) {
            singularFormPanel.setViewMode(ViewMode.valueOf(viewMode.toString()));
        } else {
            singularFormPanel.setViewMode(ViewMode.EDIT);
        }
        if (!annotation.isNull()) {
            singularFormPanel.setAnnotationMode(AnnotationMode.valueOf(annotation.toString()));
        } else {
            singularFormPanel.setAnnotationMode(AnnotationMode.NONE);
        }
        this.typeName = type.toString();
        if (!idExampleData.isNull()) {
            this.idExampleData = idExampleData.toLong();
        }
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        singularFormPanel.setInstanceCreator(this::createInstance);

        SingularFormWicket<?> form = new SingularFormWicket<>("save-form");
        form.setMultiPart(true);
        form.setFileMaxSize(Bytes.MAX);
        form.setMaxSize(Bytes.MAX);
        form.add(singularFormPanel);
        form.add(buildSaveButton());
        form.add(buildSaveAnnotationButton());
        form.add(buildSaveWithoutValidateButton());
        form.add(buildValidateButton());
        form.add(buildCancelButton());
        add(form);
    }

    @Override
    protected IModel<?> getContentTitleModel() {
        return Model.of();
    }

    @Override
    protected IModel<?> getContentSubtitleModel() {
        return Model.of();
    }

    protected SInstance createInstance() {
        loadOrbuildModel();

        RefType refType = singularFormConfig.getTypeLoader().loadRefTypeOrException(typeName);

        SInstance instance = loadOrCreateInstance(singularFormConfig, refType);
        loadAnnotationsIfNeeded(instance.getDocument());

        return instance;
    }

    private SInstance loadOrCreateInstance(SFormConfig<String> singularFormConfig, RefType refType) {
        String    xml = currentModel.getXml();
        SInstance instance;
        if (StringUtils.isBlank(xml)) {
            instance = singularFormConfig.getDocumentFactory().createInstance(refType);
        } else {
            instance = SFormXMLUtil.fromXML(refType, xml, singularFormConfig.getDocumentFactory());
        }
        return instance;
    }

    private void loadAnnotationsIfNeeded(SDocument document) {
        String annotationsXml = currentModel.getAnnnotations();
        if (StringUtils.isNotBlank(annotationsXml)) {
            SFormXMLUtil.annotationLoadFromXml(document, currentModel.getAnnnotations());
        }
    }

    private void loadOrbuildModel() {
        if (idExampleData == null) {
            currentModel = new ExampleDataDTO();
            currentModel.setType(typeName);
        } else {
            currentModel = dao.find(idExampleData, typeName);
        }
    }

    private void backToCrudPage(Component componentContext) {
        PageParameters params = new PageParameters().add(CrudPage.TYPE_NAME, currentModel.getType());
        componentContext.setResponsePage(CrudPage.class, params);
    }

    private Component buildSaveButton() {
        final Component button = new SingularSaveButton("save-btn", singularFormPanel.getInstanceModel()) {

            @Override
            protected void onValidationSuccess(AjaxRequestTarget target, Form<?> form, IModel<? extends SInstance> instanceModel) {
                instanceModel.getObject().getDocument().persistFiles();//Tem que ser feito antes obrigatoriamente para poder atualizar os ids
                String xml = SFormXMLUtil.toStringXML(instanceModel.getObject()).orElse(StringUtils.EMPTY);
                currentModel.setXml(xml);
                currentModel.setDescription(instanceModel.getObject().toStringDisplay());
                dao.save(currentModel);
                backToCrudPage(this);
            }
        };
        return button.add(visibleOnlyInEditionBehaviour());
    }

    private Component buildSaveAnnotationButton() {

        final Component button = new SingularValidationButton("save-annotation-btn", singularFormPanel.getInstanceModel()) {

            protected void save(IModel<? extends SInstance> instanceModel) {
                instanceModel.getObject().getDocument().persistFiles();
                addAnnotationsToModel(instanceModel.getObject());
                currentModel.setDescription(instanceModel.getObject().toStringDisplay());
                dao.save(currentModel);
                backToCrudPage(this);
            }

            @Override
            protected void onValidationSuccess(AjaxRequestTarget target, Form<?> form, IModel<? extends SInstance> instanceModel) {
                save(instanceModel);
            }

            @Override
            protected void onValidationError(AjaxRequestTarget target, Form<?> form, IModel<? extends SInstance> instanceModel) {
                save(instanceModel);
            }
        };
        return button.add(visibleOnlyInAnnotationBehaviour());
    }

    private void addAnnotationsToModel(SInstance instancia) {
        Optional<String> xmlAnnotation = SFormXMLUtil.annotationToXmlString(instancia);
        currentModel.setAnnotations(xmlAnnotation.orElse(null));
    }

    private Component buildSaveWithoutValidateButton() {
        final Component button = new SingularValidationButton("save-whitout-validate-btn", singularFormPanel.getInstanceModel()) {
            protected void save(IModel<? extends SInstance> instanceModel) {
                instanceModel.getObject().getDocument().persistFiles();//Tem que ser feito antes obrigatoriamente para poder atualizar os ids
                String rootXml = SFormXMLUtil.toStringXML(instanceModel.getObject()).orElse(StringUtils.EMPTY);
                currentModel.setXml(rootXml);
                currentModel.setDescription(instanceModel.getObject().toStringDisplay());
                addAnnotationsToModel(instanceModel.getObject());
                dao.save(currentModel);
                backToCrudPage(this);
            }

            @Override
            protected void onValidationSuccess(AjaxRequestTarget target, Form<?> form, IModel<? extends SInstance> instanceModel) {
                save(instanceModel);
            }

            @Override
            protected void onValidationError(AjaxRequestTarget target, Form<?> form, IModel<? extends SInstance> instanceModel) {
                save(instanceModel);
            }

        };
        return button.add(visibleOnlyInEditionBehaviour());
    }

    @SuppressWarnings("rawtypes")
    private AjaxLink<?> buildCancelButton() {
        return new AjaxLink("cancel-btn") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                backToCrudPage(this);
            }
        };
    }

    private Component buildValidateButton() {
        final SingularValidationButton button = new SingularValidationButton("validate-btn", singularFormPanel.getInstanceModel()) {
            @Override
            protected void onValidationSuccess(AjaxRequestTarget target, Form<?> form, IModel<? extends SInstance> instanceModel) {
            }
        };

        return button.add(visibleOnlyInEditionBehaviour());
    }

    private Behavior visibleOnlyInEditionBehaviour() {
        return new Behavior() {
            @Override
            public void onConfigure(Component component) {
                super.onConfigure(component);

                component.setVisible(singularFormPanel.getViewMode().isEdition());
            }
        };
    }

    private Behavior visibleOnlyInAnnotationBehaviour() {
        return new Behavior() {
            @Override
            public void onConfigure(Component component) {
                super.onConfigure(component);

                component.setVisible(singularFormPanel.getAnnotationMode().editable());
            }
        };
    }
}