package org.opensingular.singular.server.commons.wicket.view.form;

import org.opensingular.lib.commons.lambda.IBiConsumer;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;
import org.opensingular.form.context.SFormConfig;
import org.opensingular.form.document.RefType;
import org.opensingular.form.event.SInstanceEventType;
import org.opensingular.form.persistence.FormKey;
import org.opensingular.form.service.IFormService;
import org.opensingular.form.wicket.enums.ViewMode;
import org.opensingular.form.wicket.model.SInstanceRootModel;
import org.opensingular.form.wicket.panel.SingularFormPanel;
import org.opensingular.singular.server.commons.persistence.entity.form.PetitionEntity;
import org.opensingular.singular.server.commons.wicket.builder.HTMLParameters;
import org.opensingular.singular.server.commons.wicket.builder.MarkupCreator;
import org.opensingular.lib.wicket.util.modal.BSModalBorder;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

public class STypeBasedFlowConfirmModal<T extends PetitionEntity> extends AbstractFlowConfirmModal<T> {

    private final SFormConfig<String>              formConfig;
    private final RefType                          refType;
    private final FormKey                          formKey;
    private final IFormService                     formService;
    private final IBiConsumer<SIComposite, String> onCreateInstance;
    private       SInstanceRootModel<SInstance>    instanceModel;
    private       String                           transitionName;
    private       boolean                          dirty;
    private       boolean                          validatePageForm;

    public STypeBasedFlowConfirmModal(AbstractFormPage<T> formPage,
                                      SFormConfig<String> formConfig,
                                      RefType refType,
                                      FormKey formKey,
                                      IFormService formService,
                                      IBiConsumer<SIComposite, String> onCreateInstance, boolean validatePageForm) {
        super(formPage);

        this.formConfig = formConfig;
        this.refType = refType;
        this.formKey = formKey;
        this.formService = formService;
        this.onCreateInstance = onCreateInstance;
        this.dirty = false;
        this.validatePageForm = validatePageForm;
    }

    @Override
    public String getMarkup(String idSuffix) {
        return MarkupCreator.div("flow-modal" + idSuffix, new HTMLParameters().styleClass("portlet-body form"), MarkupCreator.div("singular-form-panel"));
    }


    @Override
    public BSModalBorder init(String idSuffix, String tn, IModel<? extends SInstance> im, ViewMode vm) {
        this.transitionName = tn;
        final BSModalBorder modal = new BSModalBorder("flow-modal" + idSuffix, new StringResourceModel("label.button.confirm", formPage, null));
        addCloseButton(modal);
        addDefaultConfirmButton(tn, im, vm, modal);
        modal.add(buildSingularFormPanel());
        return modal;
    }

    @Override
    protected FlowConfirmButton<T> newFlowConfirmButton(String tn, IModel<? extends SInstance> im, ViewMode vm, BSModalBorder m) {
        return new FlowConfirmButton<>(tn, "confirm-btn", im, validatePageForm && ViewMode.EDIT.equals(vm), formPage, m);
    }

    private void addCloseButton(BSModalBorder modal) {
        modal.addButton(
                BSModalBorder.ButtonStyle.CANCEl,
                Model.of("Fechar"),
                new AjaxButton("cancel-btn") {
                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        modal.hide(target);
                    }
                }
        );
    }

    private SingularFormPanel<String> buildSingularFormPanel() {
        return new SingularFormPanel<String>("singular-form-panel", formConfig, true) {
            @Override
            protected SInstance createInstance(SFormConfig singularFormConfig) {
                if (instanceModel == null) {
                    instanceModel = new SInstanceRootModel<>();
                    if (formKey != null) {
                        instanceModel.setObject(formService.loadSInstance(formKey, refType, singularFormConfig.getDocumentFactory()));
                    } else {
                        instanceModel.setObject(singularFormConfig.getDocumentFactory().createInstance(refType));
                    }
                }
                if (onCreateInstance != null) {
                    onCreateInstance.accept((SIComposite) instanceModel.getObject(), transitionName);
                }
                appendDirtyListener(instanceModel.getObject());
                return instanceModel.getObject();
            }
        };
    }

    private void appendDirtyListener(SInstance instance) {
        instance.getDocument().getInstanceListeners().add(SInstanceEventType.VALUE_CHANGED, evt -> dirty = true);
    }

    public SInstanceRootModel<SInstance> getInstanceModel() {
        return instanceModel;
    }

    public boolean isDirty() {
        return dirty;
    }

    public STypeBasedFlowConfirmModal setDirty(boolean dirty) {
        this.dirty = dirty;
        return this;
    }
}