package br.net.mirante.singular.server.commons.wicket.view.form;

import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.context.SFormConfig;
import br.net.mirante.singular.form.document.RefType;
import br.net.mirante.singular.form.persistence.FormKey;
import br.net.mirante.singular.form.service.IFormService;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.form.wicket.model.SInstanceRootModel;
import br.net.mirante.singular.form.wicket.panel.SingularFormPanel;
import br.net.mirante.singular.server.commons.persistence.entity.form.PetitionEntity;
import br.net.mirante.singular.util.wicket.modal.BSModalBorder;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;


public class STypeBasedFlowConfirmModalBuilder<T extends PetitionEntity> extends AbstractFlowConfirmModalBuilder<T> {

    private final SFormConfig<String>           formConfig;
    private final RefType                       refType;
    private final FormKey                       formKey;
    private final IFormService                  formService;
    private       SInstanceRootModel<SInstance> instanceModel;

    public STypeBasedFlowConfirmModalBuilder(AbstractFormPage<T> formPage,
                                             SFormConfig<String> formConfig,
                                             RefType refType,
                                             FormKey formKey,
                                             IFormService formService) {
        super(formPage);
        this.formConfig = formConfig;
        this.refType = refType;
        this.formKey = formKey;
        this.formService = formService;
    }

    @Override
    public String getMarkup(String idSuffix) {
        return "<div wicket:id='flow-modal" + idSuffix + "' class='portlet-body form'>\n" + "<div wicket:id='singular-form-panel'/>\n" + "</div>\n";
    }

    @Override
    public BSModalBorder build(String idSuffix, String tn, IModel<? extends SInstance> im, ViewMode vm) {
        final BSModalBorder modal = new BSModalBorder("flow-modal" + idSuffix, new StringResourceModel("label.button.confirm", formPage, null));
        addDefaultCancelButton(modal);
        addDefaultConfirmButton(tn, im, vm, modal);
        modal.add(buildSingularFormPanel());
        return modal;
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
                    return instanceModel.getObject();
                }
                return instanceModel.getObject();
            }
        };
    }

    public SInstanceRootModel<SInstance> getInstanceModel() {
        return instanceModel;
    }
}