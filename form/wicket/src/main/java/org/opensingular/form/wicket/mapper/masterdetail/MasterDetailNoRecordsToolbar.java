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

package org.opensingular.form.wicket.mapper.masterdetail;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.markup.IMarkupResourceStreamProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;

import static org.opensingular.lib.wicket.util.util.Shortcuts.$m;

class MasterDetailNoRecordsToolbar extends AbstractToolbar implements IMarkupResourceStreamProvider {

    private static final String MARKUP = ""
            + " <wicket:panel>                                                            "
            + "     <tr class='list-detail-empty'>                                                                  "
            + "         <td wicket:id='td'>                                               "
            + "                 <div wicket:id='msg'></div> "
            + "         </td>                                                             "
            + "     </tr>                                                                 "
            + " </wicket:panel>"                                                          ;

    MasterDetailNoRecordsToolbar(DataTable<?, ?> table) {
        super(table);
        final WebMarkupContainer td = new WebMarkupContainer("td");
        add(td);
        td.add(AttributeModifier.replace("colspan", new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                return String.valueOf(table.getColumns().size()).intern();
            }
        }));
        td.add(new Label("msg", $m.ofValue("Nenhum item foi adicionado.")));
    }

    @Override
    public boolean isVisible() {
        return getTable().getRowCount() == 0;
    }

    @Override
    public IResourceStream getMarkupResourceStream(MarkupContainer container, Class<?> containerClass) {
        return new StringResourceStream(MARKUP);
    }

}