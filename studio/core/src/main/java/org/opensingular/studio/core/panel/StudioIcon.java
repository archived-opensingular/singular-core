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

import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.opensingular.lib.commons.ui.Icon;

import java.util.HashSet;
import java.util.Set;

public class StudioIcon extends WebMarkupContainer {

    private final IModel<Icon> iconModel;

    public StudioIcon(String id, IModel<Icon> iconModel) {
        super(id);
        this.iconModel = iconModel;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        add(new ClassAttributeModifier() {
            @Override
            protected Set<String> update(Set<String> oldClasses) {
                Icon iconModelObject = iconModel.getObject();
                if (iconModelObject != null) {
                    Set<String> newClasses = new HashSet<>();
                    newClasses.add(iconModelObject.getCssClass());
                    return newClasses;
                }
                return oldClasses;
            }
        });
    }

}
