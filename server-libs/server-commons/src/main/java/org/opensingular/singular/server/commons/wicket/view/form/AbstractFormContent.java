package org.opensingular.singular.server.commons.wicket.view.form;

import org.opensingular.form.RefService;
import org.opensingular.form.SInstance;
import org.opensingular.form.context.SFormConfig;
import org.opensingular.form.document.RefType;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.form.wicket.component.SingularButton;
import org.opensingular.form.wicket.component.SingularValidationButton;
import org.opensingular.form.wicket.enums.AnnotationMode;
import org.opensingular.form.wicket.enums.ViewMode;
import org.opensingular.form.wicket.panel.SingularFormPanel;
import org.opensingular.flow.persistence.entity.ProcessInstanceEntity;
import org.opensingular.singular.server.commons.persistence.entity.form.PetitionEntity;
import org.opensingular.singular.server.commons.wicket.view.template.Content;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSContainer;
import org.opensingular.lib.wicket.util.modal.BSModalBorder;
import org.opensingular.lib.wicket.util.model.IReadOnlyModel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.springframework.orm.hibernate4.HibernateOptimisticLockingFailureException;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Optional;

import static org.opensingular.lib.wicket.util.util.WicketUtils.$b;

public abstract class AbstractFormContent extends Content {


    protected final BSContainer<?> modalContainer = new BSContainer<>("modals");
    protected final String typeName;
    private final BSModalBorder  closeModal          = construirCloseModal();
    protected     IModel<String> msgFlowModel        = new Model<>();
    protected     IModel<String> transitionNameModel = new Model<>();
    protected     ViewMode       viewMode            = ViewMode.EDIT;
    protected     AnnotationMode annotationMode      = AnnotationMode.NONE;
    protected SingularFormPanel<String> singularFormPanel;
    @Inject
    @Named("formConfigWithDatabase")
    protected SFormConfig<String>       singularFormConfig;

    public AbstractFormContent(String idWicket, String type, ViewMode viewMode, AnnotationMode annotationMode) {
        super(idWicket, false, false);
        this.viewMode = viewMode;
        this.annotationMode = annotationMode;
        this.typeName = type;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(buildForm());
    }

    private Form<?> buildForm() {
        Form<?> form = new Form<>("save-form");
        form.setMultiPart(true);
        form.add(buildSingularBasePanel());
        form.add(modalContainer);
        BSModalBorder enviarModal = buildConfirmationModal(modalContainer, getInstanceModel());
        form.add(buildSendButton(enviarModal));
        form.add(buildSaveButton());
        form.add(buildSaveAnnotationButton());
        form.add(buildFlowButtons());
        form.add(buildValidateButton());
        form.add(buildCloseButton());
        form.add(closeModal);
        form.add(buildExtraContent("extra-content"));
        return form;
    }

    protected Component buildExtraContent(String id) {
        return new WebMarkupContainer(id).setVisible(false);
    }


    private IReadOnlyModel<SInstance> getInstanceModel() {
        return (IReadOnlyModel<SInstance>) () -> Optional
                .ofNullable(singularFormPanel)
                .map(SingularFormPanel::getRootInstance)
                .map(IModel::getObject)
                .orElse(null);
    }

    private Component buildFlowButtons() {
        BSContainer<?> buttonContainer = new BSContainer<>("custom-buttons");
        buttonContainer.setVisible(true);

        configureCustomButtons(buttonContainer, modalContainer, viewMode, annotationMode, getFormInstance());

        return buttonContainer;
    }

    protected void configureCustomButtons(BSContainer<?> buttonContainer, BSContainer<?> modalContainer, ViewMode viewMode, AnnotationMode annotationMode, IModel<? extends SInstance> currentInstance) {

    }

    protected abstract SInstance createInstance(SDocumentFactory documentFactory, RefType refType);

    protected void onBuildSingularFormPanel(SingularFormPanel singularFormPanel) {

    }

