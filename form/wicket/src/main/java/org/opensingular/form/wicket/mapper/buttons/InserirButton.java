package org.opensingular.form.wicket.mapper.buttons;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.opensingular.form.SInstance;
import org.opensingular.form.wicket.mapper.MapperCommons;
import org.opensingular.lib.wicket.util.ajax.ActionAjaxButton;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSContainer;
import org.opensingular.lib.wicket.util.resource.DefaultIcons;

import static org.opensingular.lib.wicket.util.util.WicketUtils.$b;

public class InserirButton extends ActionAjaxButton {
    private final Item<SInstance> item;
    private final ElementsView elementsView;

    public InserirButton(String id, ElementsView elementsView, Form<?> form, Item<SInstance> item) {
        super(id, form);
        this.setDefaultFormProcessing(false);
        this.elementsView = elementsView;
        this.item = item;
        add($b.attr("title", "Nova Linha"));
    }

    @Override
    protected void onAction(AjaxRequestTarget target, Form<?> form) {
        elementsView.insertItem(target, item.getIndex());
        target.focusComponent(this);
    }

    public InserirButton createInserirButton(BSContainer<?> cell) {
        cell.newTemplateTag(tp -> ""
                        + "<button"
                        + " wicket:id='_inserir_'"
                        + " class='btn btn-success btn-sm'"
                        + " style='" + MapperCommons.BUTTON_STYLE + ";margin-top:3px;'><i style='" + MapperCommons.ICON_STYLE + "' class='" + DefaultIcons.PLUS + "'></i>"
                        + "</button>")
                .add(this);
        return this;
    }
}
