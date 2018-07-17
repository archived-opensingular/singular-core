package org.opensingular.form.wicket.mapper.buttons;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.model.IModel;
import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.wicket.mapper.MapperCommons;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSContainer;
import org.opensingular.lib.wicket.util.resource.DefaultIcons;
import org.opensingular.lib.wicket.util.scripts.Scripts;

import static org.opensingular.lib.wicket.util.util.WicketUtils.$b;

public class AddButton extends AjaxLink<String> {

    private final IModel<SIList<SInstance>> listModel;

    private Component component;

    public AddButton(String id, Component component, IModel<SIList<SInstance>> mList) {
        super(id);
        this.component = component;
        listModel = mList;
        add($b.attr("title", "Adicionar Linha"));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
    }

    @Override
    public void onClick(AjaxRequestTarget target) {
        final SIList<SInstance> list = listModel.getObject();
        if (list.getType().getMaximumSize() != null && list.getType().getMaximumSize() == list.size()) {
            target.appendJavaScript(";bootbox.alert('A quantidade máxima de valores foi atingida.');");
            target.appendJavaScript(Scripts.multipleModalBackDrop());
        } else {
            list.addNew();
            target.add(component);
            target.focusComponent(this);
        }
    }

    public AddButton createAddButton(BSContainer<?> cell, boolean footer) {
        cell.newTemplateTag(t -> ""
                + "<button"
                + " wicket:id='_add'"
                + " class='btn btn-sm " + (footer ? "" : "pull-right") + "'"
                + " style='" + MapperCommons.BUTTON_STYLE + ";"
                + (footer ? "margin-top:3px;margin-right:7px;" : "") + "'><i style='" + MapperCommons.ICON_STYLE + "' class='" + DefaultIcons.PLUS + "'></i>"
                + "</button>").add(this);
        return this;
    }
}
