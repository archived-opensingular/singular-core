package org.opensingular.singular.form.wicket.mapper.masterdetail;

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

import static org.opensingular.singular.util.wicket.util.Shortcuts.$m;

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