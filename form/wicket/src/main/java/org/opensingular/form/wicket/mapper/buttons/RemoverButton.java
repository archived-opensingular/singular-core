package org.opensingular.form.wicket.mapper.buttons;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.opensingular.form.SInstance;
import org.opensingular.form.view.list.ButtonAction;
import org.opensingular.form.wicket.mapper.MapperCommons;
import org.opensingular.form.wicket.mapper.components.ConfirmationModal;
import org.opensingular.form.wicket.util.WicketFormProcessing;
import org.opensingular.form.wicket.util.WicketFormUtils;
import org.opensingular.lib.commons.ui.Icon;
import org.opensingular.lib.wicket.util.ajax.ActionAjaxButton;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSContainer;
import org.opensingular.lib.wicket.util.jquery.JQuery;
import org.opensingular.lib.wicket.util.resource.DefaultIcons;

import javax.annotation.Nonnull;
import java.util.Optional;

import static org.opensingular.lib.wicket.util.util.WicketUtils.$b;

public class RemoverButton extends ActionAjaxButton {

    private final ElementsView elementsView;
    private final Item<SInstance> item;
    private final ConfirmationModal confirmationModal;
    private final Icon deleteIcon;

    public RemoverButton(String id, Form<?> form, ElementsView elementsView, Item<SInstance> item, ConfirmationModal confirmationModal, @Nonnull ButtonAction deleteButton) {
        super(id, form);
        this.setOutputMarkupId(true);
        this.setDefaultFormProcessing(false);
        this.elementsView = elementsView;
        this.item = item;
        deleteIcon = Optional.ofNullable(deleteButton.getIcon()).orElse(DefaultIcons.REMOVE);
        add($b.attr("title", Optional.ofNullable(deleteButton.getHint()).orElse("Remover Linha")));
        add($b.onConfigure(c -> c.setVisible(deleteButton.isEnabled(item.getModelObject()))));

        this.confirmationModal = confirmationModal;
    }

    @Override
    protected void onAction(AjaxRequestTarget target, Form<?> form) {
        target.add(WicketFormUtils.findUpdatableComponentInHierarchy(confirmationModal));
        confirmationModal.show(target, t -> removeItem(t, form));
    }

    protected void removeItem(AjaxRequestTarget target, Form<?> form) {
        elementsView.removeItem(target, item);
        target.appendJavaScript(JQuery.$(this).append(".prop('disabled',true);"));
        if (elementsView.getModelObject().isEmpty()) {
            target.add(this.getForm());
        }
        WicketFormProcessing.onFieldProcess(form, target, elementsView.getModel());
        behaviorAfterRemoveItem(target);
    }

    protected void behaviorAfterRemoveItem(AjaxRequestTarget target) {
    }

    public RemoverButton createRemoverButton(BSContainer<?> cell) {


        cell.newTemplateTag(tp -> ""
                + "<button wicket:id='_remover_' class='singular-remove-btn'>"
                + "     <i "
                + "      style='" + MapperCommons.ICON_STYLE + " 'class='" + deleteIcon + "' />"
                + "</button>")
                .add(this);
        return this;
    }

}
