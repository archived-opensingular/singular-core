/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.studio.core.definition;

import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.opensingular.form.SInstance;
import org.opensingular.form.studio.StudioCRUDPermissionStrategy;
import org.opensingular.studio.core.panel.CrudListContent;
import org.opensingular.studio.core.panel.CrudShellManager;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

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

    /**
     * Override this method to supply a customized version of data provider.
     * @return
     * a null return is ignored and the default provider is used instead.
     */
    @Nullable
    public StudioTableDataProvider getDataProvider(){
        return null;
    }

    public LinkedHashMap<String, String> getColumns() {
        return columns;
    }

    public List<CrudListContent.ListAction> getActions() {
        return actions;
    }

}