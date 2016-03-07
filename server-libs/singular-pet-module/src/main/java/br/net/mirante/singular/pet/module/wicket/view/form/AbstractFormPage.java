package br.net.mirante.singular.pet.module.wicket.view.form;

import java.io.Serializable;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.net.mirante.singular.flow.core.MTransition;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.util.xml.MElement;
import br.net.mirante.singular.form.wicket.component.SingularButton;
import br.net.mirante.singular.form.wicket.component.SingularSaveButton;
import br.net.mirante.singular.form.wicket.enums.AnnotationMode;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.pet.module.util.ServerProperties;
import br.net.mirante.singular.pet.module.wicket.view.template.Content;
import br.net.mirante.singular.pet.module.wicket.view.template.Template;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.TemplatePanel;
import br.net.mirante.singular.util.wicket.modal.BSModalBorder;

public abstract class AbstractFormPage extends Template {

    private static final Logger logger = LoggerFactory.getLogger(AbstractFormPage.class);

    protected FormPageConfig config;

    public AbstractFormPage(FormPageConfig config) {
        this.config = config;
    }

    @Override
    protected boolean withMenu() {
        return false;
    }


    @Override
    protected Content getContent(String id) {

        if (config.type == null
                && config.formId == null) {
            String urlServidorSingular = ServerProperties.getProperty(ServerProperties.SINGULAR_SERVIDOR_ENDERECO);
            throw new RedirectToUrlException(urlServidorSingular);
        }

        return new AbstractFormContent(id, config.type, config.formId, config.viewMode, config.annotationMode) {

            @Override
            protected IModel<?> getContentTitleModel() {
                return AbstractFormPage.this.getContentTitleModel();
            }

            @Override
            protected IModel<?> getContentSubtitleModel() {
                return AbstractFormPage.this.getContentSubtitleModel();
            }

            @Override
            protected void buildFlowTransitionButton(String buttonId, BSContainer buttonContainer, BSContainer modalContainer, String transitionName, IModel<? extends SInstance> instanceModel, ViewMode viewMode) {
                AbstractFormPage.this.buildFlowTransitionButton(buttonId, buttonContainer, modalContainer, transitionName, instanceModel, viewMode);
            }

            @Override
            protected List<MTransition> currentTaskTransitions(String formId) {
                return AbstractFormPage.this.currentTaskTransitions(formId);
            }

            @Override
            protected String getFormXML(IModel<?> model) {
                return AbstractFormPage.this.getFormXML(model);
            }

            @Override
            protected void setFormXML(IModel<?> model, String xml) {
                AbstractFormPage.this.setFormXML(model, xml);
            }

            @Override
            protected void saveForm(IModel<?> currentInstance) {
                AbstractFormPage.this.saveForm(currentInstance);
            }

            @Override
            protected void send(IModel<? extends SInstance> currentInstance, MElement xml) {
                AbstractFormPage.this.send(currentInstance, xml);
            }

            @Override
            protected void loadOrCreateFormModel(String formId, String type, ViewMode viewMode, AnnotationMode annotationMode) {
                AbstractFormPage.this.loadOrCreateFormModel(formId, type, viewMode, annotationMode);
            }

            @Override
            protected IModel<?> getFormModel() {
                return AbstractFormPage.this.getFormModel();
            }

            @Override
            protected String getAnnotationsXML(IModel<?> model) {
                return AbstractFormPage.this.getAnnotationsXML(model);
            }

            @Override
            protected void setAnnotationsXML(IModel<?> model, String xml) {
                AbstractFormPage.this.setAnnotationsXML(model, xml);
            }

            @Override
            protected boolean hasProcess() {
                return AbstractFormPage.this.hasProcess();
            }

            @Override
            protected String getIdentifier() {
                return AbstractFormPage.this.getIdentifier();
            }
        };
    }

    protected void buildFlowTransitionButton(String buttonId, BSContainer buttonContainer, BSContainer modalContainer, String transitionName, IModel<? extends SInstance> instanceModel, ViewMode viewMode) {
        BSModalBorder modal = buildFlowConfirmationModal(buttonId, modalContainer, transitionName, instanceModel, viewMode);
        buildFlowButton(buttonId, buttonContainer, transitionName, instanceModel, modal);
    }