    private SingularFormPanel<String> buildSingularBasePanel() {
        singularFormPanel = new SingularFormPanel<String>("singular-panel", singularFormConfig) {

            @Override
            protected SInstance createInstance(SFormConfig<String> singularFormConfig) {
                RefType refType = singularFormConfig.getTypeLoader().loadRefTypeOrException(typeName);

                ProcessInstanceEntity processInstance = getProcessInstance();
                SDocumentFactory extendedFactory = singularFormConfig.getDocumentFactory().extendAddingSetupStep(
                        document -> {
                            document.bindLocalService("processService", AbstractFormContent.ProcessFormService.class,
                                    RefService.of((AbstractFormContent.ProcessFormService) () -> processInstance));
                        });
                return AbstractFormContent.this.createInstance(extendedFactory, refType);
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

        onBuildSingularFormPanel(singularFormPanel);

        return singularFormPanel;
    }

    private Component buildSendButton(final BSModalBorder enviarModal) {
        final Component button = new SingularButton("send-btn", getFormInstance()) {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                enviarModal.show(target);
            }

        };
        return button.add(visibleOnlyIfDraftInEditionBehaviour());
    }

    private Component buildSaveButton() {
        final Component button = new SingularButton("save-btn", getFormInstance()) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                try {
                    saveForm(getFormInstance());
                    addToastrSuccessMessage("message.success");
                    atualizarContentWorklist(target);
                } catch (HibernateOptimisticLockingFailureException e) {
                    addToastrErrorMessage("message.save.concurrent_error");
                }
            }
        };
        return button.add(visibleOnlyInEditionBehaviour());
    }

    protected void atualizarContentWorklist(AjaxRequestTarget target) {
        target.appendJavaScript("Singular.atualizarContentWorklist();");
    }


    private Component buildSaveAnnotationButton() {
        final Component button = new SingularValidationButton("save-annotation-btn", singularFormPanel.getRootInstance()) {

            protected void save(AjaxRequestTarget target, IModel<? extends SInstance> instanceModel) {
                saveForm(instanceModel);
                atualizarContentWorklist(target);
            }

            @Override
            protected void onValidationSuccess(AjaxRequestTarget target, Form<?> form, IModel<? extends SInstance> instanceModel) {
                try {
                    save(target, instanceModel);
                    addToastrSuccessMessage("message.success");
                } catch (HibernateOptimisticLockingFailureException e) {
                    addToastrErrorMessage("message.save.concurrent_error");
                }
            }

            @Override
            protected void onValidationError(AjaxRequestTarget target, Form<?> form, IModel<? extends SInstance> instanceModel) {
                save(target, instanceModel);
            }
        };
        return button.add(visibleOnlyInAnnotationBehaviour());
    }

    @SuppressWarnings("rawtypes")
    protected AjaxLink<?> buildCloseButton() {
        return new AjaxLink("close-btn") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                if (isReadOnly()) {
                    atualizarContentWorklist(target);
                    target.appendJavaScript("window.close()");
                } else {
                    closeModal.show(target);
                }
            }
        };
    }

    private boolean isReadOnly() {
        return viewMode == ViewMode.READ_ONLY && annotationMode != AnnotationMode.EDIT;
    }

    protected BSModalBorder construirCloseModal() {
        BSModalBorder closeModal = new BSModalBorder("close-modal", getMessage("label.title.close.draft"));
        closeModal.addButton(BSModalBorder.ButtonStyle.CANCEl, "label.button.cancel", new AjaxButton("cancel-close-btn") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                closeModal.hide(target);
            }
        });
        closeModal.addButton(BSModalBorder.ButtonStyle.CONFIRM, "label.button.confirm", new AjaxButton("close-btn") {
            @Override
            protected String getOnClickScript() {
                return " Singular.atualizarContentWorklist();"
                        + "window.close();";
            }
        });

        return closeModal;
    }

    protected Component buildValidateButton() {
        final SingularValidationButton button = new SingularValidationButton("validate-btn", singularFormPanel.getRootInstance()) {

            @Override
            protected void onValidationSuccess(AjaxRequestTarget target, Form<?> form, IModel<? extends SInstance> instanceModel) {
                addToastrSuccessMessage("message.validation.success");
            }

            @Override
            protected void onValidationError(AjaxRequestTarget target, Form<?> form, IModel<? extends SInstance> instanceModel) {
                super.onValidationError(target, form, instanceModel);
                addToastrErrorMessage("message.validation.error");
            }
        };

        return button.add(visibleOnlyInEditionBehaviour());
    }

    protected abstract BSModalBorder buildConfirmationModal(BSContainer<?> modalContainer, IModel<? extends SInstance> instanceModel);

    protected Behavior visibleOnlyInEditionBehaviour() {
        return $b.visibleIf(viewMode::isEdition);
    }

    protected Behavior visibleOnlyIfDraftInEditionBehaviour() {
        return $b.visibleIf(() -> !hasProcess() && viewMode.isEdition());
    }

    protected Behavior visibleOnlyInAnnotationBehaviour() {
        return $b.visibleIf(annotationMode::editable);
    }

    protected IModel<? extends SInstance> getFormInstance() {
        return singularFormPanel.getRootInstance();
    }

    protected abstract ProcessInstanceEntity getProcessInstance();

    protected abstract void saveForm(IModel<? extends SInstance> currentInstance);

    protected abstract IModel<? extends PetitionEntity> getFormModel();

    protected abstract boolean hasProcess();

    protected abstract String getIdentifier();

    public SingularFormPanel<String> getSingularFormPanel() {
        return singularFormPanel;
    }

    @FunctionalInterface
    public interface ProcessFormService extends Serializable {
        ProcessInstanceEntity getProcessInstance();
    }
}
