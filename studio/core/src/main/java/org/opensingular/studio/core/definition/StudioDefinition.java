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


import org.apache.wicket.model.IModel;
import org.opensingular.form.SInstance;
import org.opensingular.form.persistence.FormRespository;
import org.opensingular.form.studio.StudioCRUDPermissionStrategy;
import org.opensingular.lib.support.spring.util.ApplicationContextProvider;
import org.opensingular.studio.core.panel.CrudEditContent;
import org.opensingular.studio.core.panel.CrudListContent;
import org.opensingular.studio.core.panel.CrudShellContent;
import org.opensingular.studio.core.panel.CrudShellManager;

import java.io.Serializable;

public interface StudioDefinition extends Serializable {

    Class<? extends FormRespository> getRepositoryClass();

    void configureStudioDataTable(StudioTableDefinition studioDataTable);

    String getTitle();

    default StudioCRUDPermissionStrategy getPermissionStrategy() {
        return StudioCRUDPermissionStrategy.ALL;
    }

    default FormRespository getRepository() {
        return ApplicationContextProvider.get().getBean(getRepositoryClass());
    }

    default CrudShellContent buildStartContent(CrudShellManager shellManager) {
        return buildListContent(shellManager);
    }

    default CrudEditContent buildEditContent(CrudShellManager crudShellManager, CrudShellContent previousContent, IModel<SInstance> instance) {
        return new CrudEditContent(crudShellManager, previousContent, instance);
    }

    default CrudListContent buildListContent(CrudShellManager shellManager) {
        return new CrudListContent(shellManager);
    }


}