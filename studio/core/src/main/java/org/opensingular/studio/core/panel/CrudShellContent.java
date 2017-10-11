package org.opensingular.studio.core.panel;

import org.apache.wicket.markup.html.panel.Panel;
import org.opensingular.form.persistence.FormRespository;
import org.opensingular.lib.support.spring.util.ApplicationContextProvider;
import org.opensingular.studio.core.definition.StudioDefinition;

public abstract class CrudShellContent extends Panel {

    private final CrudShellManager crudShellManager;

    public CrudShellContent(CrudShellManager crudShellManager) {
        super("crudShellContent");
        this.crudShellManager = crudShellManager;
    }

    public CrudShellManager getCrudShellManager() {
        return crudShellManager;
    }

    public StudioDefinition getDefinition() {
        return getCrudShellManager().getStudioDefinition();
    }

    protected FormRespository getFormPersistence() {
        return ApplicationContextProvider.get().getBean(getDefinition().getRepositoryClass());
    }

}