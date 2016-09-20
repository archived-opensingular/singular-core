package br.net.mirante.singular.server.commons.wicket.view.form;


import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.server.commons.persistence.entity.form.PetitionEntity;
import br.net.mirante.singular.util.wicket.jquery.JQuery;
import br.net.mirante.singular.util.wicket.modal.BSModalBorder;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

import static br.net.mirante.singular.util.wicket.util.Shortcuts.$b;

public abstract class AbstractFlowConfirmModal<T extends PetitionEntity> implements FlowConfirmModal<T> {

    protected final AbstractFormPage<T> formPage;

    public AbstractFlowConfirmModal(AbstractFormPage<T> formPage) {
        this.formPage = formPage;
    }

    /**
     * @param tn -> transition name
     * @param im -> instance model
     * @param vm -> view mode
     * @param m  -> modal
     * @return the new AjaxButton
     */
    protected FlowConfirmButton<T> newFlowConfirmButton(String tn, IModel<? extends SInstance> im, ViewMode vm, BSModalBorder m) {
        return new FlowConfirmButton<>(tn, "confirm-btn", im, ViewMode.EDIT.equals(vm), formPage, m);
    }

    protected void addDefaultConfirmButton(String tn, IModel<? extends SInstance> im, ViewMode vm, BSModalBorder modal) {
        final FlowConfirmButton<T> confirmButton = newFlowConfirmButton(tn, im, vm, modal);
        confirmButton.add($b.on("click", (c) -> JQuery.$(modal).append(".modal('hide');")));
        modal.addButton(BSModalBorder.ButtonStyle.CONFIRM, "label.button.confirm", confirmButton);
    }

    protected void addDefaultCancelButton(final BSModalBorder modal) {
        modal.addButton(
                BSModalBorder.ButtonStyle.CANCEl,
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