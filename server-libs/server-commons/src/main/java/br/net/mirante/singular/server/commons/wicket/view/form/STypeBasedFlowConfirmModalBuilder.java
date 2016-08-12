package br.net.mirante.singular.server.commons.wicket.view.form;

import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.context.SFormConfig;
import br.net.mirante.singular.form.document.RefType;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.form.wicket.panel.SingularFormPanel;
import br.net.mirante.singular.util.wicket.modal.BSModalBorder;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;


public class STypeBasedFlowConfirmModalBuilder extends AbstractFlowConfirmModalBuilder {

    private final SFormConfig<String>       formConfig;
    private final RefType                   refType;
    private       SingularFormPanel<String> singularFormPanel;

    public STypeBasedFlowConfirmModalBuilder(AbstractFormPage formPage, SFormConfig<String> formConfig, RefType refType) {
        super(formPage);
        this.formConfig = formConfig;
        this.refType = refType;
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
        modal.add(singularFormPanel = buildSingularFormPanel());
        return modal;
    }

    private SingularFormPanel<String> buildSingularFormPanel() {
        return new SingularFormPanel<String>("singular-form-panel", formConfig) {
            @Override
            protected SInstance createInstance(SFormConfig singularFormConfig) {
                return singularFormConfig.getDocumentFactory().createInstance(refType);
            }
        };
    }

}