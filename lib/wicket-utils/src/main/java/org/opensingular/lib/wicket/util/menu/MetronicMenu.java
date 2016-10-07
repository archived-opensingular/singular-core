/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.lib.wicket.util.menu;

import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

import java.util.ArrayList;
import java.util.List;

import static org.opensingular.lib.wicket.util.util.WicketUtils.$b;

public class MetronicMenu extends Panel {

    public List<AbstractMenuItem> itens = new ArrayList<>();

    public MetronicMenu(String id) {
        super(id);
    }

    public void addItem(AbstractMenuItem item) {
        if(itens.isEmpty()){
            item.add($b.classAppender("start"));
        }
        itens.add(item);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new ListView<AbstractMenuItem>("itens", itens) {
            @Override
            protected void populateItem(ListItem<AbstractMenuItem> item) {
                item.add(item.getModelObject());
            }
        });
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        itens.forEach(AbstractMenuItem::configureActiveItem);
    }

}
