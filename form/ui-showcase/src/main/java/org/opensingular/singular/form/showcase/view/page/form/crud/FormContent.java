/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form.showcase.view.page.form.crud;

import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

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
import org.opensingular.form.internal.xml.MElement;
import org.opensingular.form.io.MformPersistenciaXML;
import org.opensingular.singular.form.showcase.dao.form.ExampleDataDAO;
import org.opensingular.singular.form.showcase.dao.form.ExampleDataDTO;
import org.opensingular.singular.form.showcase.view.SingularWicketContainer;
import org.opensingular.singular.form.showcase.view.template.Content;
import org.opensingular.singular.form.wicket.component.SingularForm;
import org.opensingular.singular.form.wicket.component.SingularSaveButton;
import org.opensingular.singular.form.wicket.component.SingularValidationButton;
import org.opensingular.singular.form.wicket.enums.AnnotationMode;
import org.opensingular.singular.form.wicket.enums.ViewMode;
import org.opensingular.singular.form.wicket.panel.SingularFormPanel;

public class FormContent extends Content implements SingularWicketContainer<CrudContent, Void> {

    /**
     *
     */
    private static final long serialVersionUID = 327099871613673185L;

    private Long   idExampleData;
    private String typeName;
    private ViewMode viewMode = ViewMode.EDIT;

    private AnnotationMode            annotation = AnnotationMode.NONE;
    private ExampleDataDTO currentModel;

    private SingularFormPanel<String> singularFormPanel;

    @Inject
    private ExampleDataDAO dao;

    @Inject
    @Named("formConfigWithDatabase")
    private SFormConfig<String> singularFormConfig;

    public FormContent(String id, StringValue type, StringValue idExampleData, StringValue viewMode, StringValue annotation) {
        super(id, false, false);
        if (!viewMode.isNull()) {
            this.viewMode = ViewMode.valueOf(viewMode.toString());
        }
        if (!annotation.isNull()) {
            this.annotation = AnnotationMode.valueOf(annotation.toString());
        }
        this.typeName = type.toString();
        if (!idExampleData.isNull()) {
            this.idExampleData = idExampleData.toLong();
        }
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(buildForm());
    }

    @Override
    protected IModel<?> getContentTitleModel() {
        return Model.of();
    }

    @Override
    protected IModel<?> getContentSubtitleModel() {
        return Model.of();
    }

    private Form<?> buildForm() {
        SingularForm<?> form = new SingularForm<>("save-form");
        form.setMultiPart(true);
        form.setFileMaxSize(Bytes.MAX);
        form.setMaxSize(Bytes.MAX);
        form.add(buildSingularBasePanel());
        form.add(buildSaveButton());
        form.add(buildSaveAnnotationButton());
        form.add(buildSaveWithoutValidateButton());
        form.add(buildValidateButton());
        form.add(buildCancelButton());
        return form;
    }

    private SingularFormPanel<String> buildSingularBasePanel() {
        singularFormPanel = new SingularFormPanel<String>("singular-panel", singularFormConfig) {

            @Override
            protected SInstance createInstance(SFormConfig<String> singularFormConfig) {
                loadOrbuildModel();

                RefType refType = singularFormConfig.getTypeLoader().loadRefTypeOrException(typeName);

                SInstance instance = loadOrCreateInstance(singularFormConfig, refType);
                loadAnnotationsIfNeeded(instance);

                return instance;
            }

            private SInstance loadOrCreateInstance(SFormConfig<String> singularFormConfig, RefType refType) {
                String    xml = currentModel.getXml();
                SInstance instance;
                if (StringUtils.isBlank(xml)) {
                    instance = singularFormConfig.getDocumentFactory().createInstance(refType);
                } else {
                    instance = MformPersistenciaXML.fromXML(refType, xml, singularFormConfig.getDocumentFactory());
                }
                return instance;
            }

            private void loadAnnotationsIfNeeded(SInstance instance) {
                String annotationsXml = currentModel.getAnnnotations();
                if (StringUtils.isNotBlank(annotationsXml)) {
                    MformPersistenciaXML.annotationLoadFromXml(instance, currentModel.getAnnnotations());
                }
            }

            @Override
            public ViewMode getViewMode() {
                return viewMode;
            }

            @Override
            public AnnotationMode getAnnotationMode() {
                return annotation;
            }
        };

        return singularFormPanel;
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
        final Component button = new SingularSaveButton("save-btn", singularFormPanel.getRootInstance()) {

            @Override
            protected void onValidationSuccess(AjaxRequestTarget target, Form<?> form, IModel<? extends SInstance> instanceModel) {
                instanceModel.getObject().getDocument().persistFiles();//Tem que ser feito antes obrigatoriamente para poder atualizar os ids
                MElement xml = MformPersistenciaXML.toXML(instanceModel.getObject());
                if (xml != null) {
                    currentModel.setXml(xml.toStringExato());
                } else {
                    currentModel.setXml(StringUtils.EMPTY);
                }
                currentModel.setDescription(instanceModel.getObject().toStringDisplay());
                dao.save(currentModel);
                backToCrudPage(this);
            }
        };
        return button.add(visibleOnlyInEditionBehaviour());
    }

    private Component buildSaveAnnotationButton() {

        final Component button = new SingularValidationButton("save-annotation-btn", singularFormPanel.getRootInstance()) {

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
        Optional<String> xmlAnnotation = MformPersistenciaXML.annotationToXmlString(instancia);
        currentModel.setAnnotations(xmlAnnotation.orElse(null));
    }

    private Component buildSaveWithoutValidateButton() {
        final Component button = new SingularValidationButton("save-whitout-validate-btn", singularFormPanel.getRootInstance()) {
            protected void save(IModel<? extends SInstance> instanceModel) {
                instanceModel.getObject().getDocument().persistFiles();//Tem que ser feito antes obrigatoriamente para poder atualizar os ids
                MElement rootXml = MformPersistenciaXML.toXML(instanceModel.getObject());
                if (rootXml != null) {
                    currentModel.setXml(rootXml.toStringExato());
                } else {
                    currentModel.setXml(StringUtils.EMPTY);
                }
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
        final SingularValidationButton button = new SingularValidationButton("validate-btn", singularFormPanel.getRootInstance()) {
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

                component.setVisible(viewMode.isEdition());
            }
        };
    }

    private Behavior visibleOnlyInAnnotationBehaviour() {
        return new Behavior() {
            @Override
            public void onConfigure(Component component) {
                super.onConfigure(component);

                component.setVisible(annotation.editable());
            }
        };
    }
}