    private void buildFlowButton(String buttonId, BSContainer buttonContainer, String transitionName, IModel<? extends SInstance> instanceModel, BSModalBorder confirmarAcaoFlowModal) {
        TemplatePanel tp = buttonContainer
                .newTemplateTag(
                        tt -> "<button  type='submit' class='btn purple' wicket:id='" + buttonId + "'>\n <span wicket:id='flowButtonLabel' /> \n</button>\n");
        SingularButton singularButton = new SingularButton(buttonId) {

            @Override
            public IModel<? extends SInstance> getCurrentInstance() {
                return instanceModel;
            }

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                confirmarAcaoFlowModal.show(target);
            }
        };
        singularButton.add(new Label("flowButtonLabel", transitionName).setRenderBodyOnly(true));
        tp.add(singularButton);
    }

    private BSModalBorder buildFlowConfirmationModal(String buttonId, BSContainer modalContainer, String transitionName, IModel<? extends SInstance> instanceModel, ViewMode viewMode) {
        TemplatePanel tpModal = modalContainer.newTemplateTag(tt ->
                "<div wicket:id='flow-modal" + buttonId + "' class='portlet-body form'>\n"
                        + "<div wicket:id='flow-msg'/>\n"
                        + "</div>\n");
        BSModalBorder confirmarAcaoFlowModal = new BSModalBorder("flow-modal" + buttonId, getMessage("label.button.confirm"));
        tpModal.add(confirmarAcaoFlowModal);
        confirmarAcaoFlowModal
                .addButton(BSModalBorder.ButtonStyle.EMPTY, "label.button.cancel", new AjaxButton("cancel-btn") {
                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        confirmarAcaoFlowModal.hide(target);
                    }
                });

        confirmarAcaoFlowModal.addButton(BSModalBorder.ButtonStyle.DANGER, "label.button.confirm", new SingularSaveButton("confirm-btn", ViewMode.EDITION.equals(viewMode)) {
                    @Override
                    public IModel<? extends SInstance> getCurrentInstance() {
                        return instanceModel;
                    }

                    @Override
                    protected void handleSaveXML(AjaxRequestTarget target, MElement xml) {
                        setFormXML(getFormModel(), xml.toStringExato());
                        AbstractFormPage.this.executeTransition(transitionName, getFormModel());
                        target.appendJavaScript("Singular.atualizarContentWorklist();");
                        addToastrSuccessMessageWorklist("message.action.success", transitionName);

                        target.appendJavaScript("window.close();");
                    }

                    @Override
                    protected void onValidationError(AjaxRequestTarget target, Form<?> form, IModel<? extends SInstance> instanceModel) {
                        confirmarAcaoFlowModal.hide(target);
                        target.add(form);
                    }
                });
        confirmarAcaoFlowModal.add(new Label("flow-msg", String.format("Tem certeza que deseja %s ?", transitionName)));
        return confirmarAcaoFlowModal;
    }

    protected abstract List<MTransition> currentTaskTransitions(String formId);

    protected abstract void executeTransition(String transitionName, IModel<?> currentInstance);

    protected abstract IModel<?> getContentTitleModel();

    protected abstract IModel<?> getContentSubtitleModel();

    protected abstract String getFormXML(IModel<?> model);

    protected abstract void setFormXML(IModel<?> model, String xml);

    protected abstract void saveForm(IModel<?> currentInstance);

    protected abstract void send(IModel<? extends SInstance> currentInstance, MElement xml);

    protected abstract void loadOrCreateFormModel(String formId, String type, ViewMode viewMode, AnnotationMode annotationMode);

    protected abstract IModel<?> getFormModel();

    protected abstract String getAnnotationsXML(IModel<?> model);

    protected abstract void setAnnotationsXML(IModel<?> model, String xml);

    protected abstract boolean hasProcess();

    protected abstract String getIdentifier();

    public static class FormPageConfig implements Serializable {

        public FormPageConfig() {
        }

        public FormPageConfig(String type, String formId, AnnotationMode annotationMode, ViewMode viewMode) {
            this.type = type;
            this.formId = formId;
            this.annotationMode = annotationMode;
            this.viewMode = viewMode;
        }

        public ViewMode viewMode = ViewMode.VISUALIZATION;
        public AnnotationMode annotationMode = AnnotationMode.NONE;
        public String formId;
        public String type;
    }

}
