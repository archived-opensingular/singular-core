package br.net.mirante.singular.server.commons.wicket.view.form;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.flow.RedirectToUrlException;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.$m;

import br.net.mirante.singular.commons.util.Loggable;
import br.net.mirante.singular.flow.core.MTransition;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.context.SFormConfig;
import br.net.mirante.singular.form.document.RefType;
import br.net.mirante.singular.form.document.SDocumentFactory;
import br.net.mirante.singular.form.persistence.FormKey;
import br.net.mirante.singular.form.persistence.entity.FormEntity;
import br.net.mirante.singular.form.service.IFormService;
import br.net.mirante.singular.form.wicket.component.SingularButton;
import br.net.mirante.singular.form.wicket.component.SingularSaveButton;
import br.net.mirante.singular.form.wicket.enums.AnnotationMode;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.form.wicket.panel.SingularFormPanel;
import br.net.mirante.singular.persistence.entity.ProcessInstanceEntity;
import br.net.mirante.singular.persistence.entity.TaskDefinitionEntity;
import br.net.mirante.singular.server.commons.config.ConfigProperties;
import br.net.mirante.singular.server.commons.flow.metadata.ServerContextMetaData;
import br.net.mirante.singular.server.commons.persistence.entity.form.DraftEntity;
import br.net.mirante.singular.server.commons.persistence.entity.form.FormPetitionEntity;
import br.net.mirante.singular.server.commons.persistence.entity.form.PetitionEntity;
import br.net.mirante.singular.server.commons.service.PetitionService;
import br.net.mirante.singular.server.commons.wicket.SingularSession;
import br.net.mirante.singular.server.commons.wicket.builder.MarkupCreator;
import br.net.mirante.singular.server.commons.wicket.view.template.Content;
import br.net.mirante.singular.server.commons.wicket.view.template.Template;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.TemplatePanel;
import br.net.mirante.singular.util.wicket.modal.BSModalBorder;

public abstract class AbstractFormPage<T extends PetitionEntity> extends Template implements Loggable {

    protected static final String URL_PATH_ACOMPANHAMENTO = "/singular/peticionamento/acompanhamento";

    @Inject
    protected PetitionService<T> petitionService;

    @Inject
    protected IFormService formService;

    @Inject
    @Named("formConfigWithDatabase")
    protected SFormConfig<String> singularFormConfig;

    protected final Class<T>            petitionClass;
    protected final FormPageConfig      config;
    protected final IModel<T>           currentModel;
    protected final IModel<FormKey>     formModel;
    protected       AbstractFormContent content;

    public AbstractFormPage(Class<T> petitionClass, FormPageConfig config) {
        if (config == null) {
            throw new RedirectToUrlException("/singular");
        }
        this.petitionClass = Objects.requireNonNull(petitionClass);
        this.config = Objects.requireNonNull(config);
        this.currentModel = $m.ofValue();
        this.formModel = $m.ofValue();
        Objects.requireNonNull(getFormType(config));
    }

    @Override
    protected boolean withMenu() {
        return false;
    }

    @Override
    protected void onInitialize() {
        final T petition;
        if (StringUtils.isBlank(config.getPetitionId())) {
            petition = petitionService.createNewPetitionWithoutSave(petitionClass, config, this::onNewPetitionCreation);
        } else {
            petition = petitionService.find(Long.valueOf(config.getPetitionId()));
        }
        if (petition.getCod() != null) {
            final FormEntity formEntityDraftOrPetition = getFormEntityDraftOrPetition(petition);
            if (formEntityDraftOrPetition != null) {
                formModel.setObject(formService.keyFromObject(formEntityDraftOrPetition.getCod()));
            }
        }
        currentModel.setObject(petition);
        super.onInitialize();
    }

