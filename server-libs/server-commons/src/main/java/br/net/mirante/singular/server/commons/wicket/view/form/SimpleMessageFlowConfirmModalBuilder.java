package br.net.mirante.singular.server.commons.wicket.view.form;

import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.util.wicket.modal.BSModalBorder;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;


public class SimpleMessageFlowConfirmModalBuilder extends AbstractFlowConfirmModalBuilder {

    public SimpleMessageFlowConfirmModalBuilder(AbstractFormPage formPage) {
        super(formPage);
    }

    @Override
    public String getMarkup(String idSuffix) {
        return "<div wicket:id='flow-modal" + idSuffix + "' class='portlet-body form'>\n" + "<div wicket:id='flow-msg'/>\n" + "</div>\n";
    }

    @Override
    public BSModalBorder build(String idSuffix, String tn, IModel<? extends SInstance> im, ViewMode vm) {
        final BSModalBorder modal = new BSModalBorder("flow-modal" + idSuffix, new StringResourceModel("label.button.confirm", formPage, null));
        addDefaultCancelButton(modal);
        addDefaultConfirmButton(tn, im, vm, modal);
        modal.add(new Label("flow-msg", String.format("Tem certeza que deseja %s ?", tn)));
        return modal;
    }

}