package br.net.mirante.singular.server.commons.wicket.view.form;

import br.net.mirante.singular.flow.core.Flow;
import br.net.mirante.singular.flow.core.MTransition;
import br.net.mirante.singular.flow.core.ProcessDefinition;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.context.SFormConfig;
import br.net.mirante.singular.form.document.RefType;
import br.net.mirante.singular.form.document.SDocumentFactory;
import br.net.mirante.singular.form.persistence.FormKey;
import br.net.mirante.singular.form.service.IFormService;
import br.net.mirante.singular.form.wicket.component.SingularButton;
import br.net.mirante.singular.form.wicket.component.SingularSaveButton;
import br.net.mirante.singular.form.wicket.enums.AnnotationMode;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.persistence.entity.ProcessInstanceEntity;
import br.net.mirante.singular.server.commons.config.ConfigProperties;
import br.net.mirante.singular.server.commons.exception.SingularServerException;
import br.net.mirante.singular.server.commons.flow.metadata.ServerContextMetaData;
import br.net.mirante.singular.server.commons.persistence.entity.form.AbstractPetitionEntity;
import br.net.mirante.singular.server.commons.service.PetitionService;
import br.net.mirante.singular.server.commons.wicket.SingularSession;
import br.net.mirante.singular.server.commons.wicket.view.template.Content;
import br.net.mirante.singular.server.commons.wicket.view.template.Template;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.TemplatePanel;
import br.net.mirante.singular.util.wicket.modal.BSModalBorder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$m;

public abstract class AbstractFormPage<T extends AbstractPetitionEntity> extends Template {

    protected static final String URL_PATH_ACOMPANHAMENTO = "/singular/peticionamento/acompanhamento";
    private static final   Logger LOGGER                  = LoggerFactory.getLogger(AbstractFormPage.class);

    protected final FormPageConfig      config;
    protected       AbstractFormContent content;

    protected final IModel<T>       currentModel = $m.ofValue();
    protected final IModel<FormKey> formModel    = $m.ofValue();

    private final Class<T> petitionClass;

    @Inject
    @Named("formConfigWithDatabase")
    private SFormConfig<String> singularFormConfig;

    @Inject
    protected PetitionService<T> petitionService;

    @Inject
    private IFormService formService;

    public AbstractFormPage(Class<T> petitionClass, FormPageConfig config) {
        if (config == null) {
            throw new RedirectToUrlException("/singular");
        }
        this.petitionClass = Objects.requireNonNull(petitionClass);
        this.config = Objects.requireNonNull(config);
        Objects.requireNonNull(config.getFormType());
    }

    @Override
    protected boolean withMenu() {
        return false;
    }

    @Override
    protected void onInitialize() {

        T petition;
        if (StringUtils.isBlank(config.getFormId())) {
            try {
                petition = petitionClass.newInstance();
            } catch (Exception e) {
                throw new SingularServerException("Error creating new petition instance", e);
            }
            petition.setType(config.getFormType());
            if (config.containsProcessDefinition()) {
                petition.setProcessType(Flow.getProcessDefinition(config.getProcessDefinition()).getKey());
            }
            petition.setCreationDate(new Date());
            petition.setProcessName(getTypeLabel(config.getFormType()));
            onNewPetitionCreation(petition, config);
        } else {
            petition = petitionService.find(Long.valueOf(config.getFormId()));
        }

        if (petition.getCodForm() != null) {
            FormKey formKey = formService.keyFromObject(petition.getCodForm());
            formModel.setObject(formKey);
        }
        currentModel.setObject(petition);

        super.onInitialize();
    }

