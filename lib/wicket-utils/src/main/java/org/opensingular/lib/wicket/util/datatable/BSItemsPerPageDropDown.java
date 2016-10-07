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

package org.opensingular.lib.wicket.util.datatable;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.navigation.paging.IPageableItems;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.opensingular.lib.wicket.util.util.WicketUtils.$m;

public class BSItemsPerPageDropDown extends DropDownChoice<Long> {

    private final IPageableItems pageableComponent;

    public <P extends Component & IPageableItems> BSItemsPerPageDropDown(String id, P pageableComponent) {
        super(id);
        this.pageableComponent = pageableComponent;
        setModel($m.getSet(() -> this.getPageable().getItemsPerPage(), arg -> this.getPageable().setItemsPerPage(arg)));
    }

    @SuppressWarnings("unchecked")
    public <P extends Component & IPageableItems> P getPageable() {
        return (P) pageableComponent;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        setChoices(this.getItemsPerPageOptions());
    }

    protected List<Long> getItemsPerPageOptions() {
        Set<Long> options = new TreeSet<>();
        options.add(getPageable().getItemsPerPage());
        options.add(10L);
        options.add(30L);
        options.add(50L);
        options.add(100L);
        return new ArrayList<>(options);
    }
}
