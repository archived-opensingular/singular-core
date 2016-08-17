package br.net.mirante.singular.server.commons.wicket.view.form;


import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.util.wicket.modal.BSModalBorder;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

public abstract class AbstractFlowConfirmModalBuilder implements FlowConfirmModalBuilder {

    protected final AbstractFormPage formPage;

    public AbstractFlowConfirmModalBuilder(AbstractFormPage formPage) {
        this.formPage = formPage;
    }

    /**
     * @param tn -> transition name
     * @param im -> instance model
     * @param vm -> view mode
     * @param m  -> modal
     * @return the new AjaxButton
     */
    protected FlowConfirmButton newFlowConfirmButton(String tn, IModel<? extends SInstance> im, ViewMode vm, BSModalBorder m) {
        return new FlowConfirmButton(tn, "confirm-btn", im, ViewMode.EDIT.equals(vm), formPage, m);
    }

    protected void addDefaultConfirmButton(String tn, IModel<? extends SInstance> im, ViewMode vm, BSModalBorder modal) {
        modal.addButton(
                BSModalBorder.ButtonStyle.DANGER,
                "label.button.confirm",
                newFlowConfirmButton(tn, im, vm, modal)
        );
    }

    protected void addDefaultCancelButton(final BSModalBorder modal) {
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
    }

}