package br.net.mirante.singular.server.commons.wicket.view.form;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;

import java.io.Serializable;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.springframework.orm.hibernate4.HibernateOptimisticLockingFailureException;

import br.net.mirante.singular.form.RefService;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.context.SFormConfig;
import br.net.mirante.singular.form.document.RefType;
import br.net.mirante.singular.form.document.SDocumentFactory;
import br.net.mirante.singular.form.wicket.component.SingularButton;
import br.net.mirante.singular.form.wicket.component.SingularValidationButton;
import br.net.mirante.singular.form.wicket.enums.AnnotationMode;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.form.wicket.panel.SingularFormPanel;
import br.net.mirante.singular.persistence.entity.ProcessInstanceEntity;
import br.net.mirante.singular.server.commons.persistence.entity.form.AbstractOldPetitionEntity;
import br.net.mirante.singular.server.commons.wicket.view.template.Content;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.modal.BSModalBorder;
import br.net.mirante.singular.util.wicket.model.IReadOnlyModel;

public abstract class AbstractFormContent extends Content {


    protected final BSContainer<?> modalContainer = new BSContainer<>("modals");
    protected final String typeName;
    private final BSModalBorder closeModal = construirCloseModal();
    protected IModel<String> msgFlowModel = new Model<>();
    protected IModel<String> transitionNameModel = new Model<>();
    protected ViewMode viewMode = ViewMode.EDIT;
    protected AnnotationMode annotationMode = AnnotationMode.NONE;
    protected SingularFormPanel<String> singularFormPanel;
    @Inject
    @Named("formConfigWithDatabase")
    protected SFormConfig<String> singularFormConfig;

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
        return form;
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

    protected void configureCustomButtons(BSContainer<?> buttonContainer, BSContainer<?> modalContainer, ViewMode viewMode, AnnotationMode annotationMode, IModel<? extends SInstance> currentInstance){
        
    }
    
    protected abstract SInstance createInstance(SDocumentFactory documentFactory, RefType refType);
    
    private SingularFormPanel<String> buildSingularBasePanel() {
        singularFormPanel = new SingularFormPanel<String>("singular-panel", singularFormConfig) {

            @Override
            protected SInstance createInstance(SFormConfig<String> singularFormConfig) {
                RefType refType = singularFormConfig.getTypeLoader().loadRefTypeOrException(typeName);

                ProcessInstanceEntity processInstance = getProcessInstance();
                SDocumentFactory extendedFactory = singularFormConfig.getDocumentFactory().extendAddingSetupStep(
                        document -> {
                            document.bindLocalService("processService",AbstractFormContent.ProcessFormService.class,
                                    RefService.of((AbstractFormContent.ProcessFormService)()->processInstance));
                        });
                return AbstractFormContent.this.createInstance(extendedFactory, refType);
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
                try{
                    saveForm(getCurrentInstance());
                    addToastrSuccessMessage("message.success");
                    atualizarContentWorklist(target);
                }catch (HibernateOptimisticLockingFailureException e){
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
                }catch (HibernateOptimisticLockingFailureException e){
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
        closeModal.addButton(BSModalBorder.ButtonStyle.EMPTY, "label.button.cancel", new AjaxButton("cancel-close-btn") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                closeModal.hide(target);
            }
        });
        closeModal.addButton(BSModalBorder.ButtonStyle.DANGER, "label.button.confirm", new AjaxButton("close-btn") {
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
        return $b.visibleIf(()->!hasProcess() && viewMode.isEdition());
    }

    protected Behavior visibleOnlyInAnnotationBehaviour() {
        return $b.visibleIf(annotationMode::editable);
    }

    protected IModel<? extends SInstance> getFormInstance() {
        return singularFormPanel.getRootInstance();
    }
    
    protected abstract ProcessInstanceEntity getProcessInstance();

    protected abstract void saveForm(IModel<? extends SInstance> currentInstance);

    protected abstract IModel<? extends AbstractOldPetitionEntity> getFormModel();

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