    private FormEntity getFormEntityDraftOrPetition(T petition) {
        return Optional
                .ofNullable(petition.getCurrentDraftEntity())
                .map(DraftEntity::getForm)
                .filter(form -> petitionService.findFormType(form.getCod()).getAbbreviation().equals(getFormType(config)))
                .orElse(getFormPetitionEntity(petition).map(FormPetitionEntity::getForm).orElse(null));
    }

    public Optional<FormPetitionEntity> getFormPetitionEntity(T petition) {
        if (isMainForm()) {
            return petitionService.findFormPetitionEntityByTypeName(petition.getCod(), getFormType(config));
        } else {
            return petitionService.findFormPetitionEntityByTypeNameAndTask(petition.getCod(), getFormType(config), getCurrentTaskDefinition(petition).map(TaskDefinitionEntity::getCod).orElse(null));
        }
    }

    private Optional<TaskDefinitionEntity> getCurrentTaskDefinition(T petition) {
        final ProcessInstanceEntity processInstanceEntity = petition.getProcessInstanceEntity();
        if (processInstanceEntity != null) {
            return Optional.of(processInstanceEntity.getCurrentTask().getTask().getTaskDefinition());
        }
        return Optional.empty();
    }

    @Override
    protected Content getContent(String id) {

        if (getFormType(config) == null && config.getPetitionId() == null) {
            String urlServidorSingular = ConfigProperties.get(ConfigProperties.SINGULAR_SERVIDOR_ENDERECO);
            throw new RedirectToUrlException(urlServidorSingular);
        }

        content = new AbstractFormContent(id, getFormType(config), getViewMode(config), getAnnotationMode(config)) {

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
            protected IModel<? extends PetitionEntity> getFormModel() {
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

            @Override
            protected void onBuildSingularFormPanel(SingularFormPanel singularFormPanel) {
                AbstractFormPage.this.onBuildSingularFormPanel(singularFormPanel);
            }

            @Override
            protected Component buildExtraContent(String id) {
                return Optional.ofNullable(AbstractFormPage.this.buildExtraContent(id)).orElse(super.buildExtraContent(id));
            }
        };

        return content;
    }

    private Component buildExtraContent(String id) {
        final TemplatePanel extraPanel     = new TemplatePanel(id, MarkupCreator.div("extraContainer"));
        final BSContainer   extraContainer = new BSContainer("extraContainer");
        extraPanel.add(extraContainer);
        appendExtraContent(extraContainer);
        extraPanel.add($b.visibleIf(() -> extraContainer.visitChildren((object, visit) -> visit.stop("found!")) != null));
        return extraPanel;
    }

    protected void appendExtraContent(BSContainer extraContainer) {
    }

    protected void onBuildSingularFormPanel(SingularFormPanel singularFormPanel) {
    }

    protected abstract IModel<?> getContentSubtitleModel();

    protected abstract String getIdentifier();

    protected void onNewPetitionCreation(T petition, FormPageConfig config) {
    }

    protected void configureCustomButtons(BSContainer<?> buttonContainer, BSContainer<?> modalContainer, ViewMode viewMode, AnnotationMode annotationMode, IModel<? extends SInstance> currentInstance) {
        List<MTransition> trans = null;
        if (StringUtils.isNotEmpty(config.getPetitionId())) {
            trans = petitionService.listCurrentTaskTransitions(Long.valueOf(config.getPetitionId()));
        }
        if (CollectionUtils.isNotEmpty(trans) && (ViewMode.EDIT.equals(viewMode) || AnnotationMode.EDIT.equals(annotationMode))) {
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

    protected final T getUpdatedPetitionFromInstance(IModel<? extends SInstance> currentInstance, boolean mainForm) {
        T petition = currentModel.getObject();
        if (currentInstance.getObject() instanceof SIComposite && mainForm) {
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
            return formService.loadSInstance(formModel.getObject(), refType, documentFactory);
        }
    }

    protected void buildFlowTransitionButton(String buttonId, BSContainer<?> buttonContainer, BSContainer<?> modalContainer, String transitionName, IModel<? extends SInstance> instanceModel, ViewMode viewMode) {
        final BSModalBorder modal = buildFlowConfirmationModal(buttonId, modalContainer, transitionName, instanceModel, viewMode);
        buildFlowButton(buttonId, buttonContainer, transitionName, modal);
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
                .addButton(BSModalBorder.ButtonStyle.CANCEl, "label.button.close", new AjaxButton("cancel-btn") {
                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        enviarModal.hide(target);
                    }
                })
                .addButton(BSModalBorder.ButtonStyle.CONFIRM, "label.button.confirm", new SingularSaveButton("confirm-btn", instanceModel) {

                    protected void onValidationSuccess(AjaxRequestTarget target, Form<?> form, IModel<? extends SInstance> instanceModel) {
                        AbstractFormPage.this.send(instanceModel, target, enviarModal);
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
        saveForm(currentInstance, null);
    }

    protected void saveForm(IModel<? extends SInstance> currentInstance, String transitionName) {
        onBeforeSave(currentInstance);
        formModel.setObject(petitionService.saveOrUpdate(getUpdatedPetitionFromInstance(currentInstance, isMainForm()), currentInstance.getObject(), true, isMainForm(), t -> onSave(t, transitionName)));
    }

    protected void onSave(T petition, String transitionName) {

    }

    protected boolean onBeforeSend(IModel<? extends SInstance> currentInstance) {
        configureLazyFlowIfNeeded(currentInstance, currentModel.getObject(), config);
        saveForm(currentInstance);
        return true;
    }

    protected void onBeforeSave(IModel<? extends SInstance> currentInstance) {
        configureLazyFlowIfNeeded(currentInstance, currentModel.getObject(), config);
    }

    protected void configureLazyFlowIfNeeded(IModel<? extends SInstance> currentInstance, T petition, FormPageConfig cfg) {
        if (petition.getProcessDefinitionEntity() == null && cfg.isWithLazyProcessResolver()) {
            cfg
                    .getLazyFlowDefinitionResolver()
                    .resolve(cfg, (SIComposite) currentInstance.getObject())
                    .ifPresent(clazz -> {
                        petition.setProcessDefinitionEntity(petitionService.findEntityProcessDefinitionByClass(clazz));
                    });
        }
    }

    protected void send(IModel<? extends SInstance> currentInstance, AjaxRequestTarget target, BSModalBorder enviarModal) {
        if (onBeforeSend(currentInstance)) {
            formModel.setObject(petitionService.send(getUpdatedPetitionFromInstance(currentInstance, isMainForm()), currentInstance.getObject(), isMainForm(), SingularSession.get().getUsername()));
            onSended(target, enviarModal);
        }
    }

    protected void onSended(AjaxRequestTarget target, BSModalBorder enviarModal) {
        atualizarContentWorklist(target);
        if (getIdentifier() == null) {
            addToastrSuccessMessageWorklist("message.send.success", getUrlPathAcompanhamento());
        } else {
            addToastrSuccessMessageWorklist("message.send.success.identifier", getIdentifier(), getUrlPathAcompanhamento());
        }
        target.appendJavaScript("; window.close();");
    }

    protected boolean onBeforeExecuteTransition(AjaxRequestTarget ajaxRequestTarget, Form<?> form, String transitionName, IModel<? extends SInstance> currentInstance) {
        saveForm(currentInstance, transitionName);
        return true;
    }

    protected void executeTransition(AjaxRequestTarget ajaxRequestTarget, Form<?> form, String transitionName, IModel<? extends SInstance> currentInstance) {
        if (onBeforeExecuteTransition(ajaxRequestTarget, form, transitionName, currentInstance)) {
            formModel.setObject(petitionService.saveAndExecuteTransition(transitionName, currentModel.getObject(), currentInstance.getObject(), isMainForm(), this::onTransition));
            onTransitionExecuted(ajaxRequestTarget, transitionName);
        }
    }

    protected void onTransition(PetitionEntity pe, String transitionName) {

    }

    protected void onTransitionExecuted(AjaxRequestTarget ajaxRequestTarget, String transitionName) {
        atualizarContentWorklist(ajaxRequestTarget);
        addToastrSuccessMessageWorklist("message.action.success", transitionName);
        closeBrowserWindow(ajaxRequestTarget);
    }

    protected void closeBrowserWindow(AjaxRequestTarget ajaxRequestTarget) {
        ajaxRequestTarget.appendJavaScript("window.close();");
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

    private void buildFlowButton(String buttonId,
                                 BSContainer<?> buttonContainer,
                                 String transitionName,
                                 BSModalBorder confirmarAcaoFlowModal) {
        final TemplatePanel tp = buttonContainer.newTemplateTag(tt ->
                "<button  type='submit' class='btn' wicket:id='" + buttonId + "'>\n <span wicket:id='flowButtonLabel' /> \n</button>\n"
        );
        final SingularButton singularButton = new SingularButton(buttonId, content.getFormInstance()) {
            @Override
            protected void onSubmit(AjaxRequestTarget ajaxRequestTarget, Form<?> form) {
                showConfirmModal(transitionName, confirmarAcaoFlowModal, ajaxRequestTarget);
            }
        };
        singularButton.add(new Label("flowButtonLabel", transitionName).setRenderBodyOnly(true));
        tp.add(singularButton);
    }

    protected void showConfirmModal(String transitionName, BSModalBorder modal, AjaxRequestTarget ajaxRequestTarget) {
        modal.show(ajaxRequestTarget);
    }

    /**
     * @param idSuffix -> button id suffix
     * @param mc       -> modal container
     * @param tn       -> transition name
     * @param im       -> instance model
     * @param vm       -> view mode
     * @return
     */
    private BSModalBorder buildFlowConfirmationModal(String idSuffix, BSContainer<?> mc, String tn, IModel<? extends SInstance> im, ViewMode vm) {
        final FlowConfirmModal flowConfirmModal   = resolveFlowConfirmModalBuilder(tn);
        final TemplatePanel    modalTemplatePanel = mc.newTemplateTag(t -> flowConfirmModal.getMarkup(idSuffix));
        final BSModalBorder    modal              = flowConfirmModal.init(idSuffix, tn, im, vm);
        modalTemplatePanel.add(modal);
        return modal;
    }

    /**
     * @param tn -> the transition name
     * @return the FlowConfirmModal
     */
    protected FlowConfirmModal<T> resolveFlowConfirmModalBuilder(String tn) {
        return new SimpleMessageFlowConfirmModal<>(this);
    }

    protected boolean isMainForm() {
        return true;
    }

    protected String getFormType(FormPageConfig formPageConfig) {
        return formPageConfig.getFormType();
    }

    protected ViewMode getViewMode(FormPageConfig formPageConfig) {
        return formPageConfig.getViewMode();
    }

    protected AnnotationMode getAnnotationMode(FormPageConfig formPageConfig) {
        return formPageConfig.getAnnotationMode();
    }

    protected FormKey loadFormKeyFromTypeAndTask(String typeName) {

        final T petitionEntity = currentModel.getObject();

        if (petitionEntity != null) {
            return petitionService
                    .findFormPetitionEntityByTypeNameAndTask(
                            petitionEntity.getCod(),
                            typeName,
                            getCurrentTaskDefinition(petitionEntity).map(TaskDefinitionEntity::getCod).orElse(null)
                    )
                    .map(FormPetitionEntity::getForm)
                    .map(FormEntity::getCod)
                    .map(cod -> formService.keyFromObject(cod))
                    .orElse(null);
        }

        return null;
    }

    public String getUrlPathAcompanhamento() {
        return URL_PATH_ACOMPANHAMENTO;
    }

}