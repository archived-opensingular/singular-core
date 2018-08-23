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

import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import static org.opensingular.lib.wicket.util.util.WicketUtils.$b;

public class BSPaginationToolbar extends AbstractToolbar {

    private WebMarkupContainer paginator;
    private WebMarkupContainer itensPerPageSelector;
    private WebMarkupContainer countContainer;
    private BSPaginationPanel pagination;
    private IModel<String> counterLabelModel = new Model<>();

    private Long initialRowsPerPage;

    public BSPaginationToolbar(DataTable<?, ?> table) {
        super(table);
        paginator = new WebMarkupContainer("paginator");
        countContainer = new WebMarkupContainer("countContainer");
        countContainer.add(new Label("countLabel", counterLabelModel));
        add(countContainer);
        pagination = newPagination("pagination", table);
        add(paginator);
        paginator.add(pagination);
        itensPerPageSelector = $b.addAjaxUpdate(
                new BSItemsPerPageDropDown("itemsPerPage", getTable()),
                (a, c) -> a.add(getTable()))
                .getTargetComponent();
        add(itensPerPageSelector);
    }

    protected BSPaginationPanel newPagination(String id, DataTable<?, ?> table) {
        return new BSPaginationPanel(id, table);
    }

    private Long getInitialRowsPerPage() {
        if (initialRowsPerPage == null) {
            initialRowsPerPage = this.getTable().getItemsPerPage();
        }
        return initialRowsPerPage;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        itensPerPageSelector.setVisible(getTable().getItemCount() > getInitialRowsPerPage());
        paginator.setVisible(getTable().getPageCount() > 1);

        /* if at least one control is visible, the toolbar must be visible. if none is visible there is no need for the toolbar.*/
        boolean toolbarVisible = getTable().getPageCount() > 1;
        toolbarVisible |= itensPerPageSelector.isVisible();
        toolbarVisible |= paginator.isVisible();

        countContainer.setVisible(toolbarVisible);
        if (countContainer.isVisible()) {
            String messageCount = String.format("Exibindo %d a %d de %d registros", getNumberOfFirstElement(), getNumberOfLastElement(), getTable().getItemCount());
            counterLabelModel.setObject(messageCount);
        }
        this.setVisible(toolbarVisible);
    }

    /**
     * The first element of the page.
     * Example: 15 Itens per page.
     * In page 1 will have 15 itens - 15 itens per row = 0 + 1 = 1.
     * In page 2 will have 30 itens - 15 itens per row = 0 + 1 = 16.
     *
     * @return The first element of the page.
     */
    private long getNumberOfFirstElement() {
        return ((getMaxNumberElementsPerPage()) - this.getTable().getItemsPerPage()) + 1;
    }

    /**
     * The last element of the page.
     * Example: 15 Itens per page. The total of itens is 21
     * In page 1 will have 15 itens - 21 <=0 = TRUE return 15.
     * In page 2 will have 30 itens - 21 <=0 = FALSE return 21.
     *
     * @return The last element of the page.
     */
    private long getNumberOfLastElement() {
        return getMaxNumberElementsPerPage() - getTable().getItemCount() <= 0 ? getMaxNumberElementsPerPage() : getTable().getItemCount();
    }

    /**
     * Return the number of elements in the page.
     * Example: 15 Itens per page.
     * In page 1 will have 15 itens.
     * In page 2 will have 30 itens.
     * In page 3 will have 45 itens.
     *
     * @return number max of elements per page.
     */
    private long getMaxNumberElementsPerPage() {
        return (pagination.getPageable().getCurrentPage() + 1) * this.getTable().getItemsPerPage();
    }
}
