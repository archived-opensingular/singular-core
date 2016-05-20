/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.view.page.form.crud;

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
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.string.StringValue;

import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.context.SFormConfig;
import br.net.mirante.singular.form.document.RefType;
import br.net.mirante.singular.form.internal.xml.MElement;
import br.net.mirante.singular.form.io.MformPersistenciaXML;
import br.net.mirante.singular.form.wicket.component.SingularForm;
import br.net.mirante.singular.form.wicket.component.SingularSaveButton;
import br.net.mirante.singular.form.wicket.component.SingularValidationButton;
import br.net.mirante.singular.form.wicket.enums.AnnotationMode;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.form.wicket.panel.SingularFormPanel;
import br.net.mirante.singular.showcase.dao.form.ExampleDataDAO;
import br.net.mirante.singular.showcase.dao.form.ExampleDataDTO;
import br.net.mirante.singular.showcase.view.SingularWicketContainer;
import br.net.mirante.singular.showcase.view.template.Content;

public class FormContent extends Content implements SingularWicketContainer<CrudContent, Void> {

    /**
     *
     */
    private static final long serialVersionUID = 327099871613673185L;

    private Long idExampleData;
    private String typeName;
    private ViewMode viewMode = ViewMode.EDITION;

    private AnnotationMode annotation = AnnotationMode.NONE;
    private ExampleDataDTO currentModel;

    private SingularFormPanel<String> singularFormPanel;

    @Inject
    private ExampleDataDAO dao;

    @Inject
    @Named("formConfigWithDatabase")
    private SFormConfig<String> singularFormConfig;

    public FormContent(String id, StringValue type, StringValue idExampleData, StringValue viewMode, StringValue annotation) {
        super(id, false, true);
        if (!viewMode.isNull()) {
            this.viewMode = ViewMode.valueOf(viewMode.toString());
        }
        if (!annotation.isNull()) {
            this.annotation = AnnotationMode.valueOf(annotation.toString());
        }
        this.typeName = type.toString();
        if(!idExampleData.isNull()) {
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
        return new ResourceModel("label.content.title");
    }

    @Override
    protected IModel<?> getContentSubtitleModel() {
        return new ResourceModel("label.content.title");
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
            public AnnotationMode annotation() {
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
                MElement xml = MformPersistenciaXML.toXML(instanceModel.getObject());
                instanceModel.getObject().getDocument().persistFiles();
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
                MElement rootXml = MformPersistenciaXML.toXML(instanceModel.getObject());
                instanceModel.getObject().getDocument().persistFiles();
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