package org.opensingular.studio.core.definition;

import org.opensingular.form.studio.StudioCRUDPermissionStrategy;
import org.opensingular.studio.core.panel.CrudListContent;
import org.opensingular.studio.core.panel.CrudShellManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class StudioTableDefinition implements Serializable {

    private LinkedHashMap<String, String> columns = new LinkedHashMap<>();
    private List<CrudListContent.ListAction> actions = new ArrayList<>();

    public StudioTableDefinition(StudioDefinition studioDefinition, CrudShellManager crudShellManager) {
        StudioCRUDPermissionStrategy permissionStrategy = studioDefinition.getPermissionStrategy();
        if (permissionStrategy.canEdit()) {
            actions.add(new CrudListContent.EditAction(crudShellManager));
        }
        if (permissionStrategy.canView()) {
            actions.add(new CrudListContent.ViewAction(crudShellManager));
        }
        if (permissionStrategy.canRemove()) {
            actions.add(new CrudListContent.DeleteAction(studioDefinition, crudShellManager));
        }
    }

    public void add(String columnName, String path) {
        columns.put(columnName, path);
    }

    public void add(CrudListContent.ListAction listAction) {
        actions.add(listAction);
    }

    public LinkedHashMap<String, String> getColumns() {
        return columns;
    }

    public List<CrudListContent.ListAction> getActions() {
        return actions;
    }

}