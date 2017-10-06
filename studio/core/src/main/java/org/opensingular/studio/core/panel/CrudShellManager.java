package org.opensingular.studio.core.panel;

import de.alpharogroup.wicket.js.addon.toastr.ToastrType;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.opensingular.lib.wicket.util.toastr.ToastrHelper;
import org.opensingular.studio.core.definition.StudioDefinition;

import java.io.Serializable;

public class CrudShellManager implements Serializable {

    private StudioDefinition studioDefinition;
    private CrudShellContent crudShellContent;
    private CrudShell crudShell;

    public CrudShellManager(StudioDefinition studioDefinition, CrudShell crudShell) {
        this.studioDefinition = studioDefinition;
        this.crudShell = crudShell;
        this.crudShellContent = new CrudListContent(this);
    }

    public void replaceContent(AjaxRequestTarget ajaxRequestTarget, CrudShellContent newContent) {
        crudShellContent = (CrudShellContent) crudShellContent.replaceWith(newContent);
        ajaxRequestTarget.add(crudShell);
    }

    public StudioDefinition getStudioDefinition() {
        return studioDefinition;
    }

    public CrudShellContent getCrudShellContent() {
        return crudShellContent;
    }

    public void addToastrMessage(ToastrType type, String message) {
        new ToastrHelper(crudShellContent).addToastrMessage(type, message);
    }

    public void update(AjaxRequestTarget ajaxRequestTarget) {
        ajaxRequestTarget.add(crudShell);
    }
}