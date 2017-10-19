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