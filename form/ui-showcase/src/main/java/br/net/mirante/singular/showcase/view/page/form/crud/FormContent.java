package br.net.mirante.singular.showcase.view.page.form.crud;

import java.util.Optional;
import java.util.UUID;

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
import org.apache.wicket.util.string.StringValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.context.SFormConfig;
import br.net.mirante.singular.form.mform.document.SDocumentFactory;
import br.net.mirante.singular.form.mform.io.MformPersistenciaXML;
import br.net.mirante.singular.form.util.xml.MElement;
import br.net.mirante.singular.form.wicket.component.SingularSaveButton;
import br.net.mirante.singular.form.wicket.component.SingularValidationButton;
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

    private static final Logger logger = LoggerFactory.getLogger(FormContent.class);

    private final String key;
    private final String typeName;
    private ViewMode viewMode = ViewMode.EDITION;

    private ExampleDataDTO currentModel;
    private SingularFormPanel singularFormPanel;

    @Inject
    private ExampleDataDAO dao;

    @Inject
    @Named("formConfigWithDatabase")
    private SFormConfig<String> singularFormConfig;

    private boolean enableAnnotation;

    public FormContent(String id, StringValue type, StringValue key, StringValue viewMode,
                       StringValue enableAnnotation) {
        super(id, false, true);
        if (!viewMode.isNull()) {   this.viewMode = ViewMode.valueOf(viewMode.toString());  }
        if (!enableAnnotation.isNull()) {   this.enableAnnotation = Boolean.valueOf(enableAnnotation.toString());  }
        this.typeName = type.toString();
        this.key = key.toString();
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(buildForm());
    }

    @Override
    protected IModel<?> getContentTitlelModel() {
        return new ResourceModel("label.content.title");
    }

    @Override
    protected IModel<?> getContentSubtitlelModel() {
        return new ResourceModel("label.content.title");
    }

    private Form<?> buildForm() {
        Form<?> form = new Form<>("save-form");
        form.setMultiPart(true);
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
            protected SType<?> getTipo(SFormConfig<String> singularFormConfig) {
                return singularFormConfig.getDictionaryLoader().loadType(typeName, typeName);
            }

            @Override
            protected SInstance createInstance(SType<?> tipo, SDocumentFactory documentFactory) {
                loadOrbuildModel();

                String xml = currentModel.getXml();
                if (StringUtils.isBlank(xml)) {
                    return documentFactory.createInstance(tipo);
                } else {
                    SInstance instance = MformPersistenciaXML.fromXML(tipo, xml, documentFactory);
                    MformPersistenciaXML.annotationLoadFromXml(instance, currentModel.getAnnnotations());
                    return instance;
                }
            }

            @Override
            public ViewMode getViewMode() {
                return viewMode;
            }

            @Override
            public boolean annotationEnabled() {    return enableAnnotation;    }
        };

        return singularFormPanel;
    }

    private void loadOrbuildModel() {
        if (key == null || key.isEmpty()) {
            currentModel = new ExampleDataDTO(UUID.randomUUID().toString());
            currentModel.setType(typeName);
        } else {
            currentModel = dao.find(key, typeName);
        }
    }

    private void backToCrudPage(Component componentContext) {
        PageParameters params = new PageParameters()
                .add(CrudPage.TYPE_NAME, currentModel.getType());
        componentContext.setResponsePage(CrudPage.class, params);
    }

    private Component buildSaveButton() {
        final Component button = new SingularSaveButton("save-btn") {
            @Override
            public IModel<? extends SInstance> getCurrentInstance() {
                return singularFormPanel.getRootInstance();
            }

            @Override
            protected void handleSaveXML(AjaxRequestTarget target, MElement xml) {
                getCurrentInstance().getObject().getDocument().persistFiles();
                if (xml != null) {
                    currentModel.setXml(xml.toStringExato());
                } else {
                    currentModel.setXml(StringUtils.EMPTY);
                }
                dao.save(currentModel);
                backToCrudPage(this);
            }
        };
        return button.add(visibleOnlyInEditionBehaviour());
    }

    private Component buildSaveAnnotationButton() {
        final Component button = new SingularValidationButton("save-annotation-btn") {
            @Override
            public IModel<? extends SInstance> getCurrentInstance() {
                return singularFormPanel.getRootInstance();
            }

            protected void save() {
                getCurrentInstance().getObject().getDocument().persistFiles();
                addAnnotationsToModel(getCurrentInstance().getObject());
                dao.save(currentModel);
                backToCrudPage(this);
            }

            @Override
            protected void onValidationSuccess(AjaxRequestTarget target, Form<?> form, IModel<? extends SInstance> instanceModel) {
                save();
            }

            @Override
            protected void onValidationError(AjaxRequestTarget target, Form<?> form, IModel<? extends SInstance> instanceModel) {
                save();
            }
        };
        return button.add(visibleOnlyInAnnotationBehaviour());
    }

    private void addAnnotationsToModel(SInstance instancia) {
        Optional<String> xmlAnnotation = MformPersistenciaXML.annotationToXmlString(instancia);
        currentModel.setAnnotations(xmlAnnotation.orElse(null));
    }

    private Component buildSaveWithoutValidateButton() {
        final Component button = new SingularValidationButton("save-whitout-validate-btn") {
            protected void save() {
                MElement rootXml = MformPersistenciaXML.toXML(getCurrentInstance().getObject());
                getCurrentInstance().getObject().getDocument().persistFiles();
                if (rootXml != null) {
                    currentModel.setXml(rootXml.toStringExato());
                } else {
                    currentModel.setXml(StringUtils.EMPTY);
                }
                addAnnotationsToModel(getCurrentInstance().getObject());
                dao.save(currentModel);
                backToCrudPage(this);
            }

            @Override
            protected void onValidationSuccess(AjaxRequestTarget target, Form<?> form, IModel<? extends SInstance> instanceModel) {
                save();
            }

            @Override
            protected void onValidationError(AjaxRequestTarget target, Form<?> form, IModel<? extends SInstance> instanceModel) {
                save();
            }

            @Override
            public IModel<? extends SInstance> getCurrentInstance() {
                return singularFormPanel.getRootInstance();
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
        final SingularValidationButton button = new SingularValidationButton("validate-btn") {

            @Override
            protected void onValidationSuccess(AjaxRequestTarget target, Form<?> form,
                                               IModel<? extends SInstance> instanceModel) {
            }

            @Override
            public IModel<? extends SInstance> getCurrentInstance() {
                return singularFormPanel.getRootInstance();
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

                component.setVisible(enableAnnotation && viewMode.isVisualization());
            }
        };
    }
}