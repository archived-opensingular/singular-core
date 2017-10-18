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