package br.net.mirante.singular.pet.module.wicket.view.form;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.context.SFormConfig;
import br.net.mirante.singular.form.mform.core.annotation.AtrAnnotation;
import br.net.mirante.singular.form.mform.core.annotation.SIAnnotation;
import br.net.mirante.singular.form.mform.document.RefType;
import br.net.mirante.singular.form.mform.io.MformPersistenciaXML;
import br.net.mirante.singular.form.util.xml.MElement;
import br.net.mirante.singular.form.wicket.component.SingularButton;
import br.net.mirante.singular.form.wicket.component.SingularSaveButton;
import br.net.mirante.singular.form.wicket.component.SingularValidationButton;
import br.net.mirante.singular.form.wicket.enums.AnnotationMode;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.form.wicket.panel.SingularFormPanel;
import br.net.mirante.singular.pet.module.wicket.view.template.Content;
import br.net.mirante.singular.util.wicket.modal.BSModalBorder;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Optional;


public abstract class AbstractFormContent extends Content {


    protected final BSModalBorder enviarModal = new BSModalBorder("enviar-modal", getMessage("label.title.send"));
    protected final String key;
    protected final String typeName;
    protected ViewMode viewMode = ViewMode.EDITION;
    protected AnnotationMode annotationMode = AnnotationMode.NONE;
    protected SingularFormPanel<String> singularFormPanel;

    @Inject
    @Named("formConfigWithDatabase")
    private SFormConfig<String> singularFormConfig;


    public AbstractFormContent(String idWicket, String type, String formId, ViewMode viewMode, AnnotationMode annotationMode) {
        super(idWicket, false, true);
        this.viewMode = viewMode;
        this.annotationMode = annotationMode;
        this.typeName = type;
        this.key = formId;
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
        form.add(buildSendButton());
        form.add(buildSaveButton());
        form.add(buildSaveAnnotationButton());
        form.add(buildSaveWithoutValidateButton());
        form.add(buildValidateButton());
        form.add(buildCancelButton());
        form.add(buildConfirmationModal());
        return form;
    }


    private SingularFormPanel buildSingularBasePanel() {
        singularFormPanel = new SingularFormPanel<String>("singular-panel", singularFormConfig) {

            @Override
            protected SInstance createInstance(SFormConfig<String> singularFormConfig) {
                loadOrCreateFormModel(key, typeName, viewMode, annotationMode);
                RefType refType = singularFormConfig.getTypeLoader().loadRefTypeOrException(typeName);
                String xml = getFormXML(getFormModel());
                if (StringUtils.isBlank(xml)) {
                    return singularFormConfig.getDocumentFactory().createInstance(refType);
                } else {
                    SInstance instance = MformPersistenciaXML.fromXML(refType, xml, singularFormConfig.getDocumentFactory());
                    MformPersistenciaXML.annotationLoadFromXml(instance, getAnnotationsXML(getFormModel()));
                    return instance;
                }
            }

            @Override
            public ViewMode getViewMode() {
                return viewMode;
            }

            @Override
            public AnnotationMode annotation() {
                return annotationMode;
            }
        };

        return singularFormPanel;
    }


    private void backToCrudPage(Component componentContext) {
        System.out.println(" Voltar para pagina alalalalla");
    }

    private Component buildSendButton() {
        final Component button = new SingularButton("send-btn") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                enviarModal.show(target);
            }

            @Override
            public IModel<? extends SInstance> getCurrentInstance() {
                return singularFormPanel.getRootInstance();
            }


        };
        return button;
    }

    private Component buildSaveButton() {
        final Component button = new SingularSaveButton("save-btn") {
            @Override
            public IModel<? extends SInstance> getCurrentInstance() {
                return singularFormPanel.getRootInstance();
            }

            @Override
            protected void handleSaveXML(AjaxRequestTarget target, MElement xml) {
                setFormXML(getFormModel(), xml.toStringExato());
                AbstractFormContent.this.saveForm(getCurrentInstance());
            }


        };
        return button;
    }


    private Component buildSaveAnnotationButton() {
        final Component button = new SingularValidationButton("save-annotation-btn") {
            @Override
            public IModel<? extends SInstance> getCurrentInstance() {
                return singularFormPanel.getRootInstance();
            }

            protected void save() {
                getCurrentInstance().getObject().getDocument().persistFiles();
                processAnnotations(getCurrentInstance().getObject());
                saveForm(getFormModel());
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
        return button;
    }

    private void processAnnotations(SInstance instancia) {
        AtrAnnotation annotatedInstance = instancia.as(AtrAnnotation::new);
        List<SIAnnotation> allAnnotations = annotatedInstance.allAnnotations();
        if (!allAnnotations.isEmpty()) {
            Optional<String> annXml = annotationsToXml(annotatedInstance);
            setAnnotationsXML(getFormModel(), annXml.orElse(""));
        }
    }

    private Optional<String> annotationsToXml(AtrAnnotation annotatedInstance) {
        return MformPersistenciaXML.toStringXML(annotatedInstance.persistentAnnotations());
    }

    private Component buildSaveWithoutValidateButton() {
        final Component button = new SingularButton("save-whitout-validate-btn") {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                MElement rootXml = MformPersistenciaXML.toXML(getCurrentInstance().getObject());
                setFormXML(getFormModel(), rootXml.toStringExato());
                processAnnotations(getCurrentInstance().getObject());
                saveForm(getFormModel());
                backToCrudPage(this);
            }

            @Override
            public IModel<? extends SInstance> getCurrentInstance() {
                return singularFormPanel.getRootInstance();
            }

        };
        return button;
    }

    @SuppressWarnings("rawtypes")
    protected AjaxLink<?> buildCancelButton() {
        return new AjaxLink("cancel-btn") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                backToCrudPage(this);
            }
        };
    }

    protected Component buildValidateButton() {
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

        return button;
    }

    private Component buildConfirmationModal() {
        enviarModal
                .addButton(BSModalBorder.ButtonStyle.EMPTY, "label.button.cancel", new AjaxButton("cancel-btn") {
                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        enviarModal.hide(target);
                    }
                })
                .addButton(BSModalBorder.ButtonStyle.DANGER, "label.button.confirm", new SingularSaveButton("confirm-btn") {
                    @Override
                    public IModel<? extends SInstance> getCurrentInstance() {
                        return singularFormPanel.getRootInstance();
                    }

                    @Override
                    protected void handleSaveXML(AjaxRequestTarget target, MElement xml) {
                        setFormXML(getFormModel(), xml.toStringExato());
                        AbstractFormContent.this.send(getCurrentInstance(), xml);
                        target.appendJavaScript("; window.close();");
                    }

                    @Override
                    protected void onValidationError(AjaxRequestTarget target, Form<?> form, IModel<? extends SInstance> instanceModel) {
                        enviarModal.hide(target);
                        target.add(form);
                    }
                });

        return enviarModal;
    }

    protected abstract String getFormXML(IModel<?> model);

    protected abstract void setFormXML(IModel<?> model, String xml);

    protected abstract void saveForm(IModel<?> currentInstance);

    protected abstract void send(IModel<? extends SInstance> currentInstance, MElement xml);

    protected abstract void loadOrCreateFormModel(String formId, String type, ViewMode viewMode, AnnotationMode annotationMode);

    protected abstract IModel<?> getFormModel();

    protected abstract String getAnnotationsXML(IModel<?> model);

    protected abstract void setAnnotationsXML(IModel<?> model, String xml);


}