package org.opensingular.form.wicket.mapper.buttons;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.opensingular.form.SInstance;
import org.opensingular.form.view.AbstractSViewListWithControls;
import org.opensingular.form.wicket.mapper.MapperCommons;
import org.opensingular.form.wicket.mapper.components.ConfirmationModal;
import org.opensingular.form.wicket.util.WicketFormUtils;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.lib.wicket.util.ajax.ActionAjaxButton;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSContainer;
import org.opensingular.lib.wicket.util.jquery.JQuery;
import org.opensingular.lib.wicket.util.resource.DefaultIcons;

import static org.opensingular.lib.wicket.util.util.WicketUtils.$b;

public class RemoverButton extends ActionAjaxButton {

    private final ElementsView elementsView;
    private final Item<SInstance> item;
    private final ConfirmationModal confirmationModal;

    public RemoverButton(String id, Form<?> form, ElementsView elementsView, Item<SInstance> item, ConfirmationModal confirmationModal) {
        super(id, form);
        this.setOutputMarkupId(true);
        this.setDefaultFormProcessing(false);
        this.elementsView = elementsView;
        this.item = item;
        add($b.attr("title", "Remover Linha"));

        this.confirmationModal = confirmationModal;
    }

    @Override
    protected void onAction(AjaxRequestTarget target, Form<?> form) {
        target.add(WicketFormUtils.findUpdatableComponentInHierarchy(confirmationModal));
        confirmationModal.show(target, this::removeItem);
    }

    protected void removeItem(AjaxRequestTarget target) {
        elementsView.removeItem(target, item);
        target.appendJavaScript(JQuery.$(this).append(".prop('disabled',true);"));
        if (elementsView.getModelObject().isEmpty()) {
            target.add(this.getForm());
        }
        behaviorAfterRemoveItem(target);
    }

    protected void behaviorAfterRemoveItem(AjaxRequestTarget target) { }

    public RemoverButton createRemoverButton(BSContainer<?> cell, ISupplier<? extends AbstractSViewListWithControls> viewListByTable) {

        cell.newTemplateTag(tp -> ""
                        + "<button wicket:id='_remover_' class='singular-remove-btn'>"
                        + "     <i "
                        + "      style='" + MapperCommons.ICON_STYLE + " 'class='" + DefaultIcons.REMOVE + "' />"
                        + "</button>")
                .add(this);

        if (viewListByTable.get() != null) {
            add($b.onConfigure(c -> c.setVisible(viewListByTable.get().isDeleteEnabled(item.getModelObject()))));
        }
        return this;
    }

}
