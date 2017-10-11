package org.opensingular.studio.core.view;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.opensingular.form.persistence.FormRespository;
import org.opensingular.lib.commons.util.Loggable;
import org.opensingular.studio.core.definition.StudioDefinition;
import org.opensingular.studio.core.menu.MenuEntry;
import org.opensingular.studio.core.menu.StudioCRUDMenuEntry;
import org.opensingular.studio.core.panel.CrudShell;

import javax.annotation.Nonnull;


public class StudioCRUDContent extends StudioContent implements Loggable {

    private StudioDefinition definition;

    public StudioCRUDContent(String id, MenuEntry currentMenuEntry) {
        super(id, currentMenuEntry);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        Form<Void> form = newStatelessIfEmptyForm();
        form.setMultiPart(true);
        MenuEntry entry = getCurrentMenuEntry();
        if (isStudioItem(entry)) {
            addCrudContent(form, (StudioCRUDMenuEntry) entry);
        }
        else {
            addEmptyContent(form);
        }
        add(form);
    }

    @Nonnull
    private Form<Void> newStatelessIfEmptyForm() {
        return new Form<Void>("form") {
            @Override
            protected boolean getStatelessHint() {
                Component statefullComp = visitChildren(Component.class, (c, v) -> {
                    if (!c.isStateless()) {
                        v.stop(c);
                    }
                });
                return statefullComp == null;
            }
        };
    }

    private void addCrudContent(Form<Void> form, StudioCRUDMenuEntry entry) {
        definition = entry.getStudioDefinition();
        FormRespository respository = definition.getRepository();
        if (respository != null) {
            form.add(new CrudShell("crud", definition));
        }
        else {
            addEmptyContent(form);
        }
    }

    private boolean isStudioItem(MenuEntry entry) {
        return entry instanceof StudioCRUDMenuEntry;
    }

    private void addEmptyContent(Form<Void> form) {
        form.add(new WebMarkupContainer("crud"));
    }
}