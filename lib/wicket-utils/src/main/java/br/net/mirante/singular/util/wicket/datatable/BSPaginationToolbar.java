package br.net.mirante.singular.util.wicket.datatable;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;

import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;

public class BSPaginationToolbar extends AbstractToolbar {

    public BSPaginationToolbar(DataTable<?, ?> table) {
        super(table);

        add(new BSPaginationPanel("pagination", table));
        add($b.addAjaxUpdate(
            new BSItemsPerPageDropDown("itemsPerPage", getTable()),
            (a, c) -> a.add(getTable()))
            .getTargetComponent());
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        setVisible(getTable().getPageCount() > 1);
    }
}
