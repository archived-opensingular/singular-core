package br.net.mirante.singular.server.commons.wicket.view.form;

import br.net.mirante.singular.commons.lambda.IBiConsumer;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.context.SFormConfig;
import br.net.mirante.singular.form.document.RefType;
import br.net.mirante.singular.form.persistence.FormKey;
import br.net.mirante.singular.form.service.IFormService;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.form.wicket.model.SInstanceRootModel;
import br.net.mirante.singular.form.wicket.panel.SingularFormPanel;
import br.net.mirante.singular.server.commons.persistence.entity.form.PetitionEntity;
import br.net.mirante.singular.server.commons.wicket.builder.HTMLParameters;
import br.net.mirante.singular.server.commons.wicket.builder.MarkupCreator;
import br.net.mirante.singular.util.wicket.modal.BSModalBorder;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;

public class STypeBasedFlowConfirmModal<T extends PetitionEntity> extends AbstractFlowConfirmModal<T> {

    private final SFormConfig<String>              formConfig;
    private final RefType                          refType;
    private final FormKey                          formKey;
    private final IFormService                     formService;
    private final IBiConsumer<SIComposite, String> onCreateInstance;
    private       SInstanceRootModel<SInstance>    instanceModel;
    private       String                           transitionName;

    public STypeBasedFlowConfirmModal(AbstractFormPage<T> formPage,
                                      SFormConfig<String> formConfig,
                                      RefType refType,
                                      FormKey formKey,
                                      IFormService formService,
                                      IBiConsumer<SIComposite, String> onCreateInstance) {
        super(formPage);

        this.formConfig = formConfig;
        this.refType = refType;
        this.formKey = formKey;
        this.formService = formService;
        this.onCreateInstance = onCreateInstance;
    }

    @Override
    public String getMarkup(String idSuffix) {
        return MarkupCreator.div("flow-modal" + idSuffix, new HTMLParameters().styleClass("portlet-body form"), MarkupCreator.div("singular-form-panel"));
    }

    @Override
    public BSModalBorder init(String idSuffix, String tn, IModel<? extends SInstance> im, ViewMode vm) {
        this.transitionName = tn;
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
                }
                if (onCreateInstance != null) {
                    onCreateInstance.accept((SIComposite) instanceModel.getObject(), transitionName);
                }
                return instanceModel.getObject();
            }
        };
    }

    public SInstanceRootModel<SInstance> getInstanceModel() {
        return instanceModel;
    }

}