package br.net.mirante.singular.server.commons.wicket.view.form;

import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.util.wicket.modal.BSModalBorder;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;


public class SimpleMessageFlowConfirmModalBuilder implements FlowConfirmModalBuilder {

    private final AbstractFormPage formPage;

    public SimpleMessageFlowConfirmModalBuilder(AbstractFormPage formPage) {
        this.formPage = formPage;
    }

    @Override
    public String getMarkup(String idSuffix) {
        return "<div wicket:id='flow-modal" + idSuffix + "' class='portlet-body form'>\n" + "<div wicket:id='flow-msg'/>\n" + "</div>\n";
    }

    @Override
    public BSModalBorder build(String idSuffix, String tn, IModel<? extends SInstance> im, ViewMode vm) {

        final BSModalBorder modal = new BSModalBorder("flow-modal" + idSuffix, new StringResourceModel("label.button.confirm", formPage, null));

        modal.addButton(
                BSModalBorder.ButtonStyle.EMPTY,
                "label.button.cancel",
                new AjaxButton("cancel-btn") {
                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        modal.hide(target);
                    }
                }
        );

        modal.addButton(
                BSModalBorder.ButtonStyle.DANGER,
                "label.button.confirm",
                newFlowConfirmButton(tn, im, vm, modal)
        );

        modal.add(
                new Label("flow-msg", String.format("Tem certeza que deseja %s ?", tn))
        );

        return modal;

    }

    /**
     * @param tn -> transition name
     * @param im -> instance model
     * @param vm -> view mode
     * @param m  -> modal
     * @return the new AjaxButton
     */
    private FlowConfirmButton newFlowConfirmButton(String tn, IModel<? extends SInstance> im, ViewMode vm, BSModalBorder m) {
        return new FlowConfirmButton(tn, "confirm-btn", im, ViewMode.EDIT.equals(vm), formPage, m);
    }

}