package org.opensingular.studio.core.panel;

import org.apache.wicket.markup.html.panel.Panel;
import org.opensingular.studio.core.definition.StudioDefinition;

public class CrudShell extends Panel {

    private final CrudShellManager crudShellManager;

    public CrudShell(String id, StudioDefinition studioDefinition) {
        super(id);
        this.crudShellManager = new CrudShellManager(studioDefinition, this);
        setOutputMarkupId(true);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(crudShellManager.getCrudShellContent());
    }
}