    @Override
    protected Content getContent(String id) {

        if (config.getFormType() == null && config.getFormId() == null) {
            String urlServidorSingular = ConfigProperties.get(ConfigProperties.SINGULAR_SERVIDOR_ENDERECO);
            throw new RedirectToUrlException(urlServidorSingular);
        }

        content = new AbstractFormContent(id, config.getFormType(), config.getViewMode(), config.getAnnotationMode()) {

            @Override
            protected SInstance createInstance(SDocumentFactory documentFactory, RefType refType) {
                return AbstractFormPage.this.createInstance(documentFactory, refType);
            }

            @Override
            protected IModel<?> getContentTitleModel() {
                return AbstractFormPage.this.getContentTitleModel();
            }

            @Override
            protected IModel<?> getContentSubtitleModel() {
                return AbstractFormPage.this.getContentSubtitleModel();
            }

            @Override
            protected void configureCustomButtons(BSContainer<?> buttonContainer, BSContainer<?> modalContainer, ViewMode viewMode, AnnotationMode annotationMode, IModel<? extends SInstance> currentInstance) {
                AbstractFormPage.this.configureCustomButtons(buttonContainer, modalContainer, viewMode, annotationMode, currentInstance);
            }

            @Override
            protected BSModalBorder buildConfirmationModal(BSContainer<?> modalContainer, IModel<? extends SInstance> instanceModel) {
                return AbstractFormPage.this.buildConfirmationModal(modalContainer, instanceModel);
            }

            @Override
            protected ProcessInstanceEntity getProcessInstance() {
                return AbstractFormPage.this.getProcessInstance();
            }

//            @Override
//            protected void setProcessInstance(ProcessInstanceEntity pie) {
//                AbstractFormPage.this.setProcessInstance(pie);
//            }

            @Override
            protected void saveForm(IModel<? extends SInstance> currentInstance) {
                AbstractFormPage.this.saveForm(currentInstance);
            }

            @Override
            protected IModel<? extends AbstractPetitionEntity> getFormModel() {
                return currentModel;
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

        return content;
    }


    protected abstract IModel<?> getContentSubtitleModel();

    protected abstract String getIdentifier();

    protected void onNewPetitionCreation(T petition, FormPageConfig config) {
    }

    protected void configureCustomButtons(BSContainer<?> buttonContainer, BSContainer<?> modalContainer, ViewMode viewMode, AnnotationMode annotationMode, IModel<? extends SInstance> currentInstance) {
        List<MTransition> trans = petitionService.listCurrentTaskTransitions(config.getFormId());
        if (CollectionUtils.isNotEmpty(trans) && (ViewMode.EDITION.equals(viewMode) || AnnotationMode.EDIT.equals(annotationMode))) {
            int index = 0;
            for (MTransition t : trans) {
                if (t.getMetaDataValue(ServerContextMetaData.KEY) != null && t.getMetaDataValue(ServerContextMetaData.KEY).isEnabledOn(SingularSession.get().getServerContext())) {
                    String btnId = "flow-btn" + index;
                    buildFlowTransitionButton(
                            btnId, buttonContainer,
                            modalContainer, t.getName(),
                            currentInstance, viewMode);
                }
            }
        } else {
            buttonContainer.setVisible(false).setEnabled(false);
        }
    }

    protected final T getUpdatedPetitionFromInstance(IModel<? extends SInstance> currentInstance) {
        T petition = currentModel.getObject();
        if (currentInstance.getObject() instanceof SIComposite) {
            petition.setDescription(createPetitionDescriptionFromForm(currentInstance.getObject()));
        }
        return petition;
    }

    protected String createPetitionDescriptionFromForm(SInstance instance) {
        return instance.toStringDisplay();
    }

    protected final SInstance createInstance(SDocumentFactory documentFactory, RefType refType) {
        if (formModel.getObject() == null) {
            return documentFactory.createInstance(refType);
        } else {
            return formService.loadFormInstance(formModel.getObject(), refType, documentFactory);
        }
    }

    protected void buildFlowTransitionButton(String buttonId, BSContainer<?> buttonContainer, BSContainer<?> modalContainer, String transitionName, IModel<? extends SInstance> instanceModel, ViewMode viewMode) {
        BSModalBorder modal = buildFlowConfirmationModal(buttonId, modalContainer, transitionName, instanceModel, viewMode);
        buildFlowButton(buttonId, buttonContainer, transitionName, instanceModel, modal);
    }

    public void atualizarContentWorklist(AjaxRequestTarget target) {
        target.appendJavaScript("Singular.atualizarContentWorklist();");
    }

    protected BSModalBorder buildConfirmationModal(BSContainer<?> modalContainer, IModel<? extends SInstance> instanceModel) {
        TemplatePanel tpModal = modalContainer.newTemplateTag(tt ->
                "<div wicket:id='send-modal' class='portlet-body form'>\n"
                        + "<wicket:message key=\"label.confirm.message\"/>\n"
                        + "</div>\n");
        BSModalBorder enviarModal = new BSModalBorder("send-modal", getMessage("label.title.send"));
        enviarModal
                .addButton(BSModalBorder.ButtonStyle.EMPTY, "label.button.close", new AjaxButton("cancel-btn") {
                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        enviarModal.hide(target);
                    }
                })
                .addButton(BSModalBorder.ButtonStyle.DANGER, "label.button.confirm", new SingularSaveButton("confirm-btn", instanceModel) {
                    protected void onValidationSuccess(AjaxRequestTarget target, Form<?> form, IModel<? extends SInstance> instanceModel) {
                        AbstractFormPage.this.send(instanceModel);
                        atualizarContentWorklist(target);
                        if (getIdentifier() == null) {
                            addToastrSuccessMessageWorklist("message.send.success", URL_PATH_ACOMPANHAMENTO);
                        } else {
                            addToastrSuccessMessageWorklist("message.send.success.identifier", getIdentifier(), URL_PATH_ACOMPANHAMENTO);
                        }
                        target.appendJavaScript("; window.close();");
                    }

                    @Override
                    protected void onValidationError(AjaxRequestTarget target, Form<?> form, IModel<? extends SInstance> instanceModel) {
                        enviarModal.hide(target);
                        target.add(form);
                        addToastrErrorMessage("message.send.error");
                    }
                });
        tpModal.add(enviarModal);
        return enviarModal;
    }

    protected void saveForm(IModel<? extends SInstance> currentInstance) {
        onBeforeSave(currentInstance);
        FormKey key = petitionService.saveOrUpdate(getUpdatedPetitionFromInstance(currentInstance),
                currentInstance.getObject());
        formModel.setObject(key);
    }

    protected void onBeforeSend(IModel<? extends SInstance> currentInstance) {
        final T petition = currentModel.getObject();
        if (petition.getProcessType() == null && config.isWithLazyProcessResolver()) {
            final Class<? extends ProcessDefinition> dc = config.getLazyFlowDefinitionResolver().resolve(config, (SIComposite) currentInstance.getObject());
            petition.setProcessType(Flow.getProcessDefinition(dc).getKey());
        }
    }

    protected void onBeforeSave(IModel<? extends SInstance> currentInstance) {

    }

    protected void send(IModel<? extends SInstance> currentInstance) {
        onBeforeSend(currentInstance);
        FormKey key = petitionService.send(getUpdatedPetitionFromInstance(currentInstance), currentInstance.getObject());
        formModel.setObject(key);
    }

    protected void executeTransition(String transitionName, IModel<? extends SInstance> currentInstance) {
        FormKey key = petitionService.saveAndExecuteTransition(transitionName, currentModel.getObject(),
                currentInstance.getObject());
        formModel.setObject(key);
    }

    protected boolean hasProcess() {
        return currentModel.getObject().getProcessInstanceEntity() != null;
    }

    protected ProcessInstanceEntity getProcessInstance() {
        return currentModel.getObject().getProcessInstanceEntity();
    }

    protected void setProcessInstance(ProcessInstanceEntity pie) {
        currentModel.getObject().setProcessInstanceEntity(pie);
    }

    protected IModel<?> getContentTitleModel() {
        return new ResourceModel("label.form.content.title");
    }

    private void buildFlowButton(String buttonId, BSContainer<?> buttonContainer, String transitionName, IModel<? extends SInstance> instanceModel, BSModalBorder confirmarAcaoFlowModal) {
        TemplatePanel tp = buttonContainer
                .newTemplateTag(tt -> "<button  type='submit' class='btn' wicket:id='" + buttonId +
                        "'>\n <span wicket:id='flowButtonLabel' /> \n</button>\n");
        SingularButton singularButton = new SingularButton(buttonId, content.getFormInstance()) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                confirmarAcaoFlowModal.show(target);
            }
        };
        singularButton.add(new Label("flowButtonLabel", transitionName).setRenderBodyOnly(true));
        tp.add(singularButton);
    }

    private BSModalBorder buildFlowConfirmationModal(String buttonId, BSContainer<?> modalContainer, String transitionName, IModel<? extends SInstance> instanceModel, ViewMode viewMode) {
        TemplatePanel tpModal = modalContainer.newTemplateTag(
                tt -> "<div wicket:id='flow-modal" + buttonId + "' class='portlet-body form'>\n" +
                        "<div wicket:id='flow-msg'/>\n" + "</div>\n");
        BSModalBorder confirmarAcaoFlowModal = new BSModalBorder("flow-modal" + buttonId, getMessage("label.button.confirm"));
        tpModal.add(confirmarAcaoFlowModal);
        confirmarAcaoFlowModal
                .addButton(BSModalBorder.ButtonStyle.EMPTY, "label.button.cancel", new AjaxButton("cancel-btn") {
                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        confirmarAcaoFlowModal.hide(target);
                    }
                });

        confirmarAcaoFlowModal.addButton(BSModalBorder.ButtonStyle.DANGER, "label.button.confirm",
                new SingularSaveButton("confirm-btn", instanceModel, ViewMode.EDITION.equals(viewMode)) {
                    protected void onValidationSuccess(AjaxRequestTarget target, Form<?> form,
                                                       IModel<? extends SInstance> instanceModel) {
                        try {
                            AbstractFormPage.this.executeTransition(transitionName, instanceModel);
                            target.appendJavaScript("Singular.atualizarContentWorklist();");
                            addToastrSuccessMessageWorklist("message.action.success", transitionName);
                            target.appendJavaScript("window.close();");
                        } catch (Exception e) { // org.hibernate.StaleObjectStateException
                            LOGGER.error("Erro ao salvar o XML", e);
                            addToastrErrorMessage("message.save.concurrent_error");
                        }
                    }

                    @Override
                    protected void onValidationError(AjaxRequestTarget target, Form<?> form,
                                                     IModel<? extends SInstance> instanceModel) {
                        confirmarAcaoFlowModal.hide(target);
                        target.add(form);
                    }
                });
        confirmarAcaoFlowModal.add(new Label("flow-msg", String.format("Tem certeza que deseja %s ?", transitionName)));
        return confirmarAcaoFlowModal;
    }

    private String getTypeLabel(String typeName) {
        STypeComposite<?> type = (STypeComposite<?>) singularFormConfig
                .getTypeLoader().loadType(typeName).orElseThrow(() -> new SingularServerException("Não foi possivel carregar o tipo"));
        return type.asAtr().getLabel();
    }

}
