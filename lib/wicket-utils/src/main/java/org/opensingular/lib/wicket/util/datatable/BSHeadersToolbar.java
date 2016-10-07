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

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.sort.AjaxFallbackOrderByLink;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByLink;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeadersToolbar;
import org.apache.wicket.markup.html.WebMarkupContainer;

import static org.opensingular.lib.wicket.util.util.WicketUtils.$b;

public class BSHeadersToolbar<S> extends HeadersToolbar<S> {

    public <T> BSHeadersToolbar(DataTable<T, S> table, ISortStateLocator<S> stateLocator) {
        super(table, stateLocator);
    }

    @Override
    protected WebMarkupContainer newSortableHeader(String headerId, S property, ISortStateLocator<S> locator) {
        return new BSOrderByBorder(headerId, property, locator);
    }

    protected void onSortChanged(AjaxRequestTarget target) {
        if (target != null) {
            target.add(getTable());
        }
    }

    private final class BSOrderByBorder extends OrderByBorder<S> {

        private BSOrderByBorder(String id, S property, ISortStateLocator<S> stateLocator) {
            super(id, property, stateLocator);
        }

        @Override
        protected void onInitialize() {
            super.onInitialize();
            add($b.classAppender("default-cursor"));
        }

        @Override
        protected void onSortChanged() {
            getTable().setCurrentPage(0);
        }

        @Override
        protected OrderByLink<S> newOrderByLink(String id, S property, ISortStateLocator<S> stateLocator) {
            return new BSAjaxOrderByLink(id, property, stateLocator);
        }

        private final class BSAjaxOrderByLink extends AjaxFallbackOrderByLink<S> {
            private BSAjaxOrderByLink(String id, S sortProperty, ISortStateLocator<S> stateLocator) {
                super(id, sortProperty, stateLocator);
            }
            @Override
            protected void onSortChanged() {
                BSOrderByBorder.this.onSortChanged();
            }
            @Override
            public void onClick(AjaxRequestTarget target) {
                BSHeadersToolbar.this.onSortChanged(target);
            }
        }
    }
